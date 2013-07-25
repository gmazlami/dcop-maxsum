package com.signalcollect.dcop.vertices

import com.signalcollect.DataGraphVertex

class Variable(id:Int, state:Double) extends DataGraphVertex(id,state){

  type Signal = Double
  
  def collect = 0
}