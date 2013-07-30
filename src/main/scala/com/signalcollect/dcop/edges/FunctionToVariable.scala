package com.signalcollect.dcop.edges

import com.signalcollect.DefaultEdge
import com.signalcollect.dcop.vertices.FunctionVertex
import com.signalcollect.dcop.vertices.id.MaxSumId

class FunctionToVariable(utility : (Set[Double]) => Double) extends DefaultEdge{
 
  override type Source = FunctionVertex

  //utility function is passed on object creation
  
  def signal = {
    //only signal if all necessary messages have arrived at the sending vertex
    if(source.readyToMessage){
      //TODO: code to compute the message to be sended from function (source vertex) to variable
    }
  }

  def R_m_n = {
    
  }

  private val utilityFunction = utility
  
  private def messageSum = {
    var summation = 0.0
    val variableIdSet = source.getNeighborIds - targetId.asInstanceOf[MaxSumId] 
    //variableIdSet.foreach(v => summation = summation + source.receivedMessages(v).value)
    //FIXME: MaxSumMessage has a value of type Array[Double], but how to ADD these values??
  }
  
  
  
}