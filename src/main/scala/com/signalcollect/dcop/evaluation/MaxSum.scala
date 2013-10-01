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

package com.signalcollect.dcop.evaluation

import com.signalcollect.dcop.io.FileGraphReader
import com.signalcollect.dcop.graphs.FactorGraphTransformer
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.ExecutionInformation
import com.signalcollect.dcop.util.ProblemConstants
import com.signalcollect.dcop.vertices.id.MaxSumId
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.dcop.evaluation.maxsum.MaxSumConflictAggregationOperation

class MaxSum(file: String, config: ExecutionConfiguration, numOfColors : Int, isAdopt : Boolean) {

  var conflictsOverTime : Map[Int,Int] = null
  val fileName = file
  val numColors = numOfColors
  val executionConfig = config
  val reader: FileGraphReader = new FileGraphReader
  val transformer: FactorGraphTransformer = new FactorGraphTransformer
  var statistics : ExecutionInformation = null
  val isInputAdopt = isAdopt

  val simpleGraph = if(isInputAdopt) reader.readFromAdoptFileToMap(fileName) else reader.readToMap(fileName)
  val simpleGraphList = if(isInputAdopt) reader.readFromAdoptFileToList(fileName) else reader.readToList(fileName)

  val signalCollectFactorGraph = transformer.transform(simpleGraph)

  initializePrefs()
  
  reader.storeNeighborStructure(simpleGraphList, simpleGraph)
  simpleGraph.foreach { entry =>
    entry._2.functionVertex.initializeReceivedMessages
    entry._2.variableVertex.initializeReceivedMessages
  }


  def execute() = {
    signalCollectFactorGraph.awaitIdle
    val stats = signalCollectFactorGraph.execute(executionConfig)
    println(stats)
    statistics = stats
  }
  
  def run() = {
    signalCollectFactorGraph.awaitIdle
    val stats = signalCollectFactorGraph.execute(executionConfig)
  }


  def printVertexStates() = {
    println("Vertices: ")
    println("---------")
    signalCollectFactorGraph.foreachVertex { v =>
      val vertex = v.id.asInstanceOf[MaxSumId]
      if (vertex.isVariable) {
        println(vertex.id + " has state: " + v.state)
      }
    }
  }

  
  private def initializePrefs() = {
    ProblemConstants.numOfColors = numColors
	var color = 0  
    simpleGraphList.foreach{el =>
      val pref = ArrayBuffer.fill(ProblemConstants.numOfColors)(-0.1)
      val variableId = el.variableVertex.id
	  pref(color) = 0.1
	  ProblemConstants.initialPreferences += (variableId -> pref)
      color = (color + 1) % ProblemConstants.numOfColors
	}
  }
  
   private def initializePrefsUniform() = {
    ProblemConstants.numOfColors = numColors
	var color = 0  
    simpleGraphList.foreach{el =>
      val pref = ArrayBuffer.fill(ProblemConstants.numOfColors)(-0.1)
      val variableId = el.variableVertex.id
	  pref(0) = 0.1
	  pref(1) = 0.1
	  pref(2) = -0.1
	  pref(3) = -0.1
	  ProblemConstants.initialPreferences += (variableId -> pref)
      color = (color + 1) % ProblemConstants.numOfColors
	}
  }
}
