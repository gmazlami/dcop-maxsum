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

package com.signalcollect.dcop.graphs

import com.signalcollect.Graph
import com.signalcollect.dcop.vertices.SimpleVertex
import com.signalcollect.GraphBuilder
import com.signalcollect.dcop.edges.FunctionToVariable
import com.signalcollect.dcop.edges.VariableToFunction
import com.signalcollect.dcop.vertices.id.MaxSumId
import scala.collection.immutable.HashMap

class FactorGraphTransformer {
	
  
  def transform(simpleVertexMap : HashMap[Int,SimpleVertex], utilityFunction : (Set[Double] => Double), numColors : Int) ={
	val graph = GraphBuilder.build
    val utility = utilityFunction
    
    simpleVertexMap.values.foreach{ vertex =>
	  //expand the simple vertex to function vertex and variable vertex
      		//and connect them bidirectionally 
    		graph.addVertex(vertex.functionVertex)
    		graph.addVertex(vertex.variableVertex)
    		graph.addEdge(vertex.functionVertex.id, new FunctionToVariable(utility, vertex.variableVertex.id))
    		graph.addEdge(vertex.variableVertex.id, new VariableToFunction(numColors, vertex.functionVertex.id))
    		
    		//
    		vertex.neighborhood.foreach{
    				neighbor =>
    				  val neighborVertex = simpleVertexMap(neighbor)
    				  
    				  //expand to function and variable vertex
    				  graph.addVertex(neighborVertex.functionVertex)
    				  graph.addVertex(neighborVertex.variableVertex)
    				  
    				  //connect to bidirectionally
    				  graph.addEdge(neighborVertex.functionVertex.id, new FunctionToVariable(utility, neighborVertex.variableVertex.id))
    				  graph.addEdge(neighborVertex.variableVertex.id, new VariableToFunction(numColors, neighborVertex.functionVertex.id))
    		
    				  //connect function and variable vertex to variable and function vertex of "vertex" from outer loop
    				  graph.addEdge(vertex.functionVertex.id, new FunctionToVariable(utility, neighborVertex.variableVertex.id))
    				  graph.addEdge(vertex.variableVertex.id, new VariableToFunction(numColors, neighborVertex.functionVertex.id))
    				  graph.addEdge(neighborVertex.functionVertex.id, new FunctionToVariable(utility, vertex.variableVertex.id))
    				  graph.addEdge(neighborVertex.variableVertex.id, new VariableToFunction(numColors, vertex.functionVertex.id))
    		}
	}
    graph
  }
}