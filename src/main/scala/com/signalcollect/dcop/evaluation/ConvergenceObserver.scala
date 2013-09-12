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

import scala.collection.mutable.HashMap
import com.signalcollect.dcop.vertices.id.MaxSumId

object ConvergenceObserver {

  //this map stores the vertices as keys with a boolean as a value stating wether the messages at that vertex have converged
  var convergedVertices : HashMap[MaxSumId,Boolean] = HashMap()
}