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

import com.signalcollect.dcop.vertices.id.MaxSumId
import com.signalcollect.DataGraphVertex
import com.signalcollect.dcop.MaxSumMessage
import com.signalcollect.Edge
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.dcop.util.ProblemConstants
import com.signalcollect.GraphEditor

abstract class MaxSumVertex(id: MaxSumId, initialState: Int) extends DataGraphVertex(id, initialState) {

  override def scoreSignal = 1.0

  override def scoreCollect = 1.0
  
  var receivedMessages: HashMap[MaxSumId, MaxSumMessage] = null

  def initializeReceivedMessages = {
    var map: HashMap[MaxSumId, MaxSumMessage] = HashMap()
    getNeighborIds.foreach { currentId =>
      val dummyMessage = new MaxSumMessage(currentId, id, ArrayBuffer.fill(ProblemConstants.numOfColors)(0.0))
      map += (currentId -> dummyMessage)
    }
    receivedMessages = map
  }

  def getNeighborIds: ArrayBuffer[MaxSumId] = {
    var resultSet: ArrayBuffer[MaxSumId] = ArrayBuffer.fill(outgoingEdges.keys.size)(null)
    var index = 0
    outgoingEdges.keys.foreach { key =>
      resultSet(index) = key.asInstanceOf[MaxSumId]
      index = index + 1
    }
    resultSet

  }

  def getNeighborVertices: ArrayBuffer[MaxSumVertex] = {
    var resultSet: ArrayBuffer[MaxSumVertex] = ArrayBuffer.fill(outgoingEdges.keys.size)(null)
    var index = 0

    outgoingEdges.keys.foreach { key =>
      val vv = ProblemConstants.findVariableVertexWithIdNum(key.asInstanceOf[MaxSumId].idNumber)
      resultSet(index) = vv
      index += 1
    }
    resultSet
  }

  def readyToMessage: Boolean = {
    if (receivedFromAllNeighbors) {
      true
    } else {
      false
    }
  }

  protected def receivedFromAllNeighbors: Boolean = {
    var receivedAll: Boolean = true
    for (id <- getNeighborIds) {
      if (!getIdsReceivedFrom.contains(id)) {
        receivedAll = false;
      }
    }
    receivedAll
  }

  protected def getIdsReceivedFrom: Set[MaxSumId] = {
    var resultSet: Set[MaxSumId] = Set()
    receivedMessages.keys.foreach(m => resultSet += m)
    resultSet
  }

  def getNumOfConflicts(): Int = {
    0
  }

}