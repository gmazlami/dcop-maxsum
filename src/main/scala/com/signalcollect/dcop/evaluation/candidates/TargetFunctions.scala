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
 *
 */


package com.signalcollect.dcop.evaluation.candidates

import com.signalcollect.dcop.evaluation.candidates


/**
 * The basic type of a TargetFunction-Mixin
 * utility and evaluate are seperate functions here, since some algorithms need
 * to override evaluate. See the main thesis paper (Chapman), P.18 PDF.
 * u_i(s_i, s_{-1}) is not always equal to the target function.
 * @tparam State A vertex' state
 */
trait TargetFunction[State] {
  // The basic measure of 'goodness' for given state
  def utility: State => Utility
  // For most algorithms, evaluate is actually the same as utility
  // Some algorithms, like FP, weigh the utilities by some additional factor
  def evaluate: State => (State, Payoff) = st => (st, utility(st))
  // In case this needs to be overridden by whatever reason
  def evaluateAll: Seq[State] => Seq[(State, Payoff)] = { states =>
    val evaluatedStates = states map evaluate
    //println(evaluatedStates)
    evaluatedStates
  }
}


/**
 * This type is the default implementation for TargetFunction and exists solely
 * for the purpose of a more consistent naming scheme.
 * @tparam State A vertex' state
 */
trait MapUtilityTarget[State] extends TargetFunction[State]


/**
 * This is simply a version of MapUtility where the DELTA-Utility is computed.
 * For most purposes it probably wont make a difference which one is used.
 * Note however, that this target function can produce negative payoffs
 * @tparam State A vertex' state
 */
trait DeltaUtilityTarget[State] extends TargetFunction[State] {
  def state: State // the current state
  override def evaluate: State => (State, Payoff) = st => (st, utility(st) - utility(state))
}


/**
 * ExpectedPayoff
 * The payoff for changing to a certain state depends on the historic frequencies of neighbouring states.
 * Vertex i's payoff of acquiring state si at time t is given by the SUM over all neighbours of their
 * probability not to be in state si times the utility the vertex would gain with state si.
 */
trait ExpectedPayoffTarget[State] extends TargetFunction[State] {
  // members needed from vertex
  val domain: Array[State]
  val id: Any
  def getMostRecentSignalMap: Map[Any, State]
  def neighbourIds: Iterable[Any]

  // Initialized to null because when the vertex is constructed, the adjacent edges are not known
  // As soon as the first collect operation gehts executed, the histories object will be initialized.
  var neighbourHistories: NeighbourHistories[State] = null

  override def evaluateAll = {
    if (neighbourHistories == null) {
      neighbourHistories = new NeighbourHistories(neighbourIds, domain)
    }
    neighbourHistories.updateFrequencies(getMostRecentSignalMap)

    super.evaluateAll
  }

  /*
   * For every neighbour calculate how likely it is NOT that he is in this state (st)
   * and multiply it by the raw utility. (Expected value of payoff)
  */
  override def evaluate: State => (State, Double) = { st =>

    val individualPayoffs = for (neighbourId <- neighbourIds) yield {
      // the utility of this candidate state times the probability that this neighbour isn't in this state
      1 - neighbourHistories.getStateFrequency(neighbourId, st).getOrElse(0.0)
    }

    val expectedPayoff = individualPayoffs.sum

    (st, expectedPayoff)
  }
}


/**
 * FictiousPlay target function.
 * The utility of a state is weighed by how likely it is that no neighbour is in this state.
 * @tparam State A vertex' state
 */
trait FictiousPlayTarget[State] extends TargetFunction[State] {

  // members needed from vertex
  val domain: Array[State]
  val id: Any
  def getMostRecentSignalMap: Map[Any, State]
  def neighbourIds: Iterable[Any]

  /**
   * Helper function for creating all combinations of a given size from the elements of a list.
   * The order does matter, so (1,2) is not the same as (2,1).
   * For example:
   * cartesian(List(1,2), 2) will yield List(List(1,1), List(1,2), List(2,1), List(2,2))
   */
  def cartesian[A](xs: Traversable[Traversable[A]]): Seq[Seq[A]] = {
    xs.foldLeft(Seq(Seq.empty[A])){
      (x, y) => for (a <- x.view; b <- y) yield a :+ b
    }
  }

  val nNeighbours = neighbourIds.size

  // All the possible state configurations as a list of lists.
  // This corresponds to Y_{-i}, where i is the id of this vertex.
  val neighbourhoods = cartesian(Traversable.fill(nNeighbours)(domain.toTraversable))

  // Convert the list of lists to a list of maps where each map represents
  // a theoretically possible neighbourhood config
  val theoreticalConfigs: Seq[Map[Any, State]] = neighbourhoods map { states =>
    val assocs = (neighbourIds zip states).toSeq
    Map(assocs:_*)
  }

  // Initialized to null because when the vertex is constructed, the adjacent edges are not known
  // As soon as the first collect operation gehts executed, the histories object will be initialized.
  var neighbourHistories: NeighbourHistories[State] = null

  override def evaluateAll = {
    if (neighbourHistories == null) {
      neighbourHistories = new NeighbourHistories(neighbourIds, domain)
    }
    neighbourHistories.updateFrequencies(getMostRecentSignalMap)
    super.evaluateAll
  }

  override def evaluate: State => (State, Double) = { st =>

    // All the individual weighted utilities for each theoretically possible neighbourhood config
    val individualUtils = for (theoreticalConfig <- theoreticalConfigs) yield {
      // The probabilities a neighbour could be in the theoretical state
      val probabilities = for ((neighbourId, neighbourSt) <- theoreticalConfig) yield {
        // If there exists no entry for a given neighbour, the probability that the whole
        // theoretical neighbourhood config can happen is automatically zero.
        neighbourHistories.getStateFrequency(neighbourId, neighbourSt).getOrElse(0.0)
      }
      // The product of all probabilities is the probability of this theoretical config being the current one
      val pConfig = probabilities.product

      // The number of constraints that would be satisfied with this theoretical config
      // Note: The utility is hardcoded as a NEQ relation
      val satisfied = (theoreticalConfig filter (_._2 != st)).size

      // The weighted utility
      satisfied * pConfig
    }

    // The expected utility for this state
    val finalUtility = individualUtils.sum

    //println(debugMsg)

    (st, finalUtility)
  }
}


trait DiscountedRegretTarget[State] extends TargetFunction[State] {
  import scala.math.max
  val fadingMemory: Double // if == 0, only the current regret counts
  def state: State
  def domain: Array[State]
  val id: Any
  var stepCounter: Int
  var pastRegrets = (for (st <- domain) yield (st, Average.empty)).toMap


  override def evaluate: (State) => (State, Payoff) = { st =>
    // compute the current regret for not changing to state st
    val currentRegret = utility(st) - utility(state)
    // take fading memory into account
    val discountedRegret = fadingMemory * currentRegret + (1 - fadingMemory) * pastRegrets(st).value
    // regret can't be below zero
    val finalRegret = max(0, discountedRegret)
    // update past regrets
    val updatedRegretRecord = pastRegrets(st).updated(discountedRegret)
    pastRegrets = pastRegrets.updated(st, updatedRegretRecord)

    (st, finalRegret)
  }
}

trait RegretTarget[State] extends TargetFunction[State] {
  var time = 1.0
  def state: State
  def domain: Array[State]
  var pastRegrets = (for (st <- domain) yield (st, Average.empty)).toMap
  override def evaluate: (State) => (State, Payoff) = { st =>
    // compute the current regret for not changing to state st
    val currentRegret = utility(st) - utility(state)
    val updatedRegretRecord = pastRegrets(st).updated(currentRegret)
    // update past regrets
    pastRegrets = pastRegrets.updated(st, updatedRegretRecord)
    // the average over all regrets (including the current one) is the payoff
    (st, pastRegrets(st).value)
  }
}


trait JointFictiousPlayTarget[State] extends TargetFunction[State] {

  // members needed from vertex
  val domain: Array[State]
  def neighbourIds: Iterable[Any]
  val id: Any

  var stateValue = (for (st <- domain) yield (st, Average.empty)).toMap

  override def evaluate: State => (State, Double) = { st =>
    // update records
    val updatedRecord = stateValue(st).updated(utility(st))
    stateValue = stateValue.updated(st, updatedRecord)

    // return updated value for this state
    (st, stateValue(st).value)
  }
}
