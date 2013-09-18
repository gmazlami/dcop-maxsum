package com.signalcollect.dcop.benchmark

import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.evaluation.maxsum.MaxSumConflictAggregationOperation
import com.signalcollect.dcop.evaluation.maxsum.MaxSumAlgorithm
import java.io.PrintWriter
import java.io.File
import com.signalcollect.dcop.io.ResultWriter

object SynchronousConflictsOverTimeBenchmark extends App {

  /*
   * general properties
   */
  val fileName = "graphs/test.txt"
  val isAdopt = true
  val steps = 20
  val numColors = 2
  val benchmarkMode = BenchmarkModes.SyncConflictsOverTime
  //------------------------------------------------
  
  
  /*
   * properties for MaxSum
   */
  val MSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0).withStepsLimit(1)
  val MSbenchmarkConfig = new BenchmarkConfiguration(MSexecutionConfig,fileName,isAdopt,steps,new MaxSumConflictAggregationOperation,numColors,benchmarkMode)
  val maxSumAlgorithm = new MaxSumAlgorithm(MSbenchmarkConfig)
  

  
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
  maxSumAlgorithm.runEvaluation()
  maxSumConflicts  = maxSumAlgorithm.getResult().asInstanceOf[List[Tuple2[Int,Int]]]
  printResults()
  storeResultToFile()
  System.exit(0)
  
  
  
  def storeResultToFile() = {
    val resultWriter = new ResultWriter(benchmarkMode,"test",maxSumConflicts)
    resultWriter.write()
  }
  
  
  def printResults() = {
    println("Conflicts per Step: Max-Sum Algorithm")
    println
    maxSumConflicts.foreach{ el=>
      println( el._1 + " | " + el._2 )
    }
  }
  
}