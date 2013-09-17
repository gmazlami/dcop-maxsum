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

import com.signalcollect.StateForwarderEdge
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.dcop.evaluation.candidates.BinaryConstraintGraphProvider
import com.signalcollect.dcop.evaluation.candidates.DSAVertexBuilder
import com.signalcollect.dcop.evaluation.candidates.BestResponseVertexBuilder
import com.signalcollect.Graph
import com.signalcollect.dcop.evaluation.candidates.DSAVariant
import com.signalcollect.GraphBuilder
import com.signalcollect.dcop.evaluation.candidates.ConstrainedVertex
import com.signalcollect.configuration.ExecutionMode

object CandidateAlgorithms extends App {
  var graph: Graph[Any, Any] = _

  val DSAAalgorithm = new DSAVertexBuilder(false, DSAVariant.A, pSchedule = 0.45)
  val DSABalgorithm = new DSAVertexBuilder(false, DSAVariant.B, pSchedule = 0.45)
  val BRalgorithm = new BestResponseVertexBuilder(randomInitialState = false, 0.4)

  val graphProvider: BinaryConstraintGraphProvider = new BinaryConstraintGraphProvider(4, 2, 2, loadFrom = "graphs/rectangle.txt", isAdopt = false)

  val algorithm = DSAAalgorithm

  val graphBuilder = new GraphBuilder[Any, Any]()

  // Get the adjustment schedule associated with this algorithm
  val execMode = (algorithm.apply(0, Array(1, 2, 3)).asInstanceOf[ConstrainedVertex[_, _]]).underlyingSignalCollectSchedule

  // Set time limit
  val executionConfig = {
    if (execMode == ExecutionMode.PureAsynchronous)
      ExecutionConfiguration(execMode).withSignalThreshold(0.01).withTimeLimit(5000)
    else
      ExecutionConfiguration(execMode).withSignalThreshold(0.01).withStepsLimit(250)
  }

  // The interval in which to log stats
  val aggregationInterval = if (execMode == ExecutionMode.PureAsynchronous) 10 else 1

  // What edgeBuilder to use with this algorithm
  val edgeBuilder = algorithm match {
    case otherwise => (x: Int, y: Int) => new StateForwarderEdge(y)
  }

  val vertexBuilder = algorithm

  graph = graphBuilder.build
  graphProvider.populate(graph, vertexBuilder, edgeBuilder)
  graph.awaitIdle

  val stats = graph.execute(executionConfig)
  println(stats)

}