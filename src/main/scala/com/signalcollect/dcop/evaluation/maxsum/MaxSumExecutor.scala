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
import com.signalcollect.dcop.vertices.id.MaxSumId
import com.signalcollect.Graph
import com.signalcollect.dcop.vertices.FunctionVertex
import scala.collection.mutable.HashMap

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
  var constants = computeAllConstants()
  val signalCollectFactorGraph = transformer.transform(simpleGraph, constants)

  val intervalList = List(500, 1000, 1500, 2500, 5000, 7500, 10000)


  signalCollectFactorGraph.awaitIdle
  initilalizeMessages()

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
              case x: Any =>
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


  private def copyExecutionConfigWithTimeLimit(t: Long, config: ExecutionConfiguration) = {
    new ExecutionConfiguration().withExecutionMode(config.executionMode).withSignalThreshold(config.signalThreshold).withCollectThreshold(config.collectThreshold).withTimeLimit(t)
  }

  def initilalizeMessages() = {
    simpleGraph.foreach { entry =>
      entry._2.functionVertex.initializeReceivedMessages
      entry._2.variableVertex.initializeReceivedMessages
    }
  }

  def getRandomPreference(ownedVar: MaxSumId) = {
    val pref = ArrayBuffer.fill(ProblemConstants.numOfColors)(-0.1)
    val variableId = ownedVar
    val index = Random.nextInt(ProblemConstants.numOfColors)
    pref(index) = 0.1
    pref
  }

  def getNeighborSet(functionIdNum: Int, ownedVar: MaxSumId) = {
    var neighborSetForFunction: ArrayBuffer[MaxSumId] = reader.getNeighbors(functionIdNum, simpleGraph)
    neighborSetForFunction += ownedVar
    neighborSetForFunction
  }

  def getConstants(idNum: Int) = {
    val ownedVariable: MaxSumId = new MaxSumId(idNum, 0)
    val randomPreferenceTable = getRandomPreference(ownedVariable)
    val neighborSet = getNeighborSet(idNum, ownedVariable)
    (ownedVariable, randomPreferenceTable, neighborSet)
  }

  def computeAllConstants() = {
    var allConstants : HashMap[MaxSumId, Tuple3[MaxSumId, ArrayBuffer[Double], ArrayBuffer[MaxSumId]]] = HashMap()
    simpleGraphList.foreach { s =>
      allConstants += (s.functionVertex.id -> getConstants(s.functionVertex.id.idNumber))
    }
    allConstants
  }
}