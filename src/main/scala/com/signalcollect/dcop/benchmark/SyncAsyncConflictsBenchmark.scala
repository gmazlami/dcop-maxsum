package com.signalcollect.dcop.benchmark

import com.signalcollect.dcop.io.ResultWriter
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.dcop.evaluation.maxsum.MaxSumAlgorithm
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.evaluation.maxsum.MaxSumConflictAggregationOperation

object SyncAsyncConflictsBenchmark extends App {

   /*
   * general properties
   */
  val fileName = "graphs/full-graph-20.txt"
  val graphName = "test"
  val isAdopt = false
  val steps = 5
  val timeLimit = 500
  val numColors = 2
  //------------------------------------------------
  
  
  /*
   * properties for sync MaxSum
   */
  val syncMaxSumName = "MaxSumSync"
  val syncBenchmarkMode = BenchmarkModes.SyncResultingConflicts
  val syncMSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0).withTimeLimit(timeLimit)
  val syncMSbenchmarkConfig = new BenchmarkConfiguration(syncMSexecutionConfig,fileName,isAdopt,steps,new MaxSumConflictAggregationOperation,numColors,syncBenchmarkMode)
  val syncMaxSumAlgorithm = new MaxSumAlgorithm(syncMSbenchmarkConfig)
  
  /*
   * properties for async MaxSum
   */
  val asyncMaxSumName = "MaxSumAsync"
  val asyncBenchmarkMode = BenchmarkModes.AsyncResultingConflicts  
  val asyncMSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.PureAsynchronous).withCollectThreshold(0).withSignalThreshold(0).withTimeLimit(timeLimit)
  val asyncMSbenchmarkConfig = new BenchmarkConfiguration(asyncMSexecutionConfig,fileName,isAdopt,steps,new MaxSumConflictAggregationOperation,numColors,asyncBenchmarkMode)
  val asyncMaxSumAlgorithm = new MaxSumAlgorithm(asyncMSbenchmarkConfig)
  
  /*
   * results
   */
  var syncMaxSumConflicts = 0
  var asyncMaxSumConflicts = 0
  
  /*
   * run evaluation for Synchronous MaxSum:
   */
  println("Evaluating Synchronous Max-Sum...")
  syncMaxSumAlgorithm.runEvaluation()
  syncMaxSumConflicts  = syncMaxSumAlgorithm.getResult().asInstanceOf[Int]
  println(syncMaxSumConflicts)
  println("Synchronous Max-Sum evaluted.")
  storeResultsToFile(syncMaxSumConflicts,syncMaxSumName, syncBenchmarkMode)
  println("-----------------------")

  
  /*
   * run evaluation for Asynchronous MaxSum:
   */
  println("Evaluating Asynchronous Max-Sum...")
  asyncMaxSumAlgorithm.runEvaluation()
  asyncMaxSumConflicts  = asyncMaxSumAlgorithm.getResult().asInstanceOf[Int]
  println(asyncMaxSumConflicts)
  println("Asynchronous Max-Sum evaluted.")
  storeResultsToFile(asyncMaxSumConflicts,asyncMaxSumName, asyncBenchmarkMode)
  println("-----------------------")

  
  System.exit(0)
  
  
  def storeResultsToFile(results : Any, algorithm : String, benchmarkMode : BenchmarkModes.Value) = {
    val resultWriter = new ResultWriter(benchmarkMode, graphName ,algorithm , results)
    resultWriter.write()
  }
  
 
}