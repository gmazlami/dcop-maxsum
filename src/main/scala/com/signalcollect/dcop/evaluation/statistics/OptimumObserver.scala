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

class OptimumObserver(results : HashMap[MaxSumId,Int], simpleVertices : List[SimpleVertex]) {

  //holds a map <MaxSumId,Int> that has the vertex ids as keys and the respective vertice's state (color) as value
  val stateMap = results
  
  //holds a list containing all vertices of the graph
  val simpleGraph = simpleVertices
  
  //checks wether a globally optimal state was reached by counting the number of conflicts
  def optimumFound() : Boolean = {
    if(computeConflicts == 0){
      true
    }else{
      false
    }
  }
  
  //a function counting the number of total color conflicts between the vertices in the graph 
  private def computeConflicts() : Int = {

    var conflicts = 0
    simpleGraph.foreach{ simpleVertex =>
      val vertex = simpleVertex.variableVertex
      val color = stateMap(vertex.id)
      
      simpleVertex.neighborhood.foreach{ neighborNum =>
        val otherVertex = findSimpleVertex(neighborNum)
        val otherColor = stateMap(otherVertex.id)
        
        if(color == otherColor){
          conflicts += 1
        }
      }
    }
    conflicts
  }
  
  //auxiliary function
  private def findSimpleVertex(idNum : Int) = {
    simpleGraph.find(element => element.id == idNum).get.variableVertex
  }
}