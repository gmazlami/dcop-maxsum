package com.signalcollect.dcop.test

import com.signalcollect.dcop.vertices.VariableVertex
import com.signalcollect.dcop.vertices.MaxSumVertex
import com.signalcollect.dcop.vertices.id.MaxSumId
import com.signalcollect.dcop.vertices.FunctionVertex

object DynamicBindingTest extends App {

  val a : MaxSumVertex = new VariableVertex(new MaxSumId(0,0),0)
  val b : MaxSumVertex = new FunctionVertex(new MaxSumId(0,1),0)
  
  println(a.getNumOfConflicts)
  println(b.getNumOfConflicts)
}