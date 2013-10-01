package com.signalcollect.dcop.benchmark.convergence

import com.signalcollect.dcop.benchmark.BenchmarkModes
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.dcop.benchmark.BenchmarkConfiguration
import com.signalcollect.dcop.evaluation.maxsum.MaxSumAlgorithm
import com.signalcollect.configuration.ExecutionMode

object AsyncTimeToConvergence extends App {
	
  /*
   * general properties
   */
  val fileName = "graphs/ADOPT/adopt20.txt"
  val graphName = "adopt10"
  val isAdopt = true
  val steps = 5
  val numColors = 3
  //------------------------------------------------

  /*
   * properties for async MaxSum
   */
  val asyncMaxSumName = "MaxSumAsync"
  val asyncBenchmarkMode = BenchmarkModes.AsyncTimeToConvergence
  val asyncMSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.PureAsynchronous).withCollectThreshold(0).withSignalThreshold(0)
  val asyncMSbenchmarkConfig = new BenchmarkConfiguration(asyncMSexecutionConfig, fileName, isAdopt, steps, null, numColors, asyncBenchmarkMode)
  val asyncMaxSumAlgorithm = new MaxSumAlgorithm(asyncMSbenchmarkConfig)

  /*
   * results
   */
  var asyncTimeToConvergence: Long = 0

  /*
   * run evaluation for Asynchronous MaxSum:
   */
  println("Evaluating Asynchronous Max-Sum...")
  asyncMaxSumAlgorithm.runEvaluation()
  asyncTimeToConvergence = asyncMaxSumAlgorithm.getResult().asInstanceOf[Long]
  println(asyncTimeToConvergence)
  println("Asynchronous Max-Sum evaluted.")
  println("-----------------------")

  System.exit(0)
}