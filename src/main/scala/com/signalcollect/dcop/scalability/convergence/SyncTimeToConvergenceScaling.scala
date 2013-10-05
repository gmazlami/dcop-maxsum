package com.signalcollect.dcop.scalability.convergence

import com.signalcollect.dcop.io.FileGraphReader
import com.signalcollect.dcop.graphs.FactorGraphProvider
import com.signalcollect.dcop.scalability.AlgorithmType
import com.signalcollect.dcop.benchmark.BenchmarkModes
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.dcop.benchmark.BenchmarkConfiguration
import com.signalcollect.dcop.scalability.DistributedBenchmarkExecutable
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.evaluation.maxsum.MaxSumConflictAggregationOperation

object SyncTimeToConvergenceScaling extends App {
	
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
   * Properties for Graph loading
   */
  val reader: FileGraphReader = new FileGraphReader
  val factorGraphProvider = new FactorGraphProvider(new FileGraphReader, fileName)
  //------------------------------------------------
  
  /*
   * properties for sync MaxSum
   */
  val algorithmType = AlgorithmType.MS
  val syncMaxSumName = "MaxSumSync"
  val syncBenchmarkMode = BenchmarkModes.SyncTimeToConvergence
  val syncMSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0)
  val syncMSbenchmarkConfig = new BenchmarkConfiguration(syncMSexecutionConfig, fileName, isAdopt, steps, null, syncBenchmarkMode)
  //------------------------------------------------

  val executable = new DistributedBenchmarkExecutable("SyncMaxSum",
		  											  syncMSexecutionConfig,
		  											  syncMSbenchmarkConfig,
		  											  factorGraphProvider,
		  											  algorithmType,
		  											  null)
  
  handleResult(executable.run.asInstanceOf[Long])
  
  
  private def handleResult(result : Long) = {
    println(result)
  }
}