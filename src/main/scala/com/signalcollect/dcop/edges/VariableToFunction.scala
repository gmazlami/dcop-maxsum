package com.signalcollect.dcop.edges

import com.signalcollect.DefaultEdge
import com.signalcollect.dcop.vertices.Variable

class VariableToFunction extends DefaultEdge{

  type Source = Variable
  
  def signal = null
  
}