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
import scala.collection.immutable.HashMap

abstract class MaxSumVertex(id : MaxSumId, initialState : Double) extends DataGraphVertex(id, initialState) {
	
	var stepCounter : Int = 0
	
	val receivedMessages : HashMap[MaxSumId,MaxSumMessage]
	
	def getNeighborIds : Set[MaxSumId] = {
	  val resultSet : Set[MaxSumId] = Set()
	  outgoingEdges.keys.foreach(key => resultSet + key.asInstanceOf[MaxSumId])
	  resultSet
	}
	
	def readyToMessage : Boolean = {
	  if(receivedFromAllNeighbors){
	    true
	  }else{
	    false
	  }
	}
	
	protected def receivedFromAllNeighbors : Boolean = {
	  var receivedAll : Boolean = true
	  for(id <- getNeighborIds){
		  if(!getIdsReceivedFrom.contains(id)){
		    receivedAll = false;
		  }
	  }
	  receivedAll
	}
	
	protected def getIdsReceivedFrom : Set[MaxSumId] = {
	  val resultSet : Set[MaxSumId] = Set()
	  receivedMessages.keys.foreach(m => resultSet + m)
	  resultSet
	}
}