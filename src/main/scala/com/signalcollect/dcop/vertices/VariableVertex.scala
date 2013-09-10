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

package com.signalcollect.dcop.vertices

import com.signalcollect.DataGraphVertex
import com.signalcollect.dcop.MaxSumMessage
import com.signalcollect.dcop.vertices.id.MaxSumId
import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.dcop.util.ProblemConstants

class VariableVertex(id:MaxSumId, state:Int) extends MaxSumVertex(id,state){

  type Signal = MaxSumMessage
  
  var marginal : ArrayBuffer[Double] = ArrayBuffer.fill(ProblemConstants.numOfColors)(0.0) 
  
  def collect =  {
	  mostRecentSignalMap.foreach{ mapEntry =>
	    val currentId = mapEntry._1.asInstanceOf[MaxSumId]
	    val message = mapEntry._2.asInstanceOf[MaxSumMessage]
	    receivedMessages += (currentId -> message)
	  }

	  //compute updated marginal with new received messages
	  marginal = ArrayBuffer.fill(ProblemConstants.numOfColors)(0.0)  
	  for(color <- 0 to ProblemConstants.numOfColors - 1){
		  receivedMessages.values.foreach{message =>
		  	marginal(color)  +=  message.value(color)
		  }
	    
	  }
	  
	  findResultingColorFromMarginal
	}

  
  
  
  private def findResultingColorFromMarginal() : Int = {
    var max = Double.MinValue
    var maxColor = -1
    for(color <- 0 to ProblemConstants.numOfColors - 1){
      val value = marginal(color)
      if(max < value){
        max = value
        maxColor = color
      }
    }
    maxColor    
  }

}