package com.signalcollect.dcop.benchmark

import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.interfaces.AggregationOperation
import com.signalcollect.ExecutionConfiguration



class BenchmarkConfiguration(exConfig : ExecutionConfiguration, fileName : String, adopt : Boolean, steps : Int, aggregator : AggregationOperation[Int], numColors : Int , benchmarkMode : BenchmarkModes.Value) {

  val executionConfiguration = exConfig
  val file = fileName
  val isAdopt = adopt
  val stepsLimit = steps
  val aggregationOperation = aggregator
  val numOfColors = numColors
  val mode = benchmarkMode 
  
}