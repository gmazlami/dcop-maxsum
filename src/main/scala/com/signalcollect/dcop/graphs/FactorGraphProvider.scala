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
import com.signalcollect.Graph
import com.signalcollect.dcop.vertices.FunctionVertex
import com.signalcollect.dcop.vertices.id.MaxSumId

class FactorGraphProvider(val reader : FileGraphReader, val fileName : String) extends GraphProvider{

  	val simpleVertexMap = reader.readToMap(fileName)
	val simpleGraphList = reader.readToList(fileName)

	var constants : HashMap[MaxSumId, Tuple3[MaxSumId, ArrayBuffer[Double], ArrayBuffer[MaxSumId]]] = HashMap()
  
	
  def computeAllConstants() = {
    simpleGraphList.foreach { s =>
      constants += (s.functionVertex.id -> getConstants(s.functionVertex.id.idNumber))
    }
  }
	
    override def construct(nodeProvisioner : TorqueNodeProvisioner) = {
    computeAllConstants  
    val graph = GraphBuilder.withNodeProvisioner(nodeProvisioner).build
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
    		graph.addEdge(vertex.functionVertex.id, new FunctionToVariable(vertex.variableVertex.id, constants(vertex.functionVertex.id)))
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
    				  graph.addEdge(neighborVertex.functionVertex.id, new FunctionToVariable(neighborVertex.variableVertex.id, constants(neighborVertex.functionVertex.id)))
    				  graph.addEdge(neighborVertex.variableVertex.id, new VariableToFunction(neighborVertex.functionVertex.id))
    		
    				  //connect function and variable vertex to variable and function vertex of "vertex" from outer loop
    				  graph.addEdge(vertex.functionVertex.id, new FunctionToVariable(neighborVertex.variableVertex.id,constants(vertex.functionVertex.id)))
    				  graph.addEdge(vertex.variableVertex.id, new VariableToFunction(neighborVertex.functionVertex.id))
    				  graph.addEdge(neighborVertex.functionVertex.id, new FunctionToVariable(vertex.variableVertex.id,constants(neighborVertex.functionVertex.id)))
    				  graph.addEdge(neighborVertex.variableVertex.id, new VariableToFunction(vertex.functionVertex.id))
    		}
	}
    initilalizeMessages
    graph
  }
    
  
   def initilalizeMessages() = {
    simpleVertexMap.foreach { entry =>
      entry._2.functionVertex.initializeReceivedMessages
      entry._2.variableVertex.initializeReceivedMessages
    }
  }  
    
  private def getConstants(idNum : Int) = {
    val ownedVariable : MaxSumId = new MaxSumId(idNum , 0)
    val randomPreferenceTable = getRandomPreference(ownedVariable)
    val neighborSet = getNeighborSet(idNum, ownedVariable)
    (ownedVariable,randomPreferenceTable,neighborSet)
  }
  
  private def getRandomPreference(ownedVar : MaxSumId) = {
      val pref = ArrayBuffer.fill(ProblemConstants.numOfColors)(-0.1)
      val variableId = ownedVar
      val index = Random.nextInt(ProblemConstants.numOfColors)
      pref(index) = 0.1
      pref
  }
  
  private def getNeighborSet(functionIdNum : Int, ownedVar : MaxSumId) = {
    var neighborSetForFunction: ArrayBuffer[MaxSumId] = reader.getNeighbors(functionIdNum, simpleVertexMap)
    neighborSetForFunction += ownedVar
    neighborSetForFunction
  }
}