/*
 *  @author Genc Mazlami
 *
 *  Copyright 2013 University of Zurich
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.signalcollect.dcop.edges

import com.signalcollect.DefaultEdge
import com.signalcollect.dcop.vertices.VariableVertex
import com.signalcollect.dcop.vertices.id.MaxSumId

class VariableToFunction(utility : (Set[Double]) => Double, id : MaxSumId) extends DefaultEdge(id){

  type Source = VariableVertex
  
  def signal = {
    //only signal if all necessary messages have arrived at the sending vertex
    if(source.readyToMessage){
      
    }
  }
  
  def Q_n_m = {
    //TODO: code to compute the message to be sended from function (source vertex) to variable
		 
  }
  
    //utility function is passed on object creation
  private val utilityFunction = utility
  
//  private def messageSum = {
//    var summation = 0.0
//    val variableIdSet = source.getNeighborIds - targetId.asInstanceOf[MaxSumId] 
//    //variableIdSet.foreach(v => summation = summation + source.receivedMessages(v).value)
//    //FIXME: MaxSumMessage has a value of type Array[Double], but how to ADD these values??
//  }
}