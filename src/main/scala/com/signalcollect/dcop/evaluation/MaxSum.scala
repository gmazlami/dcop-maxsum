package com.signalcollect.dcop.evaluation

import com.signalcollect.dcop.io.FileGraphReader
import com.signalcollect.dcop.graphs.FactorGraphTransformer
import com.signalcollect.dcop.util.ProblemConstants
import com.signalcollect.dcop.evaluation.statistics.GlobalMeasurer
import com.signalcollect.dcop.evaluation.statistics.MeasuringInstrument
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.dcop.evaluation.statistics.ConvergenceObserver
import com.signalcollect.dcop.vertices.id.MaxSumId
import com.signalcollect.dcop.evaluation.statistics.OptimumObserver
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.ExecutionInformation

class MaxSum(file: String, config: ExecutionConfiguration, numOfColors : Int) {

  val fileName = file
  val numColors = numOfColors
  val executionConfig = config
  val reader: FileGraphReader = new FileGraphReader
  val transformer: FactorGraphTransformer = new FactorGraphTransformer
  val simpleGraph = reader.readToMap(fileName)
  val simpleGraphList = reader.readToList(fileName)
  var statistics : ExecutionInformation = null

  ProblemConstants.globalVertexList = simpleGraphList

  val signalCollectFactorGraph = transformer.transform(simpleGraph)

  initializePrefs()
  
  reader.storeNeighborStructure(simpleGraphList, simpleGraph)
  simpleGraph.foreach { entry =>
    entry._2.functionVertex.initializeReceivedMessages
    entry._2.variableVertex.initializeReceivedMessages
  }

  GlobalMeasurer.maxsumInstrument = new MeasuringInstrument("Max-Sum", simpleGraphList)

  def execute() = {
    signalCollectFactorGraph.awaitIdle
    val stats = signalCollectFactorGraph.execute(executionConfig)
    println(stats)
    statistics = stats
  }

  def checkConvergence() = {
    ConvergenceObserver.simpleVertices = simpleGraphList

    if (ConvergenceObserver.checkGlobalMessageConvergence()) {
      println("--> Messages globally converged!")
    } else {
      println("--> No message convergence!")
    }

    if (ConvergenceObserver.checkGlobalStateConvergence()) {
      println("--> States globally converged!")
    } else {
      println("--> No state convergence!")
    }
  }

  def printVertexStates() = {
    println("Vertices: ")
    println("---------")
    signalCollectFactorGraph.foreachVertex { v =>
      val vertex = v.id.asInstanceOf[MaxSumId]
      if (vertex.isVariable) {
        println(vertex.id + " has state: " + v.state)
      }
    }
  }

  def checkOptimalSolution() = {
    var stateMap: HashMap[MaxSumId, Int] = HashMap()

    signalCollectFactorGraph.foreachVertex { v =>
      if(v.id.asInstanceOf[MaxSumId].isVariable){
    	  stateMap += (v.id.asInstanceOf[MaxSumId] -> v.state.asInstanceOf[Int])        
      }
    }

    val observer = new OptimumObserver(stateMap, simpleGraphList)
    if (observer.optimumFound == true) {
      println("--> Optimal Solution found!")
    } else {
      println("--> No optimal Solution found!")
    }

  }
  
  private def initializePrefs() = {
    ProblemConstants.numOfColors = numColors
	var color = 0  
    simpleGraphList.foreach{el =>
      val pref = ArrayBuffer.fill(ProblemConstants.numOfColors)(-0.1)
      val variableId = el.variableVertex.id
	  pref(color) = 0.1
	  ProblemConstants.initialPreferences += (variableId -> pref)
      color = (color + 1) % ProblemConstants.numOfColors
	}
  }
}
