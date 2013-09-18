package com.signalcollect.dcop.evaluation.dsa

import com.signalcollect.ExecutionConfiguration
import com.signalcollect.interfaces.AggregationOperation
import com.signalcollect.Graph
import com.signalcollect.dcop.evaluation.candidates.DSAVariant
import com.signalcollect.dcop.evaluation.candidates.DSAVertexBuilder
import com.signalcollect.dcop.evaluation.candidates.BinaryConstraintGraphProvider
import com.signalcollect.GraphBuilder

class DSAExecutor(file: String, config: ExecutionConfiguration, numOfColors : Int, isAdopt : Boolean, aggregation : AggregationOperation[Int], variant : DSAVariant.Value, p : Double) {

  val fileName = file
  val numColors = numOfColors
  val executionConfig = config
  val isInputAdopt = isAdopt
  val dsaVariant = variant
  val pSched = p
  
  
  var graph: Graph[Any, Any] = _

  val algorithm = new DSAVertexBuilder(false, dsaVariant, pSchedule = pSched)
  val graphProvider: BinaryConstraintGraphProvider = new BinaryConstraintGraphProvider(4, 2, 2, loadFrom = file, isAdopt = isInputAdopt)
  val graphBuilder = new GraphBuilder[Any, Any]()

  
  def executeWithAggregation : Int = {
    0
  }
  
  def executeWithEndResult : Int = {
    0
  }

}