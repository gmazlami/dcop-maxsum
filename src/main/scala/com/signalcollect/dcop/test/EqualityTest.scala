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

import com.signalcollect.dcop.vertices.id.MaxSumId
import com.signalcollect.dcop.MaxSumMessage
import scala.collection.mutable.ArrayBuffer

object EqualityTest extends App {

  val id1 : MaxSumId = new MaxSumId(1,1)
  
  val id2 : MaxSumId = new MaxSumId(1,0)
  
  val id3 : MaxSumId = new MaxSumId(1,1)
//  
//  if(id1 == id2){
//    println("WRONG! id2 should not be equal to id1")
//  }else{
//    println("CORRECT: id1 != id2")
//  }
//  
//  if(id1 == id3){
//    println("CORRECT: id1 == id3")
//  }else{
//    println("WRONG! id3 should be equal to id1")
//  }
//  
  val m1 = new MaxSumMessage(id1,id2,ArrayBuffer(0.0,0.0))
  val m2 = new MaxSumMessage(id1,id2,ArrayBuffer(0.0,0.0))
  val m3 = new MaxSumMessage(id1,id2,ArrayBuffer(0.0,0.1))
  
  if(m1 == m3) println("YES") else println("NO")
  
}