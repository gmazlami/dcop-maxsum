package com.signalcollect.dcop.vertices

import com.signalcollect.DataGraphVertex
import com.signalcollect.dcop.MaxSumMessage

class Function(id:Int, state:Double) extends DataGraphVertex(id, state){

	type Signal = MaxSumMessage
  	
	def collect =  0.0

}