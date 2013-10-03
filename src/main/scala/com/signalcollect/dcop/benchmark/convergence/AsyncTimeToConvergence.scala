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
import com.signalcollect.dcop.evaluation.maxsum.MaxSumAlgorithm
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.io.DropboxResultHandler

/**
 * Scala Application that executes a benchmark on the time until convergece for asynchronous Max-Sum.
 * The Results are stored to DropBox.
 */

object AsyncTimeToConvergence extends App {
	
  /*
   * general properties
   */
  val fileName = "graphs/ADOPT/adopt20.txt"
  val graphName = "adopt10"
  val isAdopt = true
  val steps = 5
  val numColors = 3
  //------------------------------------------------

  /*
   * properties for async MaxSum
   */
  val asyncMaxSumName = "MaxSumAsync"
  val asyncBenchmarkMode = BenchmarkModes.AsyncTimeToConvergence
  val asyncMSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.PureAsynchronous).withCollectThreshold(0).withSignalThreshold(0)
  val asyncMSbenchmarkConfig = new BenchmarkConfiguration(asyncMSexecutionConfig, fileName, isAdopt, steps, null, numColors, asyncBenchmarkMode)
  val asyncMaxSumAlgorithm = new MaxSumAlgorithm(asyncMSbenchmarkConfig)

  /*
   * results
   */
  var asyncTimeToConvergence: Long = 0

  /*
   * run evaluation for Asynchronous MaxSum:
   */
  println("Evaluating Asynchronous Max-Sum...")
  asyncMaxSumAlgorithm.runEvaluation()
  asyncTimeToConvergence = asyncMaxSumAlgorithm.getResult().asInstanceOf[Long]
  println(asyncTimeToConvergence)
  handleResult(asyncTimeToConvergence)
  println("Asynchronous Max-Sum evaluted.")
  println("-----------------------")

  System.exit(0)
  
    /**
   * stores results to a file on dropbox with corresponding timestamp, filename and folder
   */
  def handleResult(results : Any) = {
    val dbx = new DropboxResultHandler("adopt10AsyncTimeToConvergence", "benchmark/asynctimetoconvergence",asyncBenchmarkMode)
    dbx.handleResult(results)
  }
}