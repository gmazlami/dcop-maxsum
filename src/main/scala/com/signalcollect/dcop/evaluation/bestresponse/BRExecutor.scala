package com.signalcollect.dcop.evaluation.bestresponse

import com.signalcollect.StateForwarderEdge
import com.signalcollect.interfaces.AggregationOperation
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.dcop.evaluation.candidates.BestResponseVertexBuilder
import com.signalcollect.dcop.evaluation.candidates.BinaryConstraintGraphProvider
import com.signalcollect.GraphBuilder

class BRExecutor(file: String, config: ExecutionConfiguration, numOfColors : Int, isAdopt : Boolean, aggregation : AggregationOperation[Int], randomInit : Boolean, p : Double) {

  val fileName = file
  val numColors = numOfColors
  val executionConfig = config
  val isInputAdopt = isAdopt
  val randomInitialState = randomInit
  val probability = p
  val aggregator = aggregation
  
  var graph: Graph[Any, Any] = _

  val algorithm = new BestResponseVertexBuilder(randomInitialState,probability)
  val graphProvider: BinaryConstraintGraphProvider = new BinaryConstraintGraphProvider(4, 2, 2, loadFrom = file, isAdopt = isInputAdopt)
  val graphBuilder = new GraphBuilder[Any, Any]()
  
  var conflictsOverTime : Map[Int,Int] = Map()
  
  // What edgeBuilder to use with this algorithm
  val edgeBuilder = algorithm match {
    case otherwise => (x: Int, y: Int) => new StateForwarderEdge(y)
  }

  graph = graphBuilder.build
  graphProvider.populate(graph, algorithm, edgeBuilder)
  graph.awaitIdle
  
  
  
  def executeWithAggregation() : Int = {
    if(aggregation != null){
      graph.execute(config)
      graph.aggregate(aggregator)
    }else{
    	-1
    }
  }
  
  def executeWithEndResult() : Int = {
    0
  }
}