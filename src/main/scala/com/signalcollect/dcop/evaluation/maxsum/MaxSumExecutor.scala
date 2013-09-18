package com.signalcollect.dcop.evaluation.maxsum

import com.signalcollect.ExecutionConfiguration
import com.signalcollect.interfaces.AggregationOperation
import com.signalcollect.dcop.io.FileGraphReader
import com.signalcollect.dcop.graphs.FactorGraphTransformer
import com.signalcollect.ExecutionInformation
import com.signalcollect.dcop.util.ProblemConstants
import scala.collection.mutable.ArrayBuffer

class MaxSumExecutor(file: String, config: ExecutionConfiguration, numOfColors : Int, isAdopt : Boolean, aggregation : AggregationOperation[Int]) {

  val fileName = file
  val numColors = numOfColors
  val executionConfig = config
  val reader: FileGraphReader = new FileGraphReader
  val transformer: FactorGraphTransformer = new FactorGraphTransformer
  var statistics : ExecutionInformation = null
  val isInputAdopt = isAdopt
  val simpleGraph = if(isInputAdopt) reader.readFromAdoptFileToMap(fileName) else reader.readToMap(fileName)
  val simpleGraphList = if(isInputAdopt) reader.readFromAdoptFileToList(fileName) else reader.readToList(fileName)
  val signalCollectFactorGraph = transformer.transform(simpleGraph)
  
  ProblemConstants.globalVertexList = simpleGraphList
  initialize()

  
  def executeWithAggregation() : Int = {
    var conflicts = -1
    if(aggregation != null){
    	signalCollectFactorGraph.execute(executionConfig)
    	conflicts =  signalCollectFactorGraph.aggregate(new MaxSumConflictAggregationOperation)
    	conflicts
    }
    conflicts
  }
  
  def executeWithEndResult() : Int = {
    0
  }
  
  
  
  private def initialize() = {
    ProblemConstants.numOfColors = numColors
    var color = 0
    simpleGraphList.foreach { el =>
      val pref = ArrayBuffer.fill(ProblemConstants.numOfColors)(-0.1)
      val variableId = el.variableVertex.id
      pref(color) = 0.1
      ProblemConstants.initialPreferences += (variableId -> pref)
      color = (color + 1) % ProblemConstants.numOfColors
    }

    reader.storeNeighborStructure(simpleGraphList, simpleGraph)
    simpleGraph.foreach { entry =>
      entry._2.functionVertex.initializeReceivedMessages
      entry._2.variableVertex.initializeReceivedMessages
    }
  }
}