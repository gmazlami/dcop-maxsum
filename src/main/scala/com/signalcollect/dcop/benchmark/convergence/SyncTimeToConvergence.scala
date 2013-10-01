package com.signalcollect.dcop.benchmark.convergence

import com.signalcollect.dcop.benchmark.BenchmarkModes
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.dcop.benchmark.BenchmarkConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.evaluation.maxsum.MaxSumAlgorithm

object SyncTimeToConvergence extends App {
	
  /*
   * general properties
   */
  val fileName = "graphs/ADOPT/adopt40.txt"
  val graphName = "adopt40"
  val isAdopt = true
  val steps = 5
  val numColors = 3
  //------------------------------------------------

  /*
   * properties for sync MaxSum
   */
  val syncMaxSumName = "MaxSumSync"
  val syncBenchmarkMode = BenchmarkModes.SyncTimeToConvergence
  val syncMSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0)
  val syncMSbenchmarkConfig = new BenchmarkConfiguration(syncMSexecutionConfig, fileName, isAdopt, steps, null, numColors, syncBenchmarkMode)
  val syncMaxSumAlgorithm = new MaxSumAlgorithm(syncMSbenchmarkConfig)

  
  /*
   * results
   */
  var syncTimeToConvergence : Long = 0
  /*
   * run evaluation for Synchronous MaxSum:
   */
  println("Evaluating Synchronous Max-Sum...")
  syncMaxSumAlgorithm.runEvaluation()
  syncTimeToConvergence = syncMaxSumAlgorithm.getResult().asInstanceOf[Long]
  println(syncTimeToConvergence)
  println("Synchronous Max-Sum evaluted.")
  println("-----------------------")


  System.exit(0)
}