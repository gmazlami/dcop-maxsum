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

package com.signalcollect.dcop.vertices.id
/**
 * class that represents the unique id's used throughout the Max-Sum algorithm.
 * The MaxSumId's can identify two types of vertices: VariableVertex and FunctionVertex
 * @param typeFlag = 1 --> function vertex
 * @param typeFlag = 0 --> variable vertex
 * @param idNum: the number to append to the typeString ('v + idNum' for variables and 'f + idNum' for functions) 
 */
class MaxSumId(idNum: Int, typeFlag: Int) extends Serializable {

  var idNumber = idNum

  var isVariable: Boolean = if (typeFlag == 0) true else false

  //Strings will be used as identifiers for vertices
  var id: String =
    if (typeFlag == 0) { //if flag = 0 then vertex will be variable vertex with id in form of v1234
      "v" + idNum
    } else { //if flag = 1 then vertex will be function vertex with id in form of f1234
      "f" + idNum
    }

  /**
   * custom equals function so that MaxSumId's can be compared by '==' correctly
   */
  override def equals(other: Any): Boolean = {
    other match {
      case x: MaxSumId => (x.idNumber == idNumber && x.isVariable == isVariable)
      case _ => false
    }
  }

  /**
   * custom hashCode function so that MaxSumId's can be used as keys to a map datastructure
   */
  override def hashCode() = (idNumber, isVariable, id).toString.hashCode()

}