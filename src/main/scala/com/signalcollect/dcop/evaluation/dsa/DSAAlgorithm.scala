package com.signalcollect.dcop.evaluation.dsa

import com.signalcollect.dcop.benchmark.BenchmarkModes
import com.signalcollect.dcop.benchmark.BenchmarkConfiguration
import com.signalcollect.dcop.evaluation.candidates.DSAVariant
import com.signalcollect.configuration.ExecutionMode

class DSAAlgorithm(config: BenchmarkConfiguration, dsaVariant: DSAVariant.Value, pSchedule: Double, graphSize: Int) {

  private val configuration = config

  /*
   * measurements
   */
  private var conflictsOverSteps: List[Tuple2[Int, Int]] = List()
  private var resultingConflicts: Int = 0
  private var stepsToConvergence: Long = 0
  private var timeToConvergence: Long = 0

  //an executable instance of the algorithm
  val algorithm: DSAExecutor = new DSAExecutor(configuration.file, configuration.executionConfiguration, configuration.isAdopt, configuration.aggregationOperation, dsaVariant, pSchedule, graphSize)

  def runEvaluation() = {
    configuration.mode match {
      case BenchmarkModes.SyncConflictsOverSteps => evaluateSyncConflictsOverSteps()
      case BenchmarkModes.SyncStepsToConvergence => evaluateSyncStepsToConvergence()
      case BenchmarkModes.SyncTimeToConvergence => evaluateSyncTimeToConvergence()
      case _ => println("Unknown BenchmarkMode. Exiting.."); System.exit(-1)
    }
  }

  def getResult() = {
    configuration.mode match {
      case BenchmarkModes.SyncConflictsOverSteps => conflictsOverSteps
      case BenchmarkModes.SyncStepsToConvergence => stepsToConvergence
      case BenchmarkModes.SyncTimeToConvergence => timeToConvergence
      case _ => println("Unknown BenchmarkMode. Exiting.."); System.exit(-1)
    }
  }

  private def evaluateSyncConflictsOverSteps() = {
    if (configuration.executionConfiguration.executionMode != ExecutionMode.Synchronous) {
      println("ERROR: Can't evaluate ConflictsOverTime on Asynchronous BenchmarkConfiguration.")
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
      val partialResult = algorithm.executeForConvergenceSteps()
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