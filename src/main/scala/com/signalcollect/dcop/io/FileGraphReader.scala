/*
 *  @author Genc Mazlami, with contributions by Robin Hafen
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

package com.signalcollect.dcop.io

import scala.collection.mutable.LinkedHashSet
import java.io.FileNotFoundException
import com.signalcollect.dcop.vertices.SimpleVertex
import com.signalcollect.dcop.vertices.SimpleVertex
import scala.collection.immutable.HashMap
import com.signalcollect.dcop.util.ProblemConstants
import com.signalcollect.dcop.vertices.id.MaxSumId

class FileGraphReader {

  def readToList(fileName : String) : List[SimpleVertex] = {
    val tuple = fromEdgeList(fileName)
    toSimpleGraph(tuple._1, tuple._2)
  }
  
  def readToMap(fileName : String) : HashMap[Int,SimpleVertex] = {
    val map : HashMap[Int, SimpleVertex] = new HashMap()
    val list = readToList(fileName)
    list.foreach{
    		element =>
    		  map + (element.id -> element)
    }
    map
  }
  
  private def toSimpleGraph(set : LinkedHashSet[Set[Int]], vertices : Set[Int]) = {
    var simpleGraph = List[SimpleVertex]()
    vertices.foreach(v => simpleGraph = new SimpleVertex(v, findNeighbors(v, set)) :: simpleGraph )
    simpleGraph
  }
  
  
  private def findNeighbors(vertexId : Int, set : LinkedHashSet[Set[Int]]) = {
	  var neighborSet = Set[Int]()
	  
	  set.foreach(
	      s => 
	        if(s.contains(vertexId)){
	          s.foreach(
	              i => 
	                if(i != vertexId){
	                  neighborSet + i
	                }
	          )
	        }
	  )
	  neighborSet
  }
  
  //store neighboring structure of the graph to make it globally accessible, especially when needed during the computation
  def storeNeighborStructure(simpleGraph : List[SimpleVertex], vertices : HashMap[Int, SimpleVertex]) = {
    simpleGraph.foreach{current =>
    
    var neighborSetForVariable : Set[MaxSumId] = Set()
    var neighborSetForFunction : Set[MaxSumId] = Set()

    neighborSetForVariable = neighborSetForVariable + current.functionVertex.id
    neighborSetForFunction = neighborSetForFunction + current.variableVertex.id
    
    current.neighborhood.foreach{neighborId =>
      
        val simpleVertex = vertices(neighborId)
        
        neighborSetForVariable = neighborSetForVariable + simpleVertex.functionVertex.id 
        neighborSetForFunction = neighborSetForFunction + simpleVertex.variableVertex.id
        
      }
    
      ProblemConstants.neighborStructure + (current.variableVertex.id -> neighborSetForVariable)
      ProblemConstants.neighborStructure + (current.functionVertex.id -> neighborSetForFunction)
    
    }
  }
  
  
    /* 
   * Load a graph from an edge list.
   * Edge lists have the format:
   * 0 1
   * 1 2
   * 3 4
   * ...
   * where the numbers corresponed to the ids of vertices.
   * Two edges on a line mean that these two edges are connected
   * through an edge.
   * 
   * Main Author: Robin Hafen, slight modifications by Genc Mazlami
   */
  private def fromEdgeList(filename: String): (LinkedHashSet[Set[Int]],Set[Int]) = {
    val undirectedEdges = LinkedHashSet[Set[Int]]()
    val vertices = Set[Int]()
    
    try {
      val src = io.Source.fromFile(filename)
      src.getLines foreach { line =>
        val Array(iStr, jStr) = line.split(" ")
        if (iStr != jStr) {
          undirectedEdges.add(Set(iStr.toInt, jStr.toInt))
          
          if(!vertices.contains(jStr.toInt)){
            vertices + jStr.toInt
          }
          
          if(!vertices.contains(iStr.toInt)){
            vertices + iStr.toInt
          }
        }
      }
    } catch {
      case e: FileNotFoundException => throw e
    }
    (undirectedEdges,vertices)
  }
  
}