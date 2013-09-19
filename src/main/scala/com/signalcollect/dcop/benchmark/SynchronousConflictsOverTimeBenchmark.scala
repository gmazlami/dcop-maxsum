package com.signalcollect.dcop.benchmark

import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.evaluation.maxsum.MaxSumConflictAggregationOperation
import com.signalcollect.dcop.evaluation.maxsum.MaxSumAlgorithm
import java.io.PrintWriter
import java.io.File
import com.signalcollect.dcop.io.ResultWriter
import com.signalcollect.dcop.evaluation.dsa.DSAConflictAggregationOperation
import com.signalcollect.dcop.evaluation.dsa.DSAAlgorithm
import com.signalcollect.dcop.evaluation.candidates.DSAVariant
import com.signalcollect.dcop.evaluation.bestresponse.BRConflictAggregationOperation
import com.signalcollect.dcop.evaluation.bestresponse.BRAlgorithm

object SynchronousConflictsOverTimeBenchmark extends App {

  /*
   * general properties
   */
  val fileName = "graphs/test.txt"
  val graphName = "test"
  val isAdopt = true
  val steps = 5
  val numColors = 2
  val benchmarkMode = BenchmarkModes.SyncConflictsOverTime
  //------------------------------------------------
  
  
  /*
   * properties for MaxSum
   */
  val maxSumName = "MaxSum"
  val MSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0).withStepsLimit(1)
  val MSbenchmarkConfig = new BenchmarkConfiguration(MSexecutionConfig,fileName,isAdopt,steps,new MaxSumConflictAggregationOperation,numColors,benchmarkMode)
  val maxSumAlgorithm = new MaxSumAlgorithm(MSbenchmarkConfig)
  
  /*
   * properties for DSA-A and DSA-B
   */
  val dsaAname = "DSAA"
  val dsaBname = "DSAB"
  val DSAexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0).withStepsLimit(1)
  val DSAbenchmarkConfig = new BenchmarkConfiguration(DSAexecutionConfig,fileName,isAdopt,steps,new DSAConflictAggregationOperation,numColors,benchmarkMode)
  val dsaAalgorithm = new DSAAlgorithm(DSAbenchmarkConfig,DSAVariant.A,0.45)
  val dsaBalgorithm = new DSAAlgorithm(DSAbenchmarkConfig,DSAVariant.B,0.45)
  
  /*
   * properties for Best-Response
   */
  val brName = "BestResponse"
  val BRexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0).withStepsLimit(1)
  val BRbenchmarkConfig = new BenchmarkConfiguration(BRexecutionConfig,fileName,isAdopt,steps,new BRConflictAggregationOperation,numColors,benchmarkMode)
  val brAlgorithm = new BRAlgorithm(BRbenchmarkConfig,true, 0.6)
  
  
  /*
   * result Containers
   */
  var maxSumConflicts : List[Tuple2[Int,Int]] = null
  var dsaAconflicts : List[Tuple2[Int,Int]] = null
  var dsaBconflicts : List[Tuple2[Int,Int]] = null
  var bestResponseConflicts : List[Tuple2[Int,Int]] = null
  
  /*
   * run evaluation for MaxSum:
   */
  println("Evaluating Max-Sum...")
  maxSumAlgorithm.runEvaluation()
  maxSumConflicts  = maxSumAlgorithm.getResult().asInstanceOf[List[Tuple2[Int,Int]]]
  println("Max-Sum evaluted.")
  printConflictList(maxSumConflicts)
  storeResultsToFile(maxSumConflicts,maxSumName)
  println("-----------------------")
  
  /*
   * run evaluation for DSA-A
   */
  println("Evaluating DSA-A...")
  dsaAalgorithm.runEvaluation()
  dsaAconflicts = dsaAalgorithm.getResult.asInstanceOf[List[Tuple2[Int,Int]]]
  println("DSA-A evaluated.")
  printConflictList(dsaAconflicts)
  storeResultsToFile(dsaAconflicts,dsaAname)
  println("-----------------------")
  
  /*
   * run evaluation for DSA-B
   */
  println("Evaluating DSA-B...")
  dsaBalgorithm.runEvaluation()
  dsaBconflicts = dsaBalgorithm.getResult.asInstanceOf[List[Tuple2[Int,Int]]]
  println("DSA-B evaluated.")
  printConflictList(dsaBconflicts)
  storeResultsToFile(dsaBconflicts, dsaBname)
  println("-----------------------")
  
  
  /*
   * run evaluation for Best-Response
   */
  println("Evaluating Best-Response...")
  brAlgorithm.runEvaluation()
  bestResponseConflicts = brAlgorithm.getResult.asInstanceOf[List[Tuple2[Int,Int]]]
  println("Best-Response evaluated.")
  printConflictList(bestResponseConflicts)
  storeResultsToFile(bestResponseConflicts, brName)
  println("-----------------------")
  
  System.exit(0)
  
  
  def storeResultsToFile(results : Any, algorithm : String) = {
    val resultWriter = new ResultWriter(benchmarkMode, graphName ,algorithm , results)
    resultWriter.write()
  }
  
  def printConflictList(list : List[Tuple2[Int,Int]]) = {
    list.foreach{ el =>
      println(el._1 + " - " + el._2)
    }
  }
  
}