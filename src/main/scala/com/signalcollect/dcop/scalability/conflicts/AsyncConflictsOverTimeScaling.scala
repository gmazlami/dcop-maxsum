package com.signalcollect.dcop.scalability.conflicts

import com.signalcollect.dcop.io.FileGraphReader
import com.signalcollect.dcop.graphs.FactorGraphProvider
import com.signalcollect.dcop.scalability.AlgorithmType
import com.signalcollect.dcop.benchmark.BenchmarkModes
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.dcop.benchmark.BenchmarkConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.evaluation.maxsum.MaxSumConflictAggregationOperation
import com.signalcollect.dcop.scalability.DistributedBenchmarkExecutable

object AsyncConflictsOverTimeScaling extends App {

  
    /*
   * general properties
   */
  val fileName = "graphs/ADOPT/adopt10.txt"
  val graphName = "adopt10"
  val isAdopt = true
  val steps = 5
  val timeLimit = 10000
  val numColors = 3
  val intervalList = List(500L, 1000L, 1500L, 2500L, 5000L, 7500L, 10000L)
  //------------------------------------------------

  /*
   * Properties for Graph loading
   */
  val factorGraphProvider = new FactorGraphProvider(new FileGraphReader, fileName)
  //------------------------------------------------
  
  /*
   * properties for sync MaxSum
   */
  val algorithmType = AlgorithmType.MS
  val asyncMaxSumName = "MaxSumAsync"
  val asyncBenchmarkMode = BenchmarkModes.AsyncConflictsOverTime
  val asyncMSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.PureAsynchronous).withCollectThreshold(0).withSignalThreshold(0).withTimeLimit(timeLimit)
  val asyncMSbenchmarkConfig = new BenchmarkConfiguration(asyncMSexecutionConfig, fileName, isAdopt, steps, new MaxSumConflictAggregationOperation, numColors, asyncBenchmarkMode)
  //------------------------------------------------

  val executable = new DistributedBenchmarkExecutable("SyncMaxSum",
		  											  asyncMSexecutionConfig,
		  											  asyncMSbenchmarkConfig,
		  											  factorGraphProvider,
		  											  algorithmType,
		  											  intervalList)
  
  handleResult(executable.run.asInstanceOf[List[Tuple2[Long,Int]]])
  
  
  private def handleResult(result : List[Tuple2[Long,Int]]) = {
    println(result)
  }
}