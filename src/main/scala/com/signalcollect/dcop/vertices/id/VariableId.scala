package com.signalcollect.dcop.vertices.id

class VariableId(num : Int) extends MaxSumId{
  
	//Variable nodes have ids of the form V1, V2, ....
	val id = "V" + num
}