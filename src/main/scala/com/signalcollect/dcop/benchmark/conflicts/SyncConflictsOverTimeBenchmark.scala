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
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.dcop.evaluation.maxsum.MaxSumAlgorithm
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.evaluation.maxsum.MaxSumConflictAggregationOperation
import com.signalcollect.dcop.benchmark.BenchmarkModes
import com.signalcollect.dcop.benchmark.BenchmarkConfiguration
import com.signalcollect.dcop.io.DropboxResultHandler

/**
 * Scala Application that executes a benchmark on the conflicts over time for synchronous Max-Sum.
 * The Results are stored to DropBox.
 */

object SyncConflictsOverTimeBenchmark extends App {

  /*
   * general properties
   */
  val fileName = "graphs/ADOPT/adopt40.txt"
  val graphName = "adopt40"
  val isAdopt = true
  val steps = 5
  val timeLimit = 10000
  val numColors = 3
  //------------------------------------------------

  /*
   * properties for sync MaxSum
   */
  val syncMaxSumName = "MaxSumSync"
  val syncBenchmarkMode = BenchmarkModes.SyncConflictsOverTime
  val syncMSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0).withTimeLimit(timeLimit)
  val syncMSbenchmarkConfig = new BenchmarkConfiguration(syncMSexecutionConfig, fileName, isAdopt, steps, new MaxSumConflictAggregationOperation, numColors, syncBenchmarkMode)
  val syncMaxSumAlgorithm = new MaxSumAlgorithm(syncMSbenchmarkConfig)

  
  /*
   * result containers
   */
  var syncMaxSumConflicts : List[Tuple2[Long, Int]] = List()

  /*
   * run evaluation for Synchronous MaxSum:
   */
  println("Evaluating Synchronous Max-Sum...")
  syncMaxSumAlgorithm.runEvaluation()
  syncMaxSumConflicts = syncMaxSumAlgorithm.getResult().asInstanceOf[List[Tuple2[Long, Int]]]
  println(syncMaxSumConflicts)
  println("Synchronous Max-Sum evaluted.")
  handleResult(syncMaxSumConflicts)
  println("-----------------------")


  System.exit(0)
  
  def handleResult(results : Any) = {
    val dbx = new DropboxResultHandler("adopt10SyncConflicts", "benchmark/syncconflicts",syncBenchmarkMode)
    dbx.handleResult(results)
  }
}