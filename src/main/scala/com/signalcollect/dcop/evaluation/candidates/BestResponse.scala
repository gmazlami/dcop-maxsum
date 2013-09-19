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

import com.signalcollect.Vertex
import scala.util.Random
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.evaluation.candidates.ColorConstrainedVertex


class BestResponseVertex(
    newId: Int,
    initialState: Int,
    newDomain: Array[Int],
    val inertia: Double)
  extends ColorConstrainedVertex[Int,Int](newId, initialState, newDomain)
  with CompleteSearch[Int]
  with ArgmaxBIDecision[Int]
  with MapUtilityTarget[Int]
  with FloodSchedule {
  
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
