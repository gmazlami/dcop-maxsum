package com.signalcollect.dcop.evaluation.dsa

import com.signalcollect.ExecutionConfiguration
import com.signalcollect.interfaces.AggregationOperation
import com.signalcollect.Graph
import com.signalcollect.dcop.evaluation.candidates.DSAVariant
import com.signalcollect.dcop.evaluation.candidates.DSAVertexBuilder
import com.signalcollect.dcop.evaluation.candidates.BinaryConstraintGraphProvider
import com.signalcollect.GraphBuilder
import com.signalcollect.StateForwarderEdge
import com.signalcollect.configuration.TerminationReason

class DSAExecutor(file: String, config: ExecutionConfiguration, numOfColors : Int, isAdopt : Boolean, aggregation : AggregationOperation[Int], variant : DSAVariant.Value, p : Double, graphSize : Int) {

  val fileName = file
  val numColors = numOfColors
  val executionConfig = config
  val isInputAdopt = isAdopt
  val dsaVariant = variant
  val pSched = p
  
  
  var graph: Graph[Any, Any] = _

  val algorithm = new DSAVertexBuilder(false, dsaVariant, pSchedule = pSched)
  val graphProvider: BinaryConstraintGraphProvider = new BinaryConstraintGraphProvider(graphSize, 2, numColors, loadFrom = file, isAdopt = isInputAdopt)
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
      graph.aggregate(aggregation)
    }else{
    	-1
    }
  }
  
  def executeForConvergenceSteps() : Long = {
	val executionInfo = graph.execute(executionConfig)
	if(executionInfo.executionStatistics.terminationReason == TerminationReason.Converged){
		executionInfo.executionStatistics.signalSteps
	}else{
	  -1
	}  
  }
  
  def executeForConvergenceTime() : Long = {
    val executionInfo = graph.execute(executionConfig)
    if(executionInfo.executionStatistics.terminationReason == TerminationReason.Converged){
    	executionInfo.executionStatistics.computationTime.toMillis
    }else{
      -1
    }
  }

}