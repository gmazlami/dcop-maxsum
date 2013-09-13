package com.signalcollect.dcop.evaluation.statistics

import scala.collection.mutable.HashMap
import com.signalcollect.dcop.vertices.id.MaxSumId
import com.signalcollect.dcop.vertices.SimpleVertex

class OptimumObserver(results : HashMap[MaxSumId,Int], simpleVertices : List[SimpleVertex]) {

  val stateMap = results
  
  val simpleGraph = simpleVertices
  
  def optimumFound() : Boolean = {
    if(computeConflicts == 0){
      true
    }else{
      false
    }
  }
  
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
  
  private def findSimpleVertex(idNum : Int) = {
    simpleGraph.find(element => element.id == idNum).get.variableVertex
  }
}