package com.signalcollect.dcop.evaluation.maxsum

import com.signalcollect.ExecutionConfiguration
import com.signalcollect.interfaces.AggregationOperation
import com.signalcollect.dcop.io.FileGraphReader
import com.signalcollect.dcop.graphs.FactorGraphTransformer
import com.signalcollect.ExecutionInformation
import com.signalcollect.dcop.util.ProblemConstants
import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import com.signalcollect.configuration.TerminationReason
import com.signalcollect.dcop.vertices.VariableVertex
import com.signalcollect.dcop.vertices.MaxSumVertex

class MaxSumExecutor(file: String, config: ExecutionConfiguration, numOfColors: Int, isAdopt: Boolean, aggregation: AggregationOperation[Int]) {

  val fileName = file
  val numColors = numOfColors
  val executionConfig = config
  val reader: FileGraphReader = new FileGraphReader
  val transformer: FactorGraphTransformer = new FactorGraphTransformer
  var statistics: ExecutionInformation = null
  val isInputAdopt = isAdopt
  val simpleGraph = if (isInputAdopt) reader.readFromAdoptFileToMap(fileName) else reader.readToMap(fileName)
  val simpleGraphList = if (isInputAdopt) reader.readFromAdoptFileToList(fileName) else reader.readToList(fileName)
  val signalCollectFactorGraph = transformer.transform(simpleGraph)

  val intervalList = List(500, 1000, 1500, 2500, 5000, 7500, 10000)

  initializeRandom()

  def executeWithAggregation(): Int = {
    if (aggregation != null) {
      signalCollectFactorGraph.execute(executionConfig)
      signalCollectFactorGraph.foreachVertexWithGraphEditor(ge => { vertex =>
        {
          vertex match {
            case v: VariableVertex => v.tellNeighborsAboutColor(ge)
            case f: Any =>
          }
        }
      })
      signalCollectFactorGraph.aggregate(aggregation)
    } else {
      -1
    }
  }

  def executeForConflictsOverTime() = {
    var resultList: List[Tuple2[Long, Int]] = List()
    if (aggregation != null) {
      intervalList.foreach { interval =>
        val longInterval = interval.asInstanceOf[Long]
        signalCollectFactorGraph.execute(copyExecutionConfigWithTimeLimit(interval, executionConfig))
        signalCollectFactorGraph.foreachVertexWithGraphEditor(ge => { vertex =>
          {
            vertex match {
              case v: VariableVertex => v.tellNeighborsAboutColor(ge)
            }
          }
        })
        resultList :+= (longInterval, signalCollectFactorGraph.aggregate(aggregation))
      }
    }
    resultList
  }

  def executeForConvergenceSteps(): Long = {
    val executionInfo = signalCollectFactorGraph.execute(executionConfig)
    if (executionInfo.executionStatistics.terminationReason == TerminationReason.Converged) {
      executionInfo.executionStatistics.signalSteps
    } else {
      -1
    }
  }

  def executeForConvergenceTime(): Long = {
    val executionInfo = signalCollectFactorGraph.execute(executionConfig)
    if (executionInfo.executionStatistics.terminationReason == TerminationReason.Converged) {
      executionInfo.executionStatistics.computationTime.toMillis
    } else {
      -1
    }
  }

  private def initializeRandom() = {
    ProblemConstants.numOfColors = numColors
    simpleGraphList.foreach { el =>
      val pref = ArrayBuffer.fill(ProblemConstants.numOfColors)(-0.1)
      val variableId = el.variableVertex.id
      val index = Random.nextInt(ProblemConstants.numOfColors)
      pref(index) = 0.1
      ProblemConstants.initialPreferences += (variableId -> pref)
    }

    reader.storeNeighborStructure(simpleGraphList, simpleGraph)
    simpleGraph.foreach { entry =>
      entry._2.functionVertex.initializeReceivedMessages
      entry._2.variableVertex.initializeReceivedMessages
    }
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

  private def copyExecutionConfigWithTimeLimit(t: Long, config: ExecutionConfiguration) = {
    new ExecutionConfiguration().withExecutionMode(config.executionMode).withSignalThreshold(config.signalThreshold).withCollectThreshold(config.collectThreshold).withTimeLimit(t)
  }
}