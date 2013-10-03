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

/* 
 * The algorithm Best Response (BR) (also called Better Reply) with Inertia 
 * This algorithm isn't presented in the thesis due to being extremely similar to DSA-B
 * 
 * BR uses argmaxB with inertia, a flood schedule, and the immediate payoff target function (MapUtilityTarget).
 * 
 * Based on a description from:
 *   Chapman, A. C., Rogers, A., Jennings, N. R., and Leslie, D. S. (2011b).
 *   A unifying framework for iterative approximate best-response algorithms
 *   for distributed constraint optimization problems.
 */

package com.signalcollect.dcop.evaluation.candidates

import scala.util.Random
import com.signalcollect.Vertex
import com.signalcollect.dcop.termination.ConvergenceHistory
import com.signalcollect.dcop.util.ProblemConstants

class BestResponseVertex(
  newId: Int,
  initialState: Int,
  newDomain: Array[Int],
  val inertia: Double)
  extends ColorConstrainedVertex[Int, Int](newId, initialState, newDomain)
  with CompleteSearch[Int]
  with ArgmaxBIDecision[Int]
  with MapUtilityTarget[Int]
  with FloodSchedule {

  val stateHistory : ConvergenceHistory[Int] = new ConvergenceHistory[Int](4)
  val utilityHistory : ConvergenceHistory[Double] = new ConvergenceHistory[Double](2)
  
  override def collect = {

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
    stateHistory.push(stateToReturn)
    stateToReturn
  }

  override def storeUtilityHistory(utility : Double) = {
	  utilityHistory.push(utility)
  }
  
  def getNumOfConflicts() = {
    var conflicts = 0
    val neighborConfig = getNeighbourConfigs
    neighborConfig.values.foreach { value =>
      if (value == state) {
        conflicts += 1
      }
    }
    conflicts
  }
  
  override def scoreCollect : Double = {
    if(ProblemConstants.convergenceEnabled){
    	if(edgesModifiedSinceCollectOperation){
    		1.0
    	}else{
    		if(stateHistory.hasConverged && utilityHistory.hasConverged){
    			0.0
    		}else{
    			1.0
    		}
    	}
    }else{
      1.0
    }
  }
  
  override def scoreSignal = {
    1.0
  }

}

class BestResponseVertexBuilder(
  randomInitialState: Boolean,
  inertia: Double)
  extends VertexBuilder {

  def apply(id: Int, domain: Array[Int]): Vertex[Any, _] = {
    val r = new Random
    val initialState = if (randomInitialState) domain(r.nextInt(domain.size)) else domain.head
    new BestResponseVertex(id, initialState, domain, inertia)
  }

  override def toString = s"BR[i=$inertia]"
}
