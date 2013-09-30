package com.signalcollect.dcop.benchmark.convergence

import com.signalcollect.dcop.io.ResultWriter
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.dcop.evaluation.maxsum.MaxSumAlgorithm
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.evaluation.dsa.DSAAlgorithm
import com.signalcollect.dcop.evaluation.bestresponse.BRAlgorithm
import com.signalcollect.dcop.evaluation.candidates.DSAVariant
import com.signalcollect.dcop.benchmark.BenchmarkConfiguration
import com.signalcollect.dcop.benchmark.BenchmarkModes

object SyncStepsToConvergenceBenchmark extends App {

    /*
   * general properties
   */
  val fileName = "graphs/ADOPT/adopt10.txt"
  val graphName = "adopt10"
  val isAdopt = true
  val graphSize = 40
  val steps = 50
  val numColors = 3
  val benchmarkMode = BenchmarkModes.SyncStepsToConvergence
  //------------------------------------------------
  
  
  /*
   * properties for MaxSum
   */
  val maxSumName = "MaxSum"
  val MSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0)
  val MSbenchmarkConfig = new BenchmarkConfiguration(MSexecutionConfig,fileName,isAdopt,steps,null,numColors,benchmarkMode)
  val maxSumAlgorithm = new MaxSumAlgorithm(MSbenchmarkConfig)
  
  /*
   * properties for DSA-A and DSA-B
   */
  val dsaAname = "DSAA"
  val dsaBname = "DSAB"
  val DSAexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0)
  val DSAbenchmarkConfig = new BenchmarkConfiguration(DSAexecutionConfig,fileName,isAdopt,steps,null,numColors,benchmarkMode)
  val dsaAalgorithm = new DSAAlgorithm(DSAbenchmarkConfig,DSAVariant.A,0.45, graphSize)
  val dsaBalgorithm = new DSAAlgorithm(DSAbenchmarkConfig,DSAVariant.B,0.45, graphSize)
  
  /*
   * properties for Best-Response
   */
  val brName = "BestResponse"
  val BRexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0)
  val BRbenchmarkConfig = new BenchmarkConfiguration(BRexecutionConfig,fileName,isAdopt,steps,null,numColors,benchmarkMode)
  val brAlgorithm = new BRAlgorithm(BRbenchmarkConfig,true, 0.6, graphSize)
  
  
  /*
   * result Containers
   */
  var maxSumSteps : Long = 0
  var dsaAsteps : Long = 0
  var dsaBsteps : Long = 0
  var bestResponseSteps : Long = 0
  
  /*
   * run evaluation for MaxSum:
   */
  println("Evaluating Max-Sum...")
  maxSumAlgorithm.runEvaluation()
  maxSumSteps  = maxSumAlgorithm.getResult().asInstanceOf[Long]
  println("Max-Sum evaluted.")
  println(maxSumSteps)
//  storeResultsToFile(maxSumConflicts,maxSumName)
  println("-----------------------")
  
  /*
   * run evaluation for DSA-A
   */
  println("Evaluating DSA-A...")
  dsaAalgorithm.runEvaluation()
  dsaAsteps = dsaAalgorithm.getResult.asInstanceOf[Long]
  println("DSA-A evaluated.")
  println(dsaAsteps)
//  storeResultsToFile(dsaAconflicts,dsaAname)
  println("-----------------------")
  
  
  /*
   * run evaluation for DSA-B
   */
  println("Evaluating DSA-B...")
  dsaBalgorithm.runEvaluation()
  dsaBsteps = dsaBalgorithm.getResult.asInstanceOf[Long]
  println("DSA-B evaluated.")
  println(dsaBsteps)
//  storeResultsToFile(dsaBconflicts, dsaBname)
  println("-----------------------")
  
  
  /*
   * run evaluation for Best-Response
   */
  println("Evaluating Best-Response...")
  brAlgorithm.runEvaluation()
  bestResponseSteps = brAlgorithm.getResult.asInstanceOf[Long]
  println("Best-Response evaluated.")
  println(bestResponseSteps)
//  storeResultsToFile(bestResponseConflicts, brName)
  println("-----------------------")
  
  System.exit(0)
  
  
//  def storeResultsToFile(results : Any, algorithm : String) = {
//    val resultWriter = new ResultWriter(benchmarkMode, graphName ,algorithm , results)
//    resultWriter.write()
//  }
  
}