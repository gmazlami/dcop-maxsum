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

package com.signalcollect.dcop.vertices

import scala.collection.immutable.HashSet
import com.signalcollect.dcop.vertices.id.MaxSumId

class SimpleVertex(idNum:Int, neighbors : Set[Int], initial : Array[Double]) {
	val id = idNum
	
	//initialize neighborhood as empty set
	var neighborhood = neighbors
	
	val variableVertex : VariableVertex = new VariableVertex(new MaxSumId(idNum, 0),0, initial)
	
	val functionVertex : FunctionVertex = new FunctionVertex(new MaxSumId(idNum, 1),0)
	
	def addNeighbor(id : Int) = {
	  neighborhood + id
	}
}