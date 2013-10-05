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

import com.signalcollect.dcop.io.ResultWriter
import com.signalcollect.dcop.evaluation.maxsum.MaxSumConflictAggregationOperation
import com.signalcollect.dcop.evaluation.maxsum.MaxSumAlgorithm
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.benchmark.BenchmarkModes
import com.signalcollect.dcop.benchmark.BenchmarkConfiguration
import com.signalcollect.dcop.io.DropboxResultHandler

/**
 * Scala Application that executes a benchmark on the conflicts over time for asynchronous Max-Sum.
 * The Results are stored to DropBox.
 */

object AsyncConflictsOverTimeBenchmark extends App {

  /*
   * general properties
   */
  // path to the input graph
  val fileName = "graphs/ADOPT/adopt10.txt"
  
  // name of the input graph (Does not necessarily have to be the filename, will only be used to store the result)  
  val graphName = "adopt10"
  
  // is the input Graph in ADOPT-format or in EDGELIST format?
  val isAdopt = true
  
  // maximum time the benchmark is allowed to run
  val timeLimit = 10000
  //------------------------------------------------

  /*
   * properties for async MaxSum
   */
  // benchmarkMode for this benchmark
  val asyncBenchmarkMode = BenchmarkModes.AsyncConflictsOverTime
  
  // S/C execution configuration for this benchmark
  val asyncMSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.PureAsynchronous).withCollectThreshold(0).withSignalThreshold(0).withTimeLimit(timeLimit)
  
  // becnhmarkCofiguration holding all necesarry information for the benchmark
  val asyncMSbenchmarkConfig = new BenchmarkConfiguration(asyncMSexecutionConfig, fileName, isAdopt, 0, new MaxSumConflictAggregationOperation, asyncBenchmarkMode)
  
  // executable Algorithm instance
  val asyncMaxSumAlgorithm = new MaxSumAlgorithm(asyncMSbenchmarkConfig)

  /*
   * result containers
   */
  var asyncMaxSumConflicts: List[Tuple2[Long, Int]] = List()

  /*
   * run evaluation for Asynchronous MaxSum:
   */
  println("Evaluating Asynchronous Max-Sum...")
  asyncMaxSumAlgorithm.runEvaluation()
  asyncMaxSumConflicts = asyncMaxSumAlgorithm.getResult().asInstanceOf[List[Tuple2[Long, Int]]]
  println(asyncMaxSumConflicts)
  println("Asynchronous Max-Sum evaluted.")
  handleResult(asyncMaxSumConflicts)
  println("-----------------------")

  System.exit(0)
  
  /**
   * stores results to a file on dropbox with corresponding timestamp, filename and folder
   */
  def handleResult(results : Any) = {
    val dbx = new DropboxResultHandler("adopt10AsyncConflicts", "benchmark/asyncconflicts",asyncBenchmarkMode)
    dbx.handleResult(results)
  }
}