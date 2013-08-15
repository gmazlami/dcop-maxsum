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

class FunctionToVariable(utility : (Set[Double]) => Double, t: MaxSumId) extends DefaultEdge(t){
 
  override type Source = FunctionVertex

  def signal = R_m_n

  def R_m_n = {
    //a set containing all messages Q_n_m for being summed in the computation of R_m_n
    val sumSet = messageSumSet
	
	//a set containing the ids of the niehgbors of this edge's source vertex
	val neighborSetOfSource = ProblemConstants.neighborStructure(source.id)
	
	//a set containing the ids of variables (VariableVertex) over which the maximization in R_m_n is taken
	val maximizizationVariables = neighborSetOfSource - targetId.asInstanceOf[MaxSumId]
    
    
  }

  //dummy
  private def getUtilityFunction(initial:ArrayBuffer[Double]) : Unit => Double = {
    Unit => initial(0)
  }
  
  private def getStabilizedUtilityFunction(initial:ArrayBuffer[Double]) = {
    
  }
  

  
  private def messageSumSet = {
    var summationSet : ArrayBuffer[ArrayBuffer[Double]] = ArrayBuffer()
    
    //the sum in the Function-to-variable formula goes over the neighbor ids except the target id:
    val variableIdSet = source.getNeighborIds - targetId.asInstanceOf[MaxSumId] 

    //iterate over variable set and gather the messages into a summationSet
    variableIdSet.foreach{variableId =>
    		summationSet :+ source.receivedMessages(variableId).value
    }
    summationSet
  }
  
  
  
}