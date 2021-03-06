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

package com.signalcollect.dcop.termination

import com.signalcollect.GlobalTerminationCondition
import com.signalcollect.dcop.evaluation.maxsum.MaxSumConflictAggregationOperation

class OptimalSolutionTerminationCondition(interval : Long) extends GlobalTerminationCondition[Int](
     aggregationOperation = new MaxSumConflictAggregationOperation,
     aggregationInterval = interval,
     shouldTerminate = (x : Int) => if(x < 1) true else false){

}