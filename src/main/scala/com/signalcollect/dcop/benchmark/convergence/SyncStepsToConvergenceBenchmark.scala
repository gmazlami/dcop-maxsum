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
import com.signalcollect.dcop.io.DropboxResultHandler

/**
 * Scala Application that executes a benchmark on the steps until convergence for synchronous Max-Sum, DSA-A,DSA-B, BestResponse.
 * The Results are stored to DropBox.
 * 
 * IMPORTANT: To run this benchmark correctly, activate Convergence Detection in com.signalcollect.dcop.util.ProblemConstants !!!
 */

object SyncStepsToConvergenceBenchmark extends App {

    /*
   * general properties
   */
  val fileName = "graphs/ADOPT/adopt40.txt"
  val graphName = "adopt40"
  val isAdopt = true
  val graphSize = 40
  val steps = 50
  val benchmarkMode = BenchmarkModes.SyncStepsToConvergence
  //------------------------------------------------
  
  
  /*
   * properties for MaxSum
   */
  val maxSumName = "MaxSum"
  val MSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0)
  val MSbenchmarkConfig = new BenchmarkConfiguration(MSexecutionConfig,fileName,isAdopt,steps,null,benchmarkMode)
  val maxSumAlgorithm = new MaxSumAlgorithm(MSbenchmarkConfig)
  
  /*
   * properties for DSA-A and DSA-B
   */
  val dsaAname = "DSAA"
  val dsaBname = "DSAB"
  val DSAexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0)
  val DSAbenchmarkConfig = new BenchmarkConfiguration(DSAexecutionConfig,fileName,isAdopt,steps,null,benchmarkMode)
  val dsaAalgorithm = new DSAAlgorithm(DSAbenchmarkConfig,DSAVariant.A,0.45, graphSize)
  val dsaBalgorithm = new DSAAlgorithm(DSAbenchmarkConfig,DSAVariant.B,0.45, graphSize)
  
  /*
   * properties for Best-Response
   */
  val brName = "BestResponse"
  val BRexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0)
  val BRbenchmarkConfig = new BenchmarkConfiguration(BRexecutionConfig,fileName,isAdopt,steps,null,benchmarkMode)
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
  handleResult(maxSumName,maxSumSteps,benchmarkMode)
  println("-----------------------")
  
  /*
   * run evaluation for DSA-A
   */
  println("Evaluating DSA-A...")
  dsaAalgorithm.runEvaluation()
  dsaAsteps = dsaAalgorithm.getResult.asInstanceOf[Long]
  println("DSA-A evaluated.")
  println(dsaAsteps)
  handleResult(dsaAname,dsaAsteps,benchmarkMode)
  println("-----------------------")
  
  
  /*
   * run evaluation for DSA-B
   */
  println("Evaluating DSA-B...")
  dsaBalgorithm.runEvaluation()
  dsaBsteps = dsaBalgorithm.getResult.asInstanceOf[Long]
  println("DSA-B evaluated.")
  println(dsaBsteps)
  handleResult(dsaBname,dsaBsteps,benchmarkMode)
  println("-----------------------")
  
  
  /*
   * run evaluation for Best-Response
   */
  println("Evaluating Best-Response...")
  brAlgorithm.runEvaluation()
  bestResponseSteps = brAlgorithm.getResult.asInstanceOf[Long]
  println("Best-Response evaluated.")
  println(bestResponseSteps)
  handleResult(brName,bestResponseSteps,benchmarkMode)
  println("-----------------------")
  
  System.exit(0)
  
    /**
   * stores results to a file on dropbox with corresponding timestamp, filename and folder
   */
  def handleResult(string : String,results : Any, mode : BenchmarkModes.Value) = {
    val dbx = new DropboxResultHandler(string, "benchmark/syncconflictsoversteps",mode)
    dbx.handleResult(results)
  }
}