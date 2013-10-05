package com.signalcollect.dcop.scalability.convergence

import com.signalcollect.dcop.io.ResultWriter
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.dcop.benchmark.BenchmarkConfiguration
import com.signalcollect.dcop.scalability.DistributedBenchmarkExecutable
import com.signalcollect.dcop.scalability.AlgorithmType
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.benchmark.BenchmarkModes
import com.signalcollect.dcop.graphs.FactorGraphProvider
import com.signalcollect.dcop.graphs.DSAGraphProvider
import com.signalcollect.dcop.graphs.DSAGraphProvider
import com.signalcollect.dcop.graphs.BRGraphProvider
import com.signalcollect.dcop.evaluation.candidates.DSAVariant
import com.signalcollect.dcop.io.FileGraphReader

object SyncStepsToConvergenceScaling extends App {
	
  /*
   * general properties
   */
  val fileName = "graphs/ADOPT/adopt40.txt"
  val graphName = "adopt40"
  val isAdopt = true
  val graphSize = 40
  val steps = 50
  val numColors = 3
  val benchmarkMode = BenchmarkModes.SyncStepsToConvergence
  //------------------------------------------------

  /*
   * Properties for Graph loading
   */
  val factorGraphProvider = new FactorGraphProvider(new FileGraphReader, fileName)
  val dsaaGraphProvider = new DSAGraphProvider(graphSize,0.45, DSAVariant.A,fileName, isAdopt)
  val dsabGraphProvider = new DSAGraphProvider(graphSize,0.45, DSAVariant.B,fileName, isAdopt)
  val brGraphProvider = new BRGraphProvider(graphSize,0.6,fileName, isAdopt)
  //------------------------------------------------

  
  /*
   * properties for MaxSum
   */
  val maxSumName = "MaxSum"
  val MSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0)
  val MSbenchmarkConfig = new BenchmarkConfiguration(MSexecutionConfig, fileName, isAdopt, steps, null, benchmarkMode)
  val maxSumExecutable =  new DistributedBenchmarkExecutable("SyncMaxSum",
		  											  MSexecutionConfig,
		  											  MSbenchmarkConfig,
		  											  factorGraphProvider,
		  											  AlgorithmType.MS,
		  											  null)

    /*
     * properties for DSA-A and DSA-B
     */
    val dsaAname = "DSAA"
    val dsaBname = "DSAB"
    val DSAexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0)
    val DSAbenchmarkConfig = new BenchmarkConfiguration(DSAexecutionConfig,fileName,isAdopt,steps,null,benchmarkMode)
  	val dsaAexecutable =  new DistributedBenchmarkExecutable("DSA-A",
		  											  DSAexecutionConfig,
		  											  DSAbenchmarkConfig,
		  											  dsaaGraphProvider,
		  											  AlgorithmType.DSAA,
		  											  null,
		  											  graphSize,
		  											  0.45)
    val dsaBexecutable =  new DistributedBenchmarkExecutable("DSA-B",
		  											  DSAexecutionConfig,
		  											  DSAbenchmarkConfig,
		  											  dsabGraphProvider,
		  											  AlgorithmType.DSAB,
		  											  null,
		  											  graphSize,
		  											  0.45)
  
    
    /*
     * properties for Best-Response
     */
    val brName = "BestResponse"
    val BRexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0)
    val BRbenchmarkConfig = new BenchmarkConfiguration(BRexecutionConfig,fileName,isAdopt,steps,null,benchmarkMode)
  	val brExecutable = new DistributedBenchmarkExecutable("SyncMaxSum",
		  											  BRexecutionConfig,
		  											  BRbenchmarkConfig,
		  											  brGraphProvider,
		  											  AlgorithmType.BR,
		  											  null,
		  											  graphSize,
		  											  0.6)

  /*
   * result Containers
   */
  var maxSumSteps: Long = 0
  var dsaAsteps: Long = 0
  var dsaBsteps: Long = 0
  var bestResponseSteps: Long = 0

  /*
   * run evaluation for MaxSum:
   */
  println("Evaluating Max-Sum...")
  maxSumSteps = maxSumExecutable.run.asInstanceOf[Long]
  println("Max-Sum evaluted.")
  printConflictList(maxSumSteps)
  println("-----------------------")

  /*
     * run evaluation for DSA-A
     */
  println("Evaluating DSA-A...")
  dsaAsteps = dsaAexecutable.run.asInstanceOf[Long]
  println("DSA-A evaluated.")
  printConflictList(dsaAsteps)
  println("-----------------------")

  /*
     * run evaluation for DSA-B
     */
  println("Evaluating DSA-B...")
  dsaBsteps = dsaBexecutable.run.asInstanceOf[Long]
  println("DSA-B evaluated.")
  printConflictList(dsaBsteps)
  println("-----------------------")

  /*
     * run evaluation for Best-Response
     */
  println("Evaluating Best-Response...")
  bestResponseSteps = brExecutable.run.asInstanceOf[Long]
  println("Best-Response evaluated.")
  printConflictList(bestResponseSteps)
  println("-----------------------")

  System.exit(0)

  def storeResultsToFile(results: Any, algorithm: String) = {
    val resultWriter = new ResultWriter(benchmarkMode, graphName, algorithm, results)
    resultWriter.write()
  }

  def printConflictList(result: Long) = {
    println(result)
  }
 
}