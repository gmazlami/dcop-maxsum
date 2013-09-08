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
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.dcop.util.ProblemConstants

class VariableToFunction( id : MaxSumId) extends DefaultEdge(id){

  type Source = VariableVertex
  
  
  def signal = Q_n_m
  
  def Q_n_m = {
    val variableIdSet = source.getNeighborIds - (targetId.asInstanceOf[MaxSumId])
    var resultMessage : ArrayBuffer[Double] = ArrayBuffer.fill(ProblemConstants.numOfColors)(0.0)
	
    variableIdSet.foreach{ variableId =>
      val message = source.receivedMessages(variableId).value
      if(message.length != ProblemConstants.numOfColors){ //this is not allowed to happen
        println("FATAL: message length not equal to number of possible colors! Aborting..")
        System.exit(-1)
      }else{
        for(i <- 0 to resultMessage.length - 1){
          resultMessage(i) = resultMessage(i) + message(i)
        }
      }
    }
    resultMessage
  }

}