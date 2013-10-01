package com.signalcollect.dcop.test

import com.signalcollect.dcop.vertices.VariableVertex
import com.signalcollect.dcop.vertices.id.MaxSumId
import scala.collection.mutable.ArrayBuffer

object ReferenceTest extends App {
	val vv = new VariableVertex(new MaxSumId(0,2), 1)
	
	val array = ArrayBuffer(vv)
	
	println(array)
	
	vv.state = 1230
	
	println(array)
}