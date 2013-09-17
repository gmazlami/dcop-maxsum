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

package com.signalcollect.dcop.evaluation.dsa


import com.signalcollect.dcop.evaluation.candidates.BinaryConstraintGraphProvider
import com.signalcollect.Graph
import com.signalcollect.dcop.evaluation.candidates.DSAVertexBuilder
import com.signalcollect.GraphBuilder
import com.signalcollect.dcop.evaluation.candidates.DSAVariant
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.StateForwarderEdge
import com.signalcollect.dcop.aggregation.DSAConflictAggregationOperation


object DSABSyncStepsExecution extends App {

    var graph: Graph[Any, Any] = _

  val DSABalgorithm = new DSAVertexBuilder(false, DSAVariant.B, pSchedule = 0.45)
  
  val graphProvider: BinaryConstraintGraphProvider = new BinaryConstraintGraphProvider(4, 2, 2, loadFrom = "graphs/test.txt", isAdopt = true)

  val graphBuilder = new GraphBuilder[Any, Any]()

//  // Get the adjustment schedule associated with this algorithm
//  val execMode = (DSAAalgorithm.apply(0, Array(1, 2, 3)).asInstanceOf[ConstrainedVertex[_, _]]).underlyingSignalCollectSchedule
//
//  // Set time limit
//  val executionConfig = {
//    if (execMode == ExecutionMode.PureAsynchronous)
//      ExecutionConfiguration(execMode).withSignalThreshold(0.01).withTimeLimit(5000)
//    else
//      ExecutionConfiguration(execMode).withSignalThreshold(0.01).withStepsLimit(250)
//  }

  val executionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0).withStepsLimit(1)
  

  // What edgeBuilder to use with this algorithm
  val edgeBuilder = DSABalgorithm match {
    case otherwise => (x: Int, y: Int) => new StateForwarderEdge(y)
  }

  val vertexBuilder = DSABalgorithm

  graph = graphBuilder.build
  graphProvider.populate(graph, vertexBuilder, edgeBuilder)
  graph.awaitIdle

  var conflictsOverTime : Map[Int,Int] = Map()
  
  for(i <- 0 to 20){
	  graph.execute(executionConfig)
	  val conflicts = graph.aggregate(new DSAConflictAggregationOperation)
	  println(i + " -> " + conflicts)
	  conflictsOverTime += (i -> conflicts)
  }
  
}