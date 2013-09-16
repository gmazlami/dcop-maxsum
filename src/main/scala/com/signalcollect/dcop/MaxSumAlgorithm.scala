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

package com.signalcollect.dcop

import com.signalcollect.GraphBuilder
import com.signalcollect.dcop.util.ProblemConstants
import com.signalcollect.dcop.vertices.id.MaxSumId
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.dcop.io.FileGraphReader
import com.signalcollect.dcop.graphs.FactorGraphTransformer
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.evaluation.statistics.ConvergenceObserver
import com.signalcollect.dcop.evaluation.statistics.OptimumObserver
import scala.collection.mutable.HashMap
import com.signalcollect.dcop.evaluation.statistics.ConvergenceObserver
import com.signalcollect.dcop.evaluation.statistics.GlobalMeasurer
import com.signalcollect.dcop.evaluation.statistics.MeasuringInstrument



object MaxSumAlgorithm extends App{

  val fileName : String = "graphs/full-graph-20.txt"
  
  println("--------------------------------------------------")
  println("STARTING INITIALIZATION")
  println("--------------------------------------------------")
  
  val reader : FileGraphReader = new FileGraphReader
  val transformer : FactorGraphTransformer = new FactorGraphTransformer
  
  println
  println("--------------------------------------------------")
  println("Reading simple graph from txt-File: " + fileName)
  
  val simpleGraph = reader.readToMap(fileName)
  val simpleGraphList = reader.readToList(fileName)
  
  ProblemConstants.globalVertexList = simpleGraphList
  
  println("Reading of simple graph succesfully completed.")
  println("--------------------------------------------------")
  println
  println("--------------------------------------------------")
  println("Transformation of simple graph to Signal-Collect graph started.")
  
  val signalCollectFactorGraph = transformer.transform(simpleGraph)
  
  println("Transformation to Signal/Collect graph successfully completed.")
  println("--------------------------------------------------")
  
  println
  
  println("--------------------------------------------------")
  println("Initialization of Problem-Constants started")
  
  ProblemConstants.numOfColors = 3 ; println("Number of Colors = " + ProblemConstants.numOfColors + " initialized")
  initializePrefs()
  
  println("Preferences initialized.")
  
  reader.storeNeighborStructure(simpleGraphList, simpleGraph)
  
  println("Initializing initial Messages at vertices with values (0.0 , 0.0 , 0.0 ... 0,0)")
  
  simpleGraph.foreach{entry =>
    entry._2.functionVertex.initializeReceivedMessages
    entry._2.variableVertex.initializeReceivedMessages
  }  
  
  println("Initialization of global problem constants successfully completed.")
  println("--------------------------------------------------")
  
  println
  
  println("Initializing measuring instruments....")
  
  GlobalMeasurer.maxsumInstrument = new MeasuringInstrument("Max-Sum", simpleGraphList)
  
  println
  
  println("------------------------------------------")
  println("INITIALIZATION COMPLETED.")
  println("------------------------------------------")
  
  println
  
  println("------------------------------------------")
  println("EXECUTION OF MAX-SUM ALGORITHM STARTED:")
  println("------------------------------------------")
  println
  
  signalCollectFactorGraph.awaitIdle
  val stats = signalCollectFactorGraph.execute(ExecutionConfiguration.withExecutionMode(
      ExecutionMode.PureAsynchronous))
  println(stats)

  signalCollectFactorGraph.foreachVertex(println(_))
  
  println("------------------------------------------")
  println("EXECUTION FINISHED")
  println("------------------------------------------")
  println
  println("------------------------------------------")
  println("RESULTS:")
  println("------------------------------------------")
  println
  println("Convergence: ")
  println("--------------------")
  
  ConvergenceObserver.simpleVertices = simpleGraphList
  
  if(ConvergenceObserver.checkGlobalMessageConvergence()){
    println("--> Messages globally converged!")
  }else{
    println("--> No message convergence!")
  }

  if(ConvergenceObserver.checkGlobalStateConvergence()){
    println("--> States globally converged!")
  }else{
    println("--> No state convergence!")
  }

  
  println
  println("Vertices: ")
  println("---------")
  signalCollectFactorGraph.foreachVertex{v => 
    val vertex = v.id.asInstanceOf[MaxSumId]
    if(vertex.isVariable){
    	println(vertex.id + " has state: " + v.state)
    }
  }
  
  var stateMap : HashMap[MaxSumId,Int] = HashMap() 
  
  signalCollectFactorGraph.foreachVertex{ v=>
    stateMap += (v.id.asInstanceOf[MaxSumId] -> v.state.asInstanceOf[Int])
  }
  
  val observer = new OptimumObserver(stateMap, simpleGraphList)
  if(observer.optimumFound == true){
    println("--> Optimal Solution found!")
  }else{
    println("--> No optimal Solution found!")
  }
  
//  println
//  println("StepCounter : ")
//  println("---------")
//  simpleGraphList.foreach{ el =>
//    println(el.variableVertex.id.id + " steps: " + el.variableVertex.stepCounter )
//    println(el.functionVertex.id.id + " steps: " + el.functionVertex.stepCounter )
//  }  
  
  println
  println("Num of conflicts over time: ")
  GlobalMeasurer.maxsumInstrument.conflictsOverTime.foreach{ tuple =>
    println("Step "+tuple._1 + " - " + tuple._2)
  }
  
  
  signalCollectFactorGraph.shutdown
  
  
  def initializePrefs() = {
	var color = 0  
    simpleGraphList.foreach{el =>
      val pref = ArrayBuffer.fill(ProblemConstants.numOfColors)(-0.1)
      val variableId = el.variableVertex.id
	  pref(color) = 0.1
	  ProblemConstants.initialPreferences += (variableId -> pref)
      color = (color + 1) % ProblemConstants.numOfColors
	}
  }
}