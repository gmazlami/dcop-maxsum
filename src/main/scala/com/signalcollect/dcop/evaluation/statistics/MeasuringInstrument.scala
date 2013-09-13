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


package com.signalcollect.dcop.evaluation.statistics

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