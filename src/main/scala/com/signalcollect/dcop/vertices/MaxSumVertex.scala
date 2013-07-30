package com.signalcollect.dcop.vertices

import com.signalcollect.dcop.vertices.id.MaxSumId
import com.signalcollect.DataGraphVertex
import com.signalcollect.dcop.MaxSumMessage
import com.signalcollect.Edge

abstract class MaxSumVertex(id : MaxSumId, initialState : Double) extends DataGraphVertex(id, initialState) {
	
	var stepCounter : Int = 0
	
	def getNeighborIds : Set[MaxSumId] = {
	  var resultSet : Set[MaxSumId] = Set()
	  outgoingEdges.keys.foreach(key => resultSet + key.asInstanceOf[MaxSumId])
	  resultSet
	}
}