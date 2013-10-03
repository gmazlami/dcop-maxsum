/*
 *  @author Genc Mazlami
 *
 *  Copyright 2013 University of Zurich
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.signalcollect.dcop.benchmark.conflicts

import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.evaluation.maxsum.MaxSumConflictAggregationOperation
import com.signalcollect.dcop.evaluation.maxsum.MaxSumAlgorithm
import com.signalcollect.dcop.io.ResultWriter
import com.signalcollect.dcop.benchmark.BenchmarkConfiguration
import com.signalcollect.dcop.benchmark.BenchmarkModes
import com.signalcollect.dcop.io.DropboxResultHandler
import com.signalcollect.dcop.evaluation.dsa.DSAAlgorithm
import com.signalcollect.dcop.evaluation.bestresponse.BRAlgorithm
import com.signalcollect.dcop.evaluation.dsa.DSAConflictAggregationOperation
import com.signalcollect.dcop.evaluation.bestresponse.BRConflictAggregationOperation
import com.signalcollect.dcop.evaluation.candidates.DSAVariant

/**
 * Scala Application that executes a benchmark on the conflicts over steps for synchronous Max-Sum, DSA-A, DSA-B, BestResponse.
 * The Results are stored to DropBox.
 */

object SynchronousConflictsOverStepsBenchmark extends App {

  /*
   * general properties
   */
  val fileName = "graphs/ADOPT/adopt40.txt"
  val graphName = "adopt40"
  val isAdopt = true
  val graphSize = 40
  val steps = 50
  val numColors = 3
  val benchmarkMode = BenchmarkModes.SyncConflictsOverSteps
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
  val dsaAalgorithm = new DSAAlgorithm(DSAbenchmarkConfig,DSAVariant.A,0.45, graphSize)
  val dsaBalgorithm = new DSAAlgorithm(DSAbenchmarkConfig,DSAVariant.B,0.45, graphSize)
  
  /*
   * properties for Best-Response
   */
  val brName = "BestResponse"
  val BRexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0).withStepsLimit(1)
  val BRbenchmarkConfig = new BenchmarkConfiguration(BRexecutionConfig,fileName,isAdopt,steps,new BRConflictAggregationOperation,numColors,benchmarkMode)
  val brAlgorithm = new BRAlgorithm(BRbenchmarkConfig,true, 0.6, graphSize)
  
  
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
  handleResult(maxSumName,maxSumConflicts,benchmarkMode)
  println("-----------------------")
  
  /*
   * run evaluation for DSA-A
   */
  println("Evaluating DSA-A...")
  dsaAalgorithm.runEvaluation()
  dsaAconflicts = dsaAalgorithm.getResult.asInstanceOf[List[Tuple2[Int,Int]]]
  println("DSA-A evaluated.")
  printConflictList(dsaAconflicts)
  handleResult(dsaAname,dsaAconflicts,benchmarkMode)
  println("-----------------------")
  
  /*
   * run evaluation for DSA-B
   */
  println("Evaluating DSA-B...")
  dsaBalgorithm.runEvaluation()
  dsaBconflicts = dsaBalgorithm.getResult.asInstanceOf[List[Tuple2[Int,Int]]]
  println("DSA-B evaluated.")
  printConflictList(dsaBconflicts)
  handleResult(dsaBname,dsaBconflicts,benchmarkMode)
  println("-----------------------")
  
  
  /*
   * run evaluation for Best-Response
   */
  println("Evaluating Best-Response...")
  brAlgorithm.runEvaluation()
  bestResponseConflicts = brAlgorithm.getResult.asInstanceOf[List[Tuple2[Int,Int]]]
  println("Best-Response evaluated.")
  printConflictList(bestResponseConflicts)
  handleResult(brName,bestResponseConflicts,benchmarkMode)
  println("-----------------------")
  
  System.exit(0)
  
  /**
   * prints the passed list on the standard output
   */
  def printConflictList(list : List[Tuple2[Int,Int]]) = {
    list.foreach{ el =>
      println(el._1 + " - " + el._2)
    }
  }
  
  /**
   * stores results to a file on dropbox with corresponding timestamp, filename and folder
   */
  def handleResult(string : String,results : Any, mode : BenchmarkModes.Value) = {
    val dbx = new DropboxResultHandler(string, "benchmark/syncconflictsoversteps",mode)
    dbx.handleResult(results)
  }
  
}