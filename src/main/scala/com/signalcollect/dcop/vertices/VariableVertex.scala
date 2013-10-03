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
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.dcop.util.ProblemConstants
import scala.collection.mutable.Queue
import com.signalcollect.dcop.termination.ConvergenceHistory
import com.signalcollect.dcop.termination.MarginalHistory
import com.signalcollect.GraphEditor

class VariableVertex(id: MaxSumId, state: Int) extends MaxSumVertex(id, state) {

  type Signal = MaxSumMessage

  val marginalHistory : MarginalHistory[ArrayBuffer[Double]] = new MarginalHistory(2, 0.99)
  
  val stateHistory : ConvergenceHistory[Int] = new ConvergenceHistory(4)
  
  var marginal: ArrayBuffer[Double] = ArrayBuffer.fill(ProblemConstants.numOfColors)(0.0)
  
  var neighborColors : Map[MaxSumId,Int] = Map()
  
  var currentColor: Int = -1

  override def deliverSignal(signal: Any, sourceId: Option[Any], graphEditor: GraphEditor[Any, Any]): Boolean = {
    signal match {
      case s : MaxSumMessage => super.deliverSignal(signal, sourceId, graphEditor)
      case c : Int => {
    	  neighborColors += (sourceId.get.asInstanceOf[MaxSumId] -> c) 
    	  false
      }
    }
  }
  
  def tellNeighborsAboutColor(graphEditor : GraphEditor[Any,Any]) = {
    val neighbors = mapToVariableIds(getNeighborIds)
    neighbors.foreach{neighborId =>
      graphEditor.sendSignal(currentColor, neighborId, Option(id)) 
    }
  }
  
  override def collect = {
    mostRecentSignalMap.foreach { mapEntry =>
      val currentId = mapEntry._1.asInstanceOf[MaxSumId]
      val message = mapEntry._2.asInstanceOf[MaxSumMessage]
      receivedMessages += (currentId -> message)
    }
    //compute updated marginal with new received messages
    marginal = ArrayBuffer.fill(ProblemConstants.numOfColors)(0.0)
    marginalHistory.push(marginal)
    for (color <- 0 to ProblemConstants.numOfColors - 1) {
      receivedMessages.values.foreach { message =>
        marginal(color) += message.value(color)
      }
    }
    currentColor = findResultingColorFromMarginal
    stateHistory.push(currentColor)
    currentColor
  }
  
  
  override def scoreCollect : Double = {
    if(ProblemConstants.convergenceEnabled){
    	if(edgesModifiedSinceCollectOperation){
    		1.0
    	}else{
    		if(stateHistory.hasConverged && marginalHistory.hasConverged){
    			0.0
    		}else{
    			1.0
    		}
    	}
    }else{
      1.0
    }
  }
  
  private def findResultingColorFromMarginal(): Int = {
    var max = Double.MinValue
    var maxColor = -1
    for (color <- 0 to ProblemConstants.numOfColors - 1) {
      val value = marginal(color)
      if (max < value) {
        max = value
        maxColor = color
      }
    }
    maxColor
  }

  override def getNumOfConflicts() : Int = {
    var conflicts = 0
    neighborColors.foreach{n =>
      if(n._2 == currentColor){
        conflicts += 1
      }
    }
    conflicts / 2
  }


  private def mapToVariableIds(ids : ArrayBuffer[MaxSumId]) = {
    var variableIds : ArrayBuffer[MaxSumId] = ArrayBuffer()
    ids.foreach{i =>
      variableIds += new MaxSumId(i.idNumber, 0)
    }
    variableIds
  }
}