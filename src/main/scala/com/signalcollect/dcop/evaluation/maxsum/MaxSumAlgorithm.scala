package com.signalcollect.dcop.evaluation.maxsum

import com.signalcollect.dcop.benchmark.BenchmarkConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.benchmark.BenchmarkModes
import scala.collection.mutable.ListBuffer

class MaxSumAlgorithm(config: BenchmarkConfiguration) {

  private val configuration = config

  /*
   * measurements
   */
  private var conflictsOverSteps: List[Tuple2[Int, Int]] = List()
  private var conflictsOverTime: List[Tuple2[Long, Int]] = List()
  private var stepsToConvergence: Long = 0
  private var timeToConvergence: Long = 0

  //an executable instance of the algorithm
  val algorithm: MaxSumExecutor = new MaxSumExecutor(configuration.file, configuration.executionConfiguration, configuration.numOfColors, configuration.isAdopt, configuration.aggregationOperation)

  def runEvaluation() = {
    configuration.mode match {
      case BenchmarkModes.SyncConflictsOverSteps => evaluateSyncConflictsOverSteps()
      case BenchmarkModes.AsyncConflictsOverTime => evaluateConflictsOverTime()
      case BenchmarkModes.SyncConflictsOverTime => evaluateConflictsOverTime()
      case BenchmarkModes.SyncStepsToConvergence => evaluateSyncStepsToConvergence()
      case BenchmarkModes.AsyncTimeToConvergence => evaluateAsyncTimeToConvergence()
      case BenchmarkModes.SyncTimeToConvergence => evaluateSyncTimeToConvergence()
      case _ => println("Unknown BenchmarkMode. Exiting.."); System.exit(-1)
    }
  }

  def getResult() = {
    configuration.mode match {
      case BenchmarkModes.SyncConflictsOverSteps => conflictsOverSteps
      case BenchmarkModes.AsyncConflictsOverTime => conflictsOverTime
      case BenchmarkModes.SyncConflictsOverTime => conflictsOverTime
      case BenchmarkModes.SyncStepsToConvergence => stepsToConvergence
      case BenchmarkModes.AsyncTimeToConvergence => timeToConvergence
      case BenchmarkModes.SyncTimeToConvergence => timeToConvergence
      case _ => println("Unknown BenchmarkMode. Exiting.."); System.exit(-1)
    }
  }

  private def evaluateSyncConflictsOverSteps() = {
    if (configuration.executionConfiguration.executionMode != ExecutionMode.Synchronous) {
      println("ERROR: Can't evaluate ConflictsOverSteps on Asynchronous BenchmarkConfiguration.")
      println("Exiting...")
      System.exit(-1)
    } else {
      for (run <- 1 to configuration.stepsLimit) {
        val partialResult = algorithm.executeWithAggregation()
        if (partialResult == -1) {
          println("ERROR: executeWithAggregation failed, AggregationOperation was null")
          System.exit(-1)
        } else {
          conflictsOverSteps :+= (run, partialResult)
        }
      }
    }
  }

  private def evaluateConflictsOverTime() = {
      val partialResult = algorithm.executeForConflictsOverTime()
      if (partialResult.isEmpty) {
        println("ERROR: executeWithAggregation failed, AggregationOperation was null")
        System.exit(-1)
      } else {
        conflictsOverTime = partialResult
      }
  }


  private def evaluateSyncStepsToConvergence() = {
    if (configuration.executionConfiguration.executionMode != ExecutionMode.Synchronous) {
      println("ERROR: Can't evaluate StepsToConvergence on Asynchronous BenchmarkConfiguration.")
      println("Exiting...")
      System.exit(-1)
    } else {
      val partialResult = algorithm.executeForConvergenceSteps()
      if (partialResult == -1) {
        println("ERROR: SyncStepsToConvergence did not terminate because of Convergence.")
        println("Exiting...")
        System.exit(-1)
      } else {
        stepsToConvergence = partialResult
      }
    }
  }

  private def evaluateSyncTimeToConvergence() = {
    if (configuration.executionConfiguration.executionMode != ExecutionMode.Synchronous) {
      println("ERROR: Can't evaluate SyncTimeToConvergence on Asynchronous BenchmarkConfiguration.")
      println("Exiting...")
      System.exit(-1)
    } else {
      val partialResult = algorithm.executeForConvergenceTime()
      if (partialResult == -1) {
        println("ERROR: SyncTimeToConvergence did not terminate because of Convergence.")
        println("Exiting...")
        System.exit(-1)
      } else {
        timeToConvergence = partialResult
      }
    }
  }

  private def evaluateAsyncTimeToConvergence() = {
    if (configuration.executionConfiguration.executionMode == ExecutionMode.Synchronous) {
      println("ERROR: Can't evaluate AsyncTimeToConvergence on Synchronous BenchmarkConfiguration.")
      println("Exiting...")
      System.exit(-1)
    } else {
      val partialResult = algorithm.executeForConvergenceTime()
      if (partialResult == -1) {
        println("ERROR: SyncTimeToConvergence did not terminate because of Convergence.")
        println("Exiting...")
        System.exit(-1)
      } else {
        timeToConvergence = partialResult
      }
    }
  }

}