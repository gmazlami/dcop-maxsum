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
import scala.collection.mutable.HashMap

/**
 * Transformer class that takes a simple graph as an input, in form of a map of simpleVertices.
 * The transform method iterates through the simpleVertices, expands each simpleVertex to a variableVertex
 * and a functionVertex, connects those two, then iterates through the neighborhood of the current simpleVertex,
 * expands also each neighbor to variable and funcitonVertex, and then builds all necessary connections.
 * 
 * At the end, the transform method returns a fully transformed Factor-Graph built up by FunctionVertex, VariableVertex,
 * FunctionToVariable edges and VariableToFunction edges.
 * 
 * The resulting graph is ready to be processed by signal collect.
 *
 */
class FactorGraphTransformer {
	
  
  def transform(simpleVertexMap : HashMap[Int,SimpleVertex]) ={
	val graph = GraphBuilder.build
	graph.awaitIdle
    
	println("Iterating through simpleVertexMap:")
	println
	
	if(simpleVertexMap.values.isEmpty){
	  println("ERROR: simpleVertexMap is empty")
	  System.exit(-1)
	}
	
    simpleVertexMap.values.foreach{ vertex =>
	        /*
	         * expand the simple vertex to function vertex and variable vertex
      		 * and connect them bidirectionally
      		 */  
    		graph.addVertex(vertex.functionVertex)
    		graph.addVertex(vertex.variableVertex)
    		graph.addEdge(vertex.functionVertex.id, new FunctionToVariable(vertex.variableVertex.id))
    		graph.addEdge(vertex.variableVertex.id, new VariableToFunction(vertex.functionVertex.id))
    		println("Expanded Vertices " + vertex.functionVertex.id.id + " and " + vertex.variableVertex.id.id +" and connected them.")
    		//
    		
    		println("Iterating through neighborhood of " + vertex.functionVertex.id.id + " and " + vertex.variableVertex.id.id + ":" )
    		
    		if(vertex.neighborhood.isEmpty){
    		  println("ERROR: neighborhood of vertex " + vertex + " is emtpy ")
    		}
    		
    		vertex.neighborhood.foreach{
    				neighbor =>
    				  val neighborVertex = simpleVertexMap(neighbor)
    				  
    				  //expand to function and variable vertex
    				  graph.addVertex(neighborVertex.functionVertex)
    				  graph.addVertex(neighborVertex.variableVertex)
    				  
    				  println("Current neighbor (variable and function): " +neighborVertex.variableVertex.id.id + " and " + neighborVertex.functionVertex.id.id )
    				  
    				  //connect to bidirectionally
    				  graph.addEdge(neighborVertex.functionVertex.id, new FunctionToVariable(neighborVertex.variableVertex.id))
    				  graph.addEdge(neighborVertex.variableVertex.id, new VariableToFunction(neighborVertex.functionVertex.id))
    		
    				  //connect function and variable vertex to variable and function vertex of "vertex" from outer loop
    				  graph.addEdge(vertex.functionVertex.id, new FunctionToVariable(neighborVertex.variableVertex.id))
    				  graph.addEdge(vertex.variableVertex.id, new VariableToFunction(neighborVertex.functionVertex.id))
    				  graph.addEdge(neighborVertex.functionVertex.id, new FunctionToVariable(vertex.variableVertex.id))
    				  graph.addEdge(neighborVertex.variableVertex.id, new VariableToFunction(vertex.functionVertex.id))
    		}
    		println("---------")
	}
    graph
  }
}