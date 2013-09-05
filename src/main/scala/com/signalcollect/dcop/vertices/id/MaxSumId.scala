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
/*
 * typeFlag = 1 --> function vertex
 * typeFlag = 0 --> variable vertex 
 */
class MaxSumId(idNum : Int, typeFlag : Int) extends Serializable{
	
  var idNumber = idNum
  
  var isVariable : Boolean = if(typeFlag == 0) true else false
  
  //Strings will be used as identifiers for vertices
  var id : String  =
    
  if(typeFlag == 0){ //if flag = 0 then vertex will be variable vertex with id in form of v1234
    "v" + idNum
  }else{ //if flag = 1 then vertex will be function vertex with id in form of f1234
    "f" + idNum
  }
  
//  
//  def ==(that : MaxSumId) : Boolean = {
//    if (that.idNumber == idNumber && that.isVariable == isVariable) true
//    else false
//  }
  
  override def equals(other : Any) : Boolean = {
    other match {
      case x: MaxSumId => (x.idNumber == idNumber && x.isVariable == isVariable)
      case _ => false
    }
  }
  
  override def hashCode() = (idNumber, isVariable, id).toString.hashCode()
    
}