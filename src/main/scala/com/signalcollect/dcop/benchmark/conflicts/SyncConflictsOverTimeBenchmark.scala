package com.signalcollect.dcop.benchmark.conflicts

import com.signalcollect.dcop.io.ResultWriter
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.dcop.evaluation.maxsum.MaxSumAlgorithm
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.evaluation.maxsum.MaxSumConflictAggregationOperation
import com.signalcollect.dcop.benchmark.BenchmarkModes
import com.signalcollect.dcop.benchmark.BenchmarkConfiguration

object SyncConflictsOverTimeBenchmark extends App {

  /*
   * general properties
   */
  val fileName = "graphs/ADOPT/adopt10.txt"
  val graphName = "adopt10"
  val isAdopt = true
  val steps = 5
  val timeLimit = 10000
  val numColors = 3
  //------------------------------------------------

  /*
   * properties for sync MaxSum
   */
  val syncMaxSumName = "MaxSumSync"
  val syncBenchmarkMode = BenchmarkModes.SyncConflictsOverTime
  val syncMSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0).withTimeLimit(timeLimit)
  val syncMSbenchmarkConfig = new BenchmarkConfiguration(syncMSexecutionConfig, fileName, isAdopt, steps, new MaxSumConflictAggregationOperation, numColors, syncBenchmarkMode)
  val syncMaxSumAlgorithm = new MaxSumAlgorithm(syncMSbenchmarkConfig)

  
  /*
   * results
   */
  var syncMaxSumConflicts : List[Tuple2[Long, Int]] = List()

  /*
   * run evaluation for Synchronous MaxSum:
   */
  println("Evaluating Synchronous Max-Sum...")
  syncMaxSumAlgorithm.runEvaluation()
  syncMaxSumConflicts = syncMaxSumAlgorithm.getResult().asInstanceOf[List[Tuple2[Long, Int]]]
  println(syncMaxSumConflicts)
  println("Synchronous Max-Sum evaluted.")
  storeResultsToFile(syncMaxSumConflicts, syncMaxSumName, syncBenchmarkMode)
  println("-----------------------")


  System.exit(0)
  
  def storeResultsToFile(results: Any, algorithm: String, benchmarkMode: BenchmarkModes.Value) = {
    val resultWriter = new ResultWriter(benchmarkMode, graphName, algorithm, results)
    resultWriter.write()
  }
}