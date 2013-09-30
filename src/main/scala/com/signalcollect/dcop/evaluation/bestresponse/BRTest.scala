package com.signalcollect.dcop.evaluation.bestresponse

import com.signalcollect.Graph
import com.signalcollect.dcop.evaluation.candidates.BestResponseVertexBuilder
import com.signalcollect.dcop.evaluation.candidates.BinaryConstraintGraphProvider
import com.signalcollect.GraphBuilder
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.StateForwarderEdge
import com.signalcollect.configuration.ExecutionMode

object BRTest extends App {

  var graph: Graph[Any, Any] = _
  
  val algorithm = new BestResponseVertexBuilder(randomInitialState=false, 0.6)
  
  val graphProvider: BinaryConstraintGraphProvider = new BinaryConstraintGraphProvider(4, 2, 2, loadFrom = "graphs/ADOPT/adopt10.txt", isAdopt = true)

  val graphBuilder = new GraphBuilder[Any, Any]()

  val executionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0)
  

  // What edgeBuilder to use with this algorithm
  val edgeBuilder = algorithm match {
    case otherwise => (x: Int, y: Int) => new StateForwarderEdge(y)
  }

  val vertexBuilder = algorithm

  graph = graphBuilder.build
  graphProvider.populate(graph, vertexBuilder, edgeBuilder)
  graph.awaitIdle
  val stats = graph.execute(executionConfig)
  graph.foreachVertex(println(_))
  println(stats) 
  
 

}