package com.signalcollect.dcop.scalability.conflicts

import com.signalcollect.dcop.benchmark.BenchmarkModes
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.dcop.benchmark.BenchmarkConfiguration
import com.signalcollect.dcop.evaluation.maxsum.MaxSumAlgorithm
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.evaluation.maxsum.MaxSumConflictAggregationOperation
import com.signalcollect.dcop.scalability.DistributedBenchmarkExecutable
import com.signalcollect.dcop.graphs.FactorGraphProvider
import com.signalcollect.dcop.io.FileGraphReader
import com.signalcollect.dcop.scalability.AlgorithmType

object SyncConflictsOverTimeScaling extends App {

    /*
   * general properties
   */
  val fileName = "graphs/example.txt"
  val graphName = "adopt10"
  val isAdopt = false
  val steps = 5
  val timeLimit = 10000
  val numColors = 3
  val intervalList = List(500L, 1000L, 1500L, 2500L, 5000L, 7500L, 10000L)
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
  val syncBenchmarkMode = BenchmarkModes.SyncConflictsOverTime
  val syncMSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0).withTimeLimit(timeLimit)
  val syncMSbenchmarkConfig = new BenchmarkConfiguration(syncMSexecutionConfig, fileName, isAdopt, steps, null, numColors, syncBenchmarkMode)
  //------------------------------------------------

  val executable = new DistributedBenchmarkExecutable("SyncMaxSum",
		  											  syncMSexecutionConfig,
		  											  syncMSbenchmarkConfig,
		  											  factorGraphProvider,
		  											  algorithmType,
		  											  intervalList)
  
  handleResult(executable.run.asInstanceOf[List[Tuple2[Long,Int]]])
  
  
  private def handleResult(result : List[Tuple2[Long,Int]]) = {
    println(result)
  }
}