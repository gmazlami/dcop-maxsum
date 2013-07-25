package com.signalcollect.dcop.edges

import com.signalcollect.DefaultEdge
import com.signalcollect.dcop.vertices.Function

class FunctionToVariable extends DefaultEdge{
 
  type Source = Function

  def signal = null
}