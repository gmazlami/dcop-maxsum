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
import com.signalcollect.dcop.vertices.id.MaxSumId
import com.signalcollect.dcop.vertices.SimpleVertex

object ConvergenceObserver {

  //this map stores the vertices as keys with a boolean as a value stating wether the messages at that vertex have converged
  var messageConvergedVertices : HashMap[MaxSumId,Boolean] = HashMap()
  
  //this map stores the vertices as keys with a boolean as a value stating wether the state at that vertex has converged
  var stateConvergedVertices : HashMap[MaxSumId,Boolean] = HashMap()
  
  //a list containing all vertices of the current problem
  var simpleVertices : List[SimpleVertex] = null
  
  /* a function that checks wether all vertices have converged (in the sense of message-convergence, as defined in
   * Rogers, Farinelli 2008, Coordination of Low-Power Embedded Devices using the Max-Sum Algorithm)
   */
  def checkGlobalMessageConvergence() : Boolean = {
    var converged = true
    var index = 0
    
    while(index < simpleVertices.size && converged == true){
      val v = simpleVertices(index)
      val vv = v.variableVertex
      val fv = v.functionVertex
      
      if(!messageConvergedVertices.contains(vv.id)){
        converged = false
      }
      
      if(!messageConvergedVertices.contains(fv.id)){
        converged = false
      }
      index += 1
    }
    converged
  }
    /* a function that checks wether all vertices have converged (in the sense of state-convergence, as defined in
   * Rogers, Farinelli 2008, Coordination of Low-Power Embedded Devices using the Max-Sum Algorithm)
   */
  def checkGlobalStateConvergence() : Boolean = {
    var converged = true
    var index = 0
    
    while(index < simpleVertices.size && converged == true){
      val v = simpleVertices(index)
      val vv = v.variableVertex

      if(!stateConvergedVertices.contains(vv.id)){
        converged = false
      }
      index += 1
    }
    converged
  }
}