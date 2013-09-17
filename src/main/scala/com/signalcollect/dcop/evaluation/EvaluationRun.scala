package com.signalcollect.dcop.evaluation

import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.termination.OptimalSolutionTerminationCondition

object EvaluationRun extends App {

  val asyncExConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.PureAsynchronous).withGlobalTerminationCondition(new OptimalSolutionTerminationCondition(100l)).withTimeLimit(5000)
  val syncExConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withGlobalTerminationCondition(new OptimalSolutionTerminationCondition(1)).withCollectThreshold(0).withSignalThreshold(0).withStepsLimit(5)
  
  val exConfig = asyncExConfig
  val maxsum = new MaxSum("graphs/full-graph-4.txt", exConfig,3)
  
  maxsum.execute()
  maxsum.printVertexStates()
  maxsum.checkOptimalSolution()
  maxsum.checkConvergence()
  
  println("Steps: " + maxsum.statistics.executionStatistics.signalSteps)
}