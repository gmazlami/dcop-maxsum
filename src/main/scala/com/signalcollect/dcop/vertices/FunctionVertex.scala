package com.signalcollect.dcop.vertices

import com.signalcollect.DataGraphVertex
import com.signalcollect.dcop.MaxSumMessage
import com.signalcollect.dcop.vertices.id.MaxSumId
import scala.collection.immutable.HashMap

class FunctionVertex(id:MaxSumId, state:Double) extends MaxSumVertex(id, state){

	type Signal = MaxSumMessage
  	
	val receivedMessages : HashMap[MaxSumId,MaxSumMessage] = HashMap()
	
	def collect =  0.0
	
	 def readyToMessage : Boolean = {
	  if(receivedFromAllNeighbors){
	    true
	  }else{
	    false
	  }
	}
	
	private def receivedFromAllNeighbors : Boolean = {
	  var receivedAll : Boolean = true
	  for(id <- getNeighborIds){
		  if(!getIdsReceivedFrom.contains(id)){
		    receivedAll = false;
		  }
	  }
	  receivedAll
	}
	
	private def getIdsReceivedFrom : Set[MaxSumId] = {
	  val resultSet : Set[MaxSumId] = Set()
	  receivedMessages.keys.foreach(m => resultSet + m)
	  resultSet
	}

}