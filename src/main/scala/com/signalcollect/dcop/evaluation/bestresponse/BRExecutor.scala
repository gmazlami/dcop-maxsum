package com.signalcollect.dcop.evaluation.bestresponse

import com.signalcollect.StateForwarderEdge
import com.signalcollect.interfaces.AggregationOperation
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.dcop.evaluation.candidates.BestResponseVertexBuilder
import com.signalcollect.dcop.evaluation.candidates.BinaryConstraintGraphProvider
import com.signalcollect.GraphBuilder
import com.signalcollect.configuration.TerminationReason
import com.signalcollect.dcop.util.ProblemConstants

class BRExecutor(file: String, config: ExecutionConfiguration,  isAdopt: Boolean, aggregation: AggregationOperation[Int], randomInit: Boolean, p: Double, graphSize: Int) {

  val fileName = file
  val numColors = ProblemConstants.numOfColors
  val executionConfig = config
  val isInputAdopt = isAdopt
  val randomInitialState = randomInit
  val probability = p
  val aggregator = aggregation

  var graph: Graph[Any, Any] = _

  val algorithm = new BestResponseVertexBuilder(randomInitialState, probability)
  val graphProvider: BinaryConstraintGraphProvider = new BinaryConstraintGraphProvider(graphSize, numColors, 2, loadFrom = file, isAdopt = isInputAdopt)
  val graphBuilder = new GraphBuilder[Any, Any]()

  var conflictsOverTime: Map[Int, Int] = Map()

  // What edgeBuilder to use with this algorithm
  val edgeBuilder = algorithm match {
    case otherwise => (x: Int, y: Int) => new StateForwarderEdge(y)
  }

  graph = graphBuilder.build
  graphProvider.populate(graph, algorithm, edgeBuilder)
  graph.awaitIdle

  def executeWithAggregation(): Int = {
    if (aggregation != null) {
      graph.execute(config)
      graph.aggregate(aggregator)
    } else {
      -1
    }
  }

  def executeForConvergenceSteps(): Long = {
    val executionInfo = graph.execute(executionConfig)
    if (executionInfo.executionStatistics.terminationReason == TerminationReason.Converged) {
      executionInfo.executionStatistics.signalSteps
    } else {
      -1
    }
  }

  def executeForConvergenceTime(): Long = {
    val executionInfo = graph.execute(executionConfig)
    if (executionInfo.executionStatistics.terminationReason == TerminationReason.Converged) {
      executionInfo.executionStatistics.computationTime.toMillis
    } else {
      -1
    }
  }
}