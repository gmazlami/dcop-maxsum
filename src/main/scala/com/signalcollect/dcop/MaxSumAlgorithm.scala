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

  println("Starting MaxSum Algorithm...")
  println
  
  val reader : FileGraphReader = new FileGraphReader
  val transformer : FactorGraphTransformer = new FactorGraphTransformer
  
  println("Reading simple graph from txt-File")
  
  val simpleGraph = reader.readToMap("rectangle.txt")
  val simpleGraphList = reader.readToList("rectangle.txt")
  
  println("Reading of simple graph succesfully completed.")
  println
  println("Transforming simple graph started.")
  
  val signalCollectFactorGraph = transformer.transform(simpleGraph)
  
  println("Transformation to Signal/Collect graph successfully completed.")
  println
  println("Initialization of Problem-Constants:")
  
  ProblemConstants.numOfColors = 2 ; println("Number of Colors = " + ProblemConstants.numOfColors + " initialized")
  ProblemConstants.colors = Set(0,1)
  ProblemConstants.initialPreferences += (new MaxSumId(1,0) -> ArrayBuffer(0.1 , -0.1))
  ProblemConstants.initialPreferences += (new MaxSumId(2,0) -> ArrayBuffer(-0.1 , 0.1))
  ProblemConstants.initialPreferences += (new MaxSumId(3,0) -> ArrayBuffer(-0.1 , 0.1))
  ProblemConstants.initialPreferences += (new MaxSumId(4,0) -> ArrayBuffer(0.1 , -0.1))
  println("Preferences initialized.")
  
  reader.storeNeighborStructure(simpleGraphList, simpleGraph)
  simpleGraph.foreach{entry =>
    entry._2.functionVertex.initializeReceivedMessages
    entry._2.variableVertex.initializeReceivedMessages
  }  
  
  
  signalCollectFactorGraph.awaitIdle
  println
  println("------------------------------------------")
  println("EXECUTION STARTED...")
  
  val stats = signalCollectFactorGraph.execute(ExecutionConfiguration.withExecutionMode(
      ExecutionMode.ContinuousAsynchronous).withTimeLimit(15000))
  println(stats)

  signalCollectFactorGraph.foreachVertex(println(_))
  signalCollectFactorGraph.shutdown
  println("FINISHED")
  
}