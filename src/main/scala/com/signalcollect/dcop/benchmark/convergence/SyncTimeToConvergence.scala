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

import com.signalcollect.dcop.benchmark.BenchmarkModes
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.dcop.benchmark.BenchmarkConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.evaluation.maxsum.MaxSumAlgorithm
import com.signalcollect.dcop.io.DropboxResultHandler

/**
 * Scala Application that executes a benchmark on the time until convergence for synchronous Max-Sum.
 * The Results are stored to DropBox.
 * 
 * IMPORTANT: To run this benchmark correctly, activate Convergence Detection in com.signalcollect.dcop.util.ProblemConstants !!!
 */

object SyncTimeToConvergence extends App {
	
  /*
   * general properties
   */
  // path to the input graph
  val fileName = "graphs/ADOPT/adopt20.txt"
    
  // is the input Graph in ADOPT-format or in EDGELIST format?
  val isAdopt = true
  //------------------------------------------------

  /*
   * properties for sync MaxSum
   */
  val syncMaxSumName = "MaxSumSync"
  val syncBenchmarkMode = BenchmarkModes.SyncTimeToConvergence
  val syncMSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0)
  val syncMSbenchmarkConfig = new BenchmarkConfiguration(syncMSexecutionConfig, fileName, isAdopt, 0, null, syncBenchmarkMode)
  val syncMaxSumAlgorithm = new MaxSumAlgorithm(syncMSbenchmarkConfig)

  
  /*
   * results
   */
  var syncTimeToConvergence : Long = 0
  /*
   * run evaluation for Synchronous MaxSum:
   */
  println("Evaluating Synchronous Max-Sum...")
  syncMaxSumAlgorithm.runEvaluation()
  syncTimeToConvergence = syncMaxSumAlgorithm.getResult().asInstanceOf[Long]
  println(syncTimeToConvergence)
  handleResult(syncTimeToConvergence)
  println("Synchronous Max-Sum evaluted.")
  println("-----------------------")


  System.exit(0)
  
  /**
   * stores results to a file on dropbox with corresponding timestamp, filename and folder
   */
  def handleResult(results : Any) = {
    val dbx = new DropboxResultHandler("adopt10AsyncTimeToConvergence", "benchmark/asynctimetoconvergence",syncBenchmarkMode)
    dbx.handleResult(results)
  }
}