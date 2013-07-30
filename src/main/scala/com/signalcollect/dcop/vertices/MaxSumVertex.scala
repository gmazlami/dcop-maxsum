package com.signalcollect.dcop.vertices

import com.signalcollect.dcop.vertices.id.MaxSumId
import com.signalcollect.DataGraphVertex
import com.signalcollect.dcop.MaxSumMessage

abstract class MaxSumVertex(id : MaxSumId, initialState : Double) extends DataGraphVertex(id, initialState) {
	
	var stepCounter : Int = 0
	
	//most recently computed message that waits to be sent by this vertex
	var messageToSend : MaxSumMessage
}