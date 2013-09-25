package com.signalcollect.dcop.benchmark.conflicts

import com.signalcollect.dcop.io.ResultWriter
import com.signalcollect.dcop.evaluation.maxsum.MaxSumConflictAggregationOperation
import com.signalcollect.dcop.evaluation.maxsum.MaxSumAlgorithm
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.benchmark.BenchmarkModes
import com.signalcollect.dcop.benchmark.BenchmarkConfiguration

object AsyncConflictsOverTimeBenchmark extends App {

  /*
   * general properties
   */
  val fileName = "graphs/ADOPT/adopt40.txt"
  val graphName = "adopt10"
  val isAdopt = true
  val steps = 5
  val timeLimit = 10000
  val numColors = 3
  //------------------------------------------------

  /*
   * properties for async MaxSum
   */
  val asyncMaxSumName = "MaxSumAsync"
  val asyncBenchmarkMode = BenchmarkModes.AsyncConflictsOverTime
  val asyncMSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.PureAsynchronous).withCollectThreshold(0).withSignalThreshold(0).withTimeLimit(timeLimit)
  val asyncMSbenchmarkConfig = new BenchmarkConfiguration(asyncMSexecutionConfig, fileName, isAdopt, steps, new MaxSumConflictAggregationOperation, numColors, asyncBenchmarkMode)
  val asyncMaxSumAlgorithm = new MaxSumAlgorithm(asyncMSbenchmarkConfig)

  /*
   * results
   */
  var asyncMaxSumConflicts: List[Tuple2[Long, Int]] = List()

  /*
   * run evaluation for Asynchronous MaxSum:
   */
  println("Evaluating Asynchronous Max-Sum...")
  asyncMaxSumAlgorithm.runEvaluation()
  asyncMaxSumConflicts = asyncMaxSumAlgorithm.getResult().asInstanceOf[List[Tuple2[Long, Int]]]
  println(asyncMaxSumConflicts)
  println("Asynchronous Max-Sum evaluted.")
  storeResultsToFile(asyncMaxSumConflicts, asyncMaxSumName, asyncBenchmarkMode)
  println("-----------------------")

  System.exit(0)

  def storeResultsToFile(results: Any, algorithm: String, benchmarkMode: BenchmarkModes.Value) = {
    val resultWriter = new ResultWriter(benchmarkMode, graphName, algorithm, results)
    resultWriter.write()
  }
}