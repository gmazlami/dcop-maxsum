/*
 *  @author Robin Hafen
 *  @author modified by Genc Mazlami
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


/* 
 * The distributed stochastic algorithms (synchronous and asynchronous).
 * Only the synchronous variants are evaluated in this thesis.
 * 
 * DSA-A uses the decision rule argmaxA, whereas DSA-B uses argmaxB.
 * All variants use a parallel random schedule.
 * 
 * Based on a description from:
 *   Chapman, A. C., Rogers, A., Jennings, N. R., and Leslie, D. S. (2011b).
 *   A unifying framework for iterative approximate best-response algorithms
 *   for distributed constraint optimization problems.
 */

package com.signalcollect.dcop.evaluation.candidates
import com.signalcollect._
import scala.util._
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.termination.ConvergenceHistory
import com.signalcollect.dcop.evaluation.candidates.DSAVariant

object DSAVariant extends Enumeration {
  type DSAVariant = Value
  val A, B, asyncA, asyncB = Value
}


class DSAVertexBuilder(randomInitialState: Boolean, variant: DSAVariant.Value, pSchedule: Double)
  extends VertexBuilder {

  def apply(id: Int, domain: Array[Int]): Vertex[Any, _] = {
    val initialState = if (randomInitialState) {
      domain(Random.nextInt(domain.size))
    } else {
      domain.head
    }

    variant match {
      case DSAVariant.A => new DSAVertex(id, initialState, domain)
      				with ArgmaxADecision[Int]
      				with ParallelRandomSchedule {
				        val p = pSchedule
				    }
      case DSAVariant.B => new DSAVertex(id, initialState, domain)
      				with ArgmaxBDecision[Int]
      				with ParallelRandomSchedule {
    	  				val p = pSchedule
			        }
      case DSAVariant.asyncB => new DSAVertex(id, initialState, domain)
      				with ArgmaxBDecision[Int]
      				with AsyncRandomSchedule {
    	  				val p = pSchedule
			        }
      case DSAVariant.asyncA => new DSAVertex(id, initialState, domain)
      				with ArgmaxADecision[Int]
      				with AsyncRandomSchedule {
    	  				val p = pSchedule
			        }
      case other => throw new Exception("[fatal] Unknown DSA Variant")
    }
  }

  override def toString = s"DSA-$variant p=$pSchedule"
}


abstract class DSAVertex(id: Any, initialState: Int, possibleValues: Array[Int])
  extends ColorConstrainedVertex[Any,Int](id, initialState, possibleValues)
  with RandomStateSearch[Int]
  with MapUtilityTarget[Int] {
  
   val utilityHistory : ConvergenceHistory[Double] = new ConvergenceHistory[Double](2)  
   val stateHistory : ConvergenceHistory[Int] = new ConvergenceHistory[Int](4)
  
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
    neighborConfig.values.foreach{ value =>
      if(value == state){
        conflicts += 1
      }
    }
    conflicts
  }
  
  override def scoreCollect : Double = {
    if(edgesModifiedSinceCollectOperation){
      1.0
    }else{
      if(stateHistory.hasConverged && utilityHistory.hasConverged){
        0.0
      }else{
        1.0
      }
    }
  }
  
  override def scoreSignal = {
    1.0
  }
}
