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

package com.signalcollect.dcop.benchmark

import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.interfaces.AggregationOperation
import com.signalcollect.ExecutionConfiguration


/**
 * container class used to hold different configuration parameters passed to the benchmark exeuctions
 * 
 * @param exConfig: S/C ExecutionConfiguration for the benchmark
 * @param fileName: path to the file where the input graph is stored
 * @param adopt: a boolean stating wether the graph in 'fileName' has ADOPT format (true) or not(false)
 * @param steps: Number of steps to execute (will only be used for synchronous executions
 * @param aggregator: an instance of the aggregationOperation used to measure the statistics of the benchmark
 * @param numColors: the number of colors in the graph coloring problem
 * @param benchmarkMode : enumeration value stating which of the 6 benchmark modes is used
 */
class BenchmarkConfiguration(exConfig : ExecutionConfiguration, fileName : String, adopt : Boolean, steps : Int, aggregator : AggregationOperation[Int], numColors : Int , benchmarkMode : BenchmarkModes.Value) {

  val executionConfiguration = exConfig
  val file = fileName
  val isAdopt = adopt
  val stepsLimit = steps
  val aggregationOperation = aggregator
  val numOfColors = numColors
  val mode = benchmarkMode 
  
}