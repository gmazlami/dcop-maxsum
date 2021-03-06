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
import com.signalcollect.dcop.io.DropboxResultHandler

object AsyncConflictsOverTimeScaling extends App {

  
    /*
   * general properties
   */
  val fileName = "graphs/scaling/synthetic/623.txt"
  val graphName = "623"
  val isAdopt = false
  val steps = 5
  val timeLimit = 1500
  val intervalList = List(250L, 300L, 350L, 400L, 450L, 500L, 625L, 750L, 875L, 1000L, 1500L)
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
  val asyncMSbenchmarkConfig = new BenchmarkConfiguration(asyncMSexecutionConfig, fileName, isAdopt, steps, new MaxSumConflictAggregationOperation, asyncBenchmarkMode)
  //------------------------------------------------

  val executable = new DistributedBenchmarkExecutable("SyncMaxSum",
		  											  asyncMSexecutionConfig,
		  											  asyncMSbenchmarkConfig,
		  											  factorGraphProvider,
		  											  algorithmType,
		  											  intervalList)
  
  handleResult(executable.run.asInstanceOf[List[Tuple2[Long,Int]]])
  
  
  private def handleResult(result : List[Tuple2[Long,Int]]) = {
        val dbx = new DropboxResultHandler("graph623AsyncConflicts", "results/scaling/asyncconflicts",asyncBenchmarkMode)
        dbx.handleResult(result)
  }
}