/*
 *  @author Genc Mazlami
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

package com.signalcollect.dcop.evaluation.dsa

import com.signalcollect.Vertex
import com.signalcollect.interfaces.AggregationOperation
import com.signalcollect.dcop.evaluation.candidates.DSAVertex

class DSAConflictAggregationOperation extends AggregationOperation[Int] {

    /**
   *  Extracts values of type `ValueType` from vertices.
   */
  def extract(v: Vertex[_, _]): Int = {
	val vertex = v.asInstanceOf[DSAVertex]
	vertex.getNumOfConflicts
  }

  /**
   *  Aggregates all the values extracted by the `extract` function.
   *
   *  @note There is no guarantee about the order in which this function gets executed on the extracted values.
   */
  def aggregate(a: Int, b: Int): Int = {
    a + b
  }

   /**
   * Reduces an arbitrary number of elements to one element.
   */
  override def reduce(elements: Stream[Int]): Int = {
    var sum = 0
    elements.foreach{el =>
      sum += el
    }
    sum 
  }
  
  /**
   *  Neutral element of the `aggregate` function:
   *  `aggregate(x, neutralElement) == x`
   */
  val neutralElement: Int = 0
  
  
}