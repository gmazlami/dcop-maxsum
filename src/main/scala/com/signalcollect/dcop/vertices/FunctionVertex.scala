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

package com.signalcollect.dcop.vertices

import com.signalcollect.DataGraphVertex
import com.signalcollect.dcop.MaxSumMessage
import com.signalcollect.dcop.vertices.id.MaxSumId
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer

class FunctionVertex(id: MaxSumId, state: Int) extends MaxSumVertex(id, state) {

//  val ownedVariable : MaxSumId = triple._1
//  
//  val neighborSet : ArrayBuffer[MaxSumId] = triple._3
//  
//  val preferenceTable : ArrayBuffer[Double] = triple._2
  
  type Signal = MaxSumMessage
  
  override def collect = {
    mostRecentSignalMap.foreach { mapEntry =>
      val currentId = mapEntry._1.asInstanceOf[MaxSumId]
      val message = mapEntry._2.asInstanceOf[MaxSumMessage]
      receivedMessages += (currentId -> message)
    }
    0 //return value of FunctionVertex.collect is of no importance, since only the VariableVertex instances state matters to the problem
  }
  
  def initConstants(triple : Tuple3[MaxSumId,ArrayBuffer[Double],ArrayBuffer[MaxSumId]]) = {
    println("initing constants")
  }
}