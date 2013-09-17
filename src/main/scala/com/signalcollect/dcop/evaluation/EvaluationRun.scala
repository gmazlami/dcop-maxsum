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

package com.signalcollect.dcop.evaluation

import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.termination.OptimalSolutionTerminationCondition

object EvaluationRun extends App {

  val asyncExConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.PureAsynchronous).withGlobalTerminationCondition(new OptimalSolutionTerminationCondition(10)).withTimeLimit(6000)
  val syncExConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withGlobalTerminationCondition(new OptimalSolutionTerminationCondition(1)).withCollectThreshold(0).withSignalThreshold(0).withStepsLimit(20)
  
  val exConfig = syncExConfig
  
  /*
   * Boolean stating wether the input file is a normal edge list or a Graph taken from the ADOPT repo
   */
  val isInputAdopt : Boolean = true
  
  
  val maxsum = new MaxSum("graphs/test.txt", exConfig,2, isInputAdopt)
  
  maxsum.execute()
  maxsum.printVertexStates()
}