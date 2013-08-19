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
import com.signalcollect.dcop.vertices.FunctionVertex
import com.signalcollect.dcop.vertices.id.MaxSumId
import com.signalcollect.dcop.vertices.VariableVertex
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.dcop.util.ProblemConstants
import com.signalcollect.dcop.MaxSumMessage
import scala.math._

class FunctionToVariable(utility : (Set[Double]) => Double, t: MaxSumId) extends DefaultEdge(t){
 
  private var variableConfiguration : ArrayBuffer[Tuple3[MaxSumId,Double,Double]] = ArrayBuffer()
  
  override type Source = FunctionVertex

  def signal = R_m_n
  
  var maximizationResults : ArrayBuffer[Tuple2[Set[Tuple2[MaxSumId, Int]],Double]] = ArrayBuffer()

  def R_m_n : MaxSumMessage = {
    
    var messageSum : Double = 0
    
    //a set containing all messages Q_n_m for being summed in the computation of R_m_n
    val sumSet = messageSumSet
	
	//a set containing the ids of the nieghbors of this edge's source vertex
	val neighborSetOfSource : ArrayBuffer[MaxSumId] = ProblemConstants.neighborStructure(source.id)
	
	//a set containing the ids of variables (VariableVertex) over which the maximization in R_m_n is taken
	val maximizationVariables : ArrayBuffer[MaxSumId] = neighborSetOfSource - targetId.asInstanceOf[MaxSumId]
    
    val maximizationResults : ArrayBuffer[Tuple3[MaxSumId,Int,Double]] = ArrayBuffer()

    val preference = ProblemConstants.initialPreferences(source.id)
    
    //sum the maxima of the messages received from neighboring variables
    sumSet.foreach{message =>
      val currentVal: ArrayBuffer[Double] = message.value
      var max: Double = -1000000000
      
      //find out index and value of the maximium in this message vector
      currentVal.foreach { current =>
        if (current > max) {
          max = current
        }
      }
    
      messageSum = messageSum + max
    }
    
    val result1 = evaluateUtility(neighborSetOfSource, preference)(0) + messageSum
    val result2 = evaluateUtility(neighborSetOfSource, preference)(1) + messageSum
    
    val resultingMessage = new MaxSumMessage(source.id,targetId,ArrayBuffer(result1,result2))
    
    resultingMessage
  }

  def evaluateUtility(neighborSetOfSource : ArrayBuffer[MaxSumId], preference : ArrayBuffer[Double]) : ArrayBuffer[Double] = {
   ArrayBuffer(0.0,0.0) //TODO: finish implementation 
  }
  
  def evaluateStabilizedUtility(neighborSetOfSource : ArrayBuffer[MaxSumId], preference : ArrayBuffer[Double]) : ArrayBuffer[Double] = {
   ArrayBuffer(0.0,0.0) //TODO: finish implementation  
  }
  
  private def messageSumSet = {
    var summationSet : ArrayBuffer[MaxSumMessage] = ArrayBuffer()
    
    //the sum in the Function-to-variable formula goes over the neighbor ids except the target id:
    val variableIdSet = source.getNeighborIds - targetId.asInstanceOf[MaxSumId] 

    //iterate over variable set and gather the messages into a summationSet
    variableIdSet.foreach{variableId =>
    		summationSet :+ source.receivedMessages(variableId)
    }
    summationSet
  }
  
  
  
}