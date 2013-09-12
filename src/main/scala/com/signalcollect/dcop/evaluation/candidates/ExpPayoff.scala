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

import com.signalcollect.Vertex
import scala.util.Random
import com.signalcollect.dcop.evaluation.candidates.CompleteSearch
import com.signalcollect.dcop.evaluation.candidates.ExpectedPayoffTarget


/**
 * Expected Payoff - not presented in thesis
 * 
 * The payoff for changing to a certain state depends on the historic frequencies of neighboring states.
 * Vertex i's payoff of acquiring state si at time t is given by the SUM over all neighbours of their
 * probability not to be in state si times the utility the vertex would gain with state si.
 * 
 * NOTE: This is not the "expected payoff". The naming stems from the fact that this algorithm
 * was previously trying to calculate the expected payoff. 
 * 
 * Based loosely on a description of FP from:
 *   Chapman, A. C., Rogers, A., Jennings, N. R., and Leslie, D. S. (2011b).
 *   A unifying framework for iterative approximate best-response algorithms
 *   for distributed constraint optimization problems.
 */


class ExpPayoffVertex(
    newId: Int,
    initialState: Int,
    newDomain: Array[Int],
    val inertia: Double)
  extends ColorConstrainedVertex[Int,Int](newId, initialState, newDomain)
  with ArgmaxBIDecision[Int]
  with CompleteSearch[Int]
  with ExpectedPayoffTarget[Int]
  with FloodSchedule {
  type Signal = Int
}

class ExpVertexBuilder(
    randomInitialState: Boolean,
    inertia: Double)
  extends VertexBuilder {
  def apply(id: Int, domain: Array[Int]): Vertex[Any, _] = {
    val r = new Random
    val initialState = if (randomInitialState) domain(r.nextInt(domain.size)) else domain.head
    new ExpPayoffVertex(id, initialState, domain, inertia)
  }

  override def toString = s"EP I=$inertia"
}
