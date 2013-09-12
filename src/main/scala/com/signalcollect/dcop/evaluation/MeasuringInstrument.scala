package com.signalcollect.dcop.evaluation

import scala.collection.mutable.HashMap

class MeasuringInstrument(name : String) {

  //the name of the algorithm being measured
  val algorithmName = name
		  
  /*the HashMap stores the iteration step (Int) as a key and the number of conflicts at that
  iteration step (Int) as avalue */
  val conflictsOverTime : HashMap[Int,Int] = HashMap()

  //incremental counter of cycles until convergence
  var cyclesToConvergence : Int = 0
}