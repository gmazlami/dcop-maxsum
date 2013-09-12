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

object DSAVariant extends Enumeration {
  type DSAVariant = Value
  val A, B, asyncA, asyncB = Value
}

import DSAVariant._


class DSAVertexBuilder(randomInitialState: Boolean, variant: DSAVariant, pSchedule: Double)
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
  with MapUtilityTarget[Int]
