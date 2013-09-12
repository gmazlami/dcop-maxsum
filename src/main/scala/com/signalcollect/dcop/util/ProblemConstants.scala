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

package com.signalcollect.dcop.util

import scala.collection.mutable.HashMap
import com.signalcollect.dcop.vertices.id.MaxSumId
import scala.collection.mutable.ArrayBuffer

object ProblemConstants {

  var colors: Set[Int] = Set()

  var numOfColors: Int = 0

  var initialPreferences: HashMap[MaxSumId, ArrayBuffer[Double]] = HashMap()

  var neighborStructure: HashMap[MaxSumId, ArrayBuffer[MaxSumId]] = HashMap()

  def getPreferenceTable(variableNode: MaxSumId): ArrayBuffer[Tuple3[MaxSumId, Int, Double]] = {
    val preferenceTable: ArrayBuffer[Tuple3[MaxSumId, Int, Double]] = ArrayBuffer.fill(numOfColors)(null)
    val preference = initialPreferences(variableNode)
    for (i <- 0 to preference.length - 1) {
      preferenceTable(i) = (variableNode, i, preference(i))
    }
    preferenceTable
  }

  def getOwnedVariable(functionNode: MaxSumId) = {
    val neighbor = neighborStructure(functionNode)
    neighbor.find{ neighbor => 
      neighbor.idNumber == functionNode.idNumber 
    }.get
  }


}