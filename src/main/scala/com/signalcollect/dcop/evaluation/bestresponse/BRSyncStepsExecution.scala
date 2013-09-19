package com.signalcollect.dcop.evaluation.bestresponse

import com.signalcollect.dcop.evaluation.candidates.BestResponseVertexBuilder
import com.signalcollect.Graph
import com.signalcollect.dcop.evaluation.candidates.BinaryConstraintGraphProvider
import com.signalcollect.GraphBuilder
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.StateForwarderEdge
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.evaluation.candidates.ConstrainedVertex

object BRSyncStepsExecution extends App {

  var graph: Graph[Any, Any] = _
  
  val algorithm = new BestResponseVertexBuilder(randomInitialState=false, 0.6)
  
  val graphProvider: BinaryConstraintGraphProvider = new BinaryConstraintGraphProvider(4, 2, 2, loadFrom = "graphs/test.txt", isAdopt = true)

  val graphBuilder = new GraphBuilder[Any, Any]()

//  // Get the adjustment schedule associated with this algorithm
//  val execMode = (algorithm.apply(0, Array(1, 2, 3)).asInstanceOf[ConstrainedVertex[_, _]]).underlyingSignalCollectSchedule
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
  val edgeBuilder = algorithm match {
    case otherwise => (x: Int, y: Int) => new StateForwarderEdge(y)
  }

  val vertexBuilder = algorithm

  graph = graphBuilder.build
  graphProvider.populate(graph, vertexBuilder, edgeBuilder)
  graph.awaitIdle

  var conflictsOverTime : Map[Int,Int] = Map()
  
  for(i <- 0 to 20){
	  graph.execute(executionConfig)
	  val conflicts = graph.aggregate(new BRConflictAggregationOperation)
	  println(i + " -> " + conflicts)
	  conflictsOverTime += (i -> conflicts)
  }
 
  
}