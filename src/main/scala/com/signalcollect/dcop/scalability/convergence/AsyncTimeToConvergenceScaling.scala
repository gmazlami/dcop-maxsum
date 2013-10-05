package com.signalcollect.dcop.scalability.convergence

import com.signalcollect.dcop.scalability.AlgorithmType
import com.signalcollect.dcop.graphs.FactorGraphProvider
import com.signalcollect.dcop.benchmark.BenchmarkModes
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.dcop.benchmark.BenchmarkConfiguration
import com.signalcollect.dcop.scalability.DistributedBenchmarkExecutable
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.evaluation.maxsum.MaxSumConflictAggregationOperation
import com.signalcollect.dcop.io.FileGraphReader

object AsyncTimeToConvergenceScaling extends App {
	
  
    /*
   * general properties
   */
  val fileName = "graphs/scaling/carnegieMellonRepository/cgmTest.txt"
  val graphName = "test"
  val isAdopt = false
  val steps = 5
  val timeLimit = 10000
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
  val asyncBenchmarkMode = BenchmarkModes.AsyncTimeToConvergence
  val asyncMSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.PureAsynchronous).withCollectThreshold(0).withSignalThreshold(0)
  val asyncMSbenchmarkConfig = new BenchmarkConfiguration(asyncMSexecutionConfig, fileName, isAdopt, steps, new MaxSumConflictAggregationOperation,asyncBenchmarkMode)
  //------------------------------------------------

  val executable = new DistributedBenchmarkExecutable("SyncMaxSum",
		  											  asyncMSexecutionConfig,
		  											  asyncMSbenchmarkConfig,
		  											  factorGraphProvider,
		  											  algorithmType,
		  											  null)
  
  handleResult(executable.run.asInstanceOf[Long])
  
  
  private def handleResult(result : Long) = {
    println(result)
  }
}