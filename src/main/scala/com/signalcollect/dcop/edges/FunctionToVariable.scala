package com.signalcollect.dcop.edges

import com.signalcollect.DefaultEdge
import com.signalcollect.dcop.vertices.Function

class FunctionToVariable(utility : (Set[Double]) => Double) extends DefaultEdge{
 
  override type Source = Function

  //utility function is passed on object creation
  val utilityFunction = utility
  
  def signal = {
    
    
  }
  
  
}