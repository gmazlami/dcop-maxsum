package com.signalcollect.dcop.termination

import com.signalcollect.GlobalTerminationCondition

class OptimalSolutionTerminationCondition(interval : Long) extends GlobalTerminationCondition[Int](
     aggregationOperation = new ConflictAggregationOperation,
     aggregationInterval = interval,
     shouldTerminate = (x : Int) => if(x < 1) true else false){

}