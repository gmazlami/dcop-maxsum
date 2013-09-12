/*
 *  @author Robin Hafen
 *
 *  Copyright 2013 University of Zurich
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.signalcollect.dcop.evaluation.candidates

import com.signalcollect.DataGraphVertex
import scala.util.Random
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.Vertex


// Takes vertex id, constraints for the vertex with that id and the domain of the variable represented by the vertex.
trait VertexBuilder
  extends Function2[Int, Array[Int], Vertex[Any, _]]
  with Serializable {

  def apply(id: Int, domain: Array[Int]): Vertex[Any, _]
}


/*
 * The abstract base class for DCOP vertices.
 * Three methods have to be implemented by concrete sub classes.
 * Many convenience methods are implemented on top of these abstract methods.
 */
abstract class ConstrainedVertex[Id, State](
    newId: Id,
    initialState: State,
    val domain: Array[State])
  extends DataGraphVertex[Id, State](newId, initialState)
  with ProspectiveStates[State]
  with TargetFunction[State]
  with DecisionRule[State]
  with AdjustmentSchedule {

  var stepCounter = 0


  // Abstract
  def satisfiesConstraintsWith(newState: State): Boolean

  def numberSatisfiedWith(newState: State): Int

  def bestPossibleUtility: Double

  // Information about the vertex' state in the computation
  def allConstraintsSatisfied: Boolean = {
    if (!lastSignalState.isDefined) false
    else satisfiesConstraintsWith(lastSignalState.get)
  }

  def currentUtility: Double = utility(state)

  def currentNumberSatisfied: Int = numberSatisfiedWith(state)

  def neighbourIds: Iterable[Any] = outgoingEdges.values map {_.targetId}

  def stateHasChanged: Boolean = lastSignalState match {
    case Some(oldState) if oldState != state => true
    case None => true // In the beginning there is no old state. Treat this scenario as if the state had changed.
    case otherwise => false
  }

  
  /*
   * Return a map of vertexId => most recent signal recieved.
   */
  def getNeighbourConfigs: Map[Any, Signal] = {
    val signals: Map[Any, Signal] = mostRecentSignalMap.toMap // convert to immutable
    signals map { case (edgeId, signal) => outgoingEdges(edgeId).targetId -> signal}
  }

  
  def existsBetterStateUtility: Boolean = {
    !(domain map utility filter { _ > currentUtility }).isEmpty
  }

  def getMostRecentSignalMap = mostRecentSignalMap.toMap

  def constraintsCount: Int = edgeCount
}


/*
 * The base class for all vertices used in this thesis.
 * This class includes the default implementations of collect.
 */
abstract class ColorConstrainedVertex[Id, State](
    newId: Id,
    initialState: State,
    newDomain: Array[State])
    extends ConstrainedVertex[Id, State](newId, initialState, newDomain) {

  def collect = {

    stepCounter += 1

    // Hook for certain algorithms that need to modify their state
    // For example DSAN needs to lower its temperature or FP needs to
    // update its neighbourFrequencies.
    // Default is to to nothing here.
    doBeforeCollect

    // If the algorithm should even bother to compute a new state
    val stateToReturn = if (shouldComputeNewState) {
      val newState = chooseNewState()
      newState
    } else {
      state
    }

    stateToReturn
  }

  // A hook to use if some procedures have to be executed before a collect step.
  def doBeforeCollect { () }

  def chooseNewState: () => State = { () =>
    // Determine which values in the domain should be considered
    val prospStates: Seq[State] = prospectiveStates(domain.toSet)

    // Calculate the scores for each of these values.
    val evaluatedStates = evaluateAll(prospStates.toSeq)

    // Choose the score according to a certain decision rule.
    val evaluatedCurrentState = evaluate(state)
    val chosenState = decisionRule(evaluatedCurrentState)(evaluatedStates)

    // Change the vertex' state to the newly chosen one.
    chosenState
  }
  
  def satisfiesConstraintsWith(newState: State): Boolean = {
    // TODO: This map is empty in the beginning which wrongly means that the constraints are satisfied
    if (getNeighbourConfigs.isEmpty) false
    else (getNeighbourConfigs filter { _._2 == newState }).isEmpty
  }

  // Default utility for graph coloring
  override def utility: State => Utility = st => numberSatisfiedWith(st).toDouble

  def numberSatisfiedWith(newState: State): Int = {
    (getNeighbourConfigs filter { _._2 != newState }).size
  }

  def bestPossibleUtility: Double = constraintsCount.toDouble

  // Only signal if the all constraints are satisfied and the state
  // hasn't changed for one computation step
  override def scoreSignal: Double = {
    if (allConstraintsSatisfied && !stateHasChanged) 0
    else 1
  }
}
