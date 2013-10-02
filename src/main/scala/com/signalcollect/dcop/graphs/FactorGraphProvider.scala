package com.signalcollect.dcop.graphs

import com.signalcollect.GraphBuilder
import scala.collection.mutable.HashMap
import com.signalcollect.dcop.vertices.SimpleVertex
import com.signalcollect.nodeprovisioning.torque.TorqueNodeProvisioner
import com.signalcollect.dcop.edges.FunctionToVariable
import com.signalcollect.dcop.edges.VariableToFunction
import com.signalcollect.dcop.util.ProblemConstants
import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import com.signalcollect.dcop.io.FileGraphReader

class FactorGraphProvider(val reader : FileGraphReader, val fileName : String) extends GraphProvider{

  	val simpleVertexMap = reader.readToMap(fileName)
	val simpleGraphList = reader.readToList(fileName)

  
    override def construct(nodeProvisioner : TorqueNodeProvisioner) = {
    val graph = GraphBuilder
    .withNodeProvisioner(nodeProvisioner)
//	.withKryoRegistrations(
//	    List(
//	        "com.signalcollect.dcop.MaxSumMessage",
//	        "com.signalcollect.dcop.vertices.id.MaxSumId",
//	        "scala.collection.mutable.ArrayBuffer" 
//	    ))
	.build
	graph.awaitIdle
	
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
    		
    		if(vertex.neighborhood.isEmpty){
    		  println("ERROR: neighborhood of vertex " + vertex + " is emtpy ")
    		}
    		
    		vertex.neighborhood.foreach{
    				neighbor =>
    				  val neighborVertex = simpleVertexMap(neighbor)
    				  
    				  //expand to function and variable vertex
    				  graph.addVertex(neighborVertex.functionVertex)
    				  graph.addVertex(neighborVertex.variableVertex)
    				  
    				  //connect to bidirectionally
    				  graph.addEdge(neighborVertex.functionVertex.id, new FunctionToVariable(neighborVertex.variableVertex.id))
    				  graph.addEdge(neighborVertex.variableVertex.id, new VariableToFunction(neighborVertex.functionVertex.id))
    		
    				  //connect function and variable vertex to variable and function vertex of "vertex" from outer loop
    				  graph.addEdge(vertex.functionVertex.id, new FunctionToVariable(neighborVertex.variableVertex.id))
    				  graph.addEdge(vertex.variableVertex.id, new VariableToFunction(neighborVertex.functionVertex.id))
    				  graph.addEdge(neighborVertex.functionVertex.id, new FunctionToVariable(vertex.variableVertex.id))
    				  graph.addEdge(neighborVertex.variableVertex.id, new VariableToFunction(vertex.functionVertex.id))
    		}
	}
    graph
  }
    
    def initializeRandom(n : Int) = {
    ProblemConstants.numOfColors = n
    simpleGraphList.foreach { el =>
      val pref = ArrayBuffer.fill(ProblemConstants.numOfColors)(-0.1)
      val variableId = el.variableVertex.id
      val index = Random.nextInt(ProblemConstants.numOfColors)
      pref(index) = 0.1
      ProblemConstants.initialPreferences += (variableId -> pref)
    }

    reader.storeNeighborStructure(simpleGraphList, simpleVertexMap)
    simpleVertexMap.foreach { entry =>
      entry._2.functionVertex.initializeReceivedMessages
      entry._2.variableVertex.initializeReceivedMessages
    }
  }
}