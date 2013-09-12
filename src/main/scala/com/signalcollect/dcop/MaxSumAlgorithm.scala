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



object MaxSumAlgorithm extends App{

  val fileName : String = "rectangle.txt"
  
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
  
  ProblemConstants.numOfColors = 2 ; println("Number of Colors = " + ProblemConstants.numOfColors + " initialized")
  ProblemConstants.colors = Set(0,1)
  ProblemConstants.initialPreferences += (new MaxSumId(0,0) -> ArrayBuffer(0.1 , -0.1))
  ProblemConstants.initialPreferences += (new MaxSumId(1,0) -> ArrayBuffer(-0.1 , 0.1))
  ProblemConstants.initialPreferences += (new MaxSumId(2,0) -> ArrayBuffer(-0.1 , 0.1))
  ProblemConstants.initialPreferences += (new MaxSumId(3,0) -> ArrayBuffer(0.1 , -0.1))
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
      ExecutionMode.ContinuousAsynchronous).withTimeLimit(15000))
  println(stats)

  signalCollectFactorGraph.foreachVertex(println(_))
  signalCollectFactorGraph.shutdown
  
  println("------------------------------------------")
  println("EXECUTION FINISHED")
  println("------------------------------------------")
  
}