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

package com.signalcollect.dcop.test

import scala.collection.mutable.HashMap
import com.signalcollect.dcop.vertices.id.MaxSumId

object MapKeyTest extends App {

  val map : HashMap[MaxSumId,String] = HashMap()
  
  //fill in data
  var id1 = new MaxSumId(1,0)
  var id2 = new MaxSumId(1,0)
  map += (id1 -> "Node1")
  map += (new MaxSumId(2,0) -> "Node2")
  map += (new MaxSumId(1,1) -> "Node3")
  map += (new MaxSumId(2,1) -> "Node4")
  
  //try to get data:
  println(map(id1))
  println(map.apply(id2))
  
  
  
  
}