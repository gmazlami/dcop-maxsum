package com.signalcollect.dcop.evaluation.bestresponse

import com.signalcollect.dcop.benchmark.BenchmarkConfiguration
import com.signalcollect.dcop.benchmark.BenchmarkModes
import com.signalcollect.configuration.ExecutionMode

class BRAlgorithm(config : BenchmarkConfiguration, randomInit : Boolean, p : Double, graphSize : Int) {

    private val configuration = config
  
  /*
   * measurements
   */
  private var conflictsOverSteps : List[Tuple2[Int,Int]] = List()
  private var stepsToConvergence : Int = 0
  private var timeToConvergence : Long = 0
  
  //an executable instance of the algorithm
  val algorithm : BRExecutor = new BRExecutor(configuration.file, configuration.executionConfiguration, configuration.numOfColors, configuration.isAdopt, configuration.aggregationOperation, randomInit, p, graphSize)
  
  def runEvaluation() = {
    configuration.mode match {
      case BenchmarkModes.SyncConflictsOverSteps => evaluateSyncConflictsOverSteps()
      case BenchmarkModes.SyncStepsToConvergence =>evaluateSyncStepsToConvergence()
      case BenchmarkModes.AsyncTimeToConvergence => evaluateAsyncTimeToConvergence()
      case BenchmarkModes.SyncTimeToConvergence => evaluateSyncTimeToConvergence()
      case _ => println("Unknown BenchmarkMode. Exiting.."); System.exit(-1)
    }
  }
  
    def getResult() = {
    configuration.mode match {
      case BenchmarkModes.SyncConflictsOverSteps => conflictsOverSteps
      case BenchmarkModes.SyncStepsToConvergence => stepsToConvergence
      case BenchmarkModes.AsyncTimeToConvergence => timeToConvergence
      case BenchmarkModes.SyncTimeToConvergence => timeToConvergence
      case _ => println("Unknown BenchmarkMode. Exiting.."); System.exit(-1)
    }
  }
  
  private def evaluateSyncConflictsOverSteps() = {
    if(configuration.executionConfiguration.executionMode != ExecutionMode.Synchronous){
      println("ERROR: Can't evaluate ConflictsOverTime on Asynchronous BenchmarkConfiguration.")
      println("Exiting...")
      System.exit(-1)
    }else{
    	for(run <- 1 to configuration.stepsLimit){
    		val partialResult = algorithm.executeWithAggregation()
    		if(partialResult == -1){
    		  println("ERROR: executeWithAggregation failed, AggregationOperation was null")
    		  System.exit(-1)
    		}else{
    		  conflictsOverSteps :+= (run,partialResult)
    		}
    	}
    }
  }

  private def evaluateSyncStepsToConvergence() = {
    if(configuration.executionConfiguration.executionMode != ExecutionMode.Synchronous){
      println("ERROR: Can't evaluate StepsToConvergence on Asynchronous BenchmarkConfiguration.")
      println("Exiting...")
      System.exit(-1)
    }else{
      //TODO: analyze sync
    }
  }
  
  private def evaluateSyncTimeToConvergence() = {
    if(configuration.executionConfiguration.executionMode != ExecutionMode.Synchronous){
      println("ERROR: Can't evaluate SyncTimeToConvergence on Asynchronous BenchmarkConfiguration.")
      println("Exiting...")
      System.exit(-1) 
    }else{
      //TODO: analyze sync
    }
  }
  
  private def evaluateAsyncTimeToConvergence() = {
    if(configuration.executionConfiguration.executionMode == ExecutionMode.Synchronous){
      println("ERROR: Can't evaluate AsyncTimeToConvergence on Synchronous BenchmarkConfiguration.")
      println("Exiting...")
      System.exit(-1)  
    }else{
      //TODO: analyze async
    }
  }


}