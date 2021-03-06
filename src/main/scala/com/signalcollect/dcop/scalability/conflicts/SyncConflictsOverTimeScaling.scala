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
import com.signalcollect.dcop.io.DropboxResultHandler

object SyncConflictsOverTimeScaling extends App {

    /*
   * general properties
   */
  val fileName = "graphs/scaling/synthetic/graph300.txt"
  val graphName = "graph300"
  val isAdopt = false
  val steps = 5
  val timeLimit = 10000
  val intervalList = List(250L, 300L, 350L, 400L, 450L, 500L, 625L, 750L, 875L, 1000L, 1500L, 2500L, 5000L, 7500L, 10000L)
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
  val syncMSbenchmarkConfig = new BenchmarkConfiguration(syncMSexecutionConfig, fileName, isAdopt, steps, new MaxSumConflictAggregationOperation, syncBenchmarkMode)
  //------------------------------------------------

  val executable = new DistributedBenchmarkExecutable("SyncMaxSum",
		  											  syncMSexecutionConfig,
		  											  syncMSbenchmarkConfig,
		  											  factorGraphProvider,
		  											  algorithmType,
		  											  intervalList)
  
  handleResult(executable.run.asInstanceOf[List[Tuple2[Long,Int]]])
  
  
  private def handleResult(result : List[Tuple2[Long,Int]]) = {
        val dbx = new DropboxResultHandler("graph300SyncConflicts", "results/scaling/syncconflicts",syncBenchmarkMode)
        dbx.handleResult(result)
  }
}