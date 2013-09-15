
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

object Evaluation extends App {
  var graph: Graph[Any, Any] = _

  val DSAAalgorithm = new DSAVertexBuilder(false, DSAVariant.A, pSchedule = 0.45)
  val DSABalgorithm = new DSAVertexBuilder(false, DSAVariant.B, pSchedule = 0.45)
  val BRalgorithm = new BestResponseVertexBuilder(randomInitialState = false, 0.4)

  val graphProvider: BinaryConstraintGraphProvider = new BinaryConstraintGraphProvider(4, 2, 2, loadFrom = "graphs/rectangle.txt")

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