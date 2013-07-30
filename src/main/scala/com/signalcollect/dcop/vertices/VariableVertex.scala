package com.signalcollect.dcop.vertices

import com.signalcollect.DataGraphVertex
import com.signalcollect.dcop.MaxSumMessage

class Variable(id:Int, state:List[Double]) extends DataGraphVertex(id,state){

  type Signal = MaxSumMessage
  
  var test = List(1.0,2.0,3.0,4.0)
  
  def collect = test
  
}