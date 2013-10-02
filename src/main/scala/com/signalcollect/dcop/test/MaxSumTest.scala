package com.signalcollect.dcop.test

import com.signalcollect.dcop.util.ProblemConstants
import com.signalcollect.dcop.io.FileGraphReader
import com.signalcollect.dcop.graphs.FactorGraphTransformer
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.dcop.termination.OptimalSolutionTerminationCondition
import com.signalcollect.dcop.vertices.MaxSumVertex
import com.signalcollect.dcop.evaluation.maxsum.MaxSumConflictAggregationOperation
import scala.util.Random
import com.signalcollect.dcop.vertices.VariableVertex

object MaxSumTest extends App {

  val fileName: String = "graphs/ADOPT/adopt10.txt"

  println("--------------------------------------------------")
  println("STARTING INITIALIZATION")
  println("--------------------------------------------------")

  val reader: FileGraphReader = new FileGraphReader
  val transformer: FactorGraphTransformer = new FactorGraphTransformer

  println
  println("--------------------------------------------------")
  println("Reading simple graph from txt-File: " + fileName)

//    val simpleGraph = reader.readToMap(fileName)
//    val simpleGraphList = reader.readToList(fileName)

  val simpleGraph = reader.readFromAdoptFileToMap(fileName)
  val simpleGraphList = reader.readFromAdoptFileToList(fileName)

  println("Reading of simple graph succesfully completed.")
  println("--------------------------------------------------")
  println
  println("--------------------------------------------------")
  println("Transformation of simple graph to Signal-Collect graph started.")

  val signalCollectFactorGraph = transformer.transform(simpleGraph)

  println("Transformation to Signal/Collect graph successfully completed.")
  println("--------------------------------------------------")

  println

  println("--------------------------------------------------")
  println("Initialization of Problem-Constants started")

  ProblemConstants.numOfColors = 2
  println("Number of Colors = " + ProblemConstants.numOfColors + " initialized")
  initializeRandom()

  println("Preferences initialized.")

  reader.storeNeighborStructure(simpleGraphList, simpleGraph)

  println("Initializing initial Messages at vertices with values (0.0 , 0.0 , 0.0 ... 0,0)")

  simpleGraph.foreach { entry =>
    entry._2.functionVertex.initializeReceivedMessages
    entry._2.variableVertex.initializeReceivedMessages
  }

  println("Initialization of global problem constants successfully completed.")
  println("------------------------------------------")
  println("INITIALIZATION COMPLETED.")
  println("------------------------------------------")

  println

  println("------------------------------------------")
  println("EXECUTION OF MAX-SUM ALGORITHM STARTED:")
  println("------------------------------------------")
  println

  signalCollectFactorGraph.awaitIdle
  val stats = signalCollectFactorGraph.execute(ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0))
  println(stats)

  signalCollectFactorGraph.foreachVertex(println(_))

  println("------------------------------------------")
  println("EXECUTION FINISHED")
  println("------------------------------------------")

  signalCollectFactorGraph.shutdown

  def initializePrefs() = {
    var color = 0
    simpleGraphList.foreach { el =>
      val pref = ArrayBuffer.fill(ProblemConstants.numOfColors)(-0.1)
      val variableId = el.variableVertex.id
      pref(0) = 0.1
      ProblemConstants.initialPreferences += (variableId -> pref)
      color = (color + 1) % ProblemConstants.numOfColors
    }
  }

  private def initializeRandom() = {
    simpleGraphList.foreach { el =>
      val pref = ArrayBuffer.fill(ProblemConstants.numOfColors)(-0.1)
      val variableId = el.variableVertex.id
      val index = Random.nextInt(ProblemConstants.numOfColors)
      pref(index) = 0.1
      ProblemConstants.initialPreferences += (variableId -> pref)
    }
  }
}