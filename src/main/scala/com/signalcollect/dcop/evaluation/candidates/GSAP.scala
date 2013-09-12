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


/*
 * The greedy spatial adaptive play algorithm
 * 
 * A greedy version of spatial adaptive play that uses a greedy argmaxA decision rule
 * and -- like SAP -- a sequential random schedule.
 * 
 * Based on a description from:
 *   Chapman, A. C., Rogers, A., Jennings, N. R., and Leslie, D. S. (2011b).
 *   A unifying framework for iterative approximate best-response algorithms
 *   for distributed constraint optimization problems.
 */


package com.signalcollect.dcop.evaluation.candidates

import com.signalcollect.Vertex
import scala.util.Random
import com.signalcollect.dcop.evaluation.candidates.CompleteSearch
import com.signalcollect.dcop.evaluation.candidates.MapUtilityTarget


class GSAPVertex(newId: Int, initialState: Int, newDomain: Array[Int], val vertexIds: Seq[Int])
  extends ColorConstrainedVertex[Int, Int](newId, initialState, newDomain)
  with CompleteSearch[Int]
  with MapUtilityTarget[Int]
  with ArgmaxADecision[Int]
  with SequentialRandomSchedule
  

class GSAPVertexBuilder(randomInitialState: Boolean, vertexIds: Seq[Int])
  extends VertexBuilder {
  def apply(id: Int, domain: Array[Int]): Vertex[Any, _] = {
    val r = new Random
    val initialState = if (randomInitialState) domain(r.nextInt(domain.size)) else domain.head
    new GSAPVertex(id, initialState, domain, vertexIds)
  }

  override def toString = "gSAP"
}
