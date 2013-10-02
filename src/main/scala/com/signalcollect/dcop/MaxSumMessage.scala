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

package com.signalcollect.dcop

import com.signalcollect.dcop.vertices.id.MaxSumId
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.dcop.util.ProblemConstants
import scala.math._
import java.io.ObjectOutputStream
import java.io.IOException

class MaxSumMessage(s : MaxSumId, t: MaxSumId, v : ArrayBuffer[Double]) extends Serializable{

  val source : MaxSumId = s
  
  val target : MaxSumId = t
  
  val value : ArrayBuffer[Double] = v
 
  /**
   * checks if this MaxSumMessage instance has the equal ArrayBuffer value as "other"
   */
  def valueEquals(other : MaxSumMessage) = {
    var equal : Boolean = true
    for(i <- 0 to value.length - 1){
      if(value(i) != other.value(i)){
        equal = false
      }
    }
    equal
  }
  
  override def equals(other : Any) : Boolean = {
    other match {
      case x: MaxSumMessage => (valueEquals(x) && (x.source == source) && (x.target == target))
      case _ => false
    }
  }
  
  private def epsilonCompare(other : MaxSumMessage , epsilon : Double) = {
    var equal : Boolean = true
    for(i <- 0 to value.length - 1){
      if(abs(value(i) - other.value(i)) > epsilon){
        equal = false
      }
    }
    equal
  }
  
  def epsilon(other : Any, e : Double) : Boolean = {
    other match {
      case x: MaxSumMessage => (valueEquals(x) && (x.source == source) && (x.target == target))
      case _ => false
    }
  }
}