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

package com.signalcollect.dcop.edges

import com.signalcollect.DefaultEdge
import com.signalcollect.dcop.vertices.FunctionVertex
import com.signalcollect.dcop.vertices.id.MaxSumId
import com.signalcollect.dcop.vertices.VariableVertex
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.dcop.util.ProblemConstants
import com.signalcollect.dcop.MaxSumMessage
import scala.math._

class FunctionToVariable(t: MaxSumId, triple : Tuple3[MaxSumId, ArrayBuffer[Double], ArrayBuffer[MaxSumId]]) extends DefaultEdge(t){
  
  override type Source = FunctionVertex

  //the signal that is computed by this edge
  def signal = R_m_n
  
  //helping boolean to tell wether the constants were already initialized
  private var initializedConstants : Boolean = false
  
  //the variable that belongs to the source function vertex of this edge
  private var ownedVariable : MaxSumId = triple._1

  // a set containing all neighbor nodes of the source node of this edge
  private var neighborSetOfSource : ArrayBuffer[MaxSumId] = triple._3

  //the variables that are involved in the computation of the sum of cross-products, which is then subtracted
  private var subtractiveTermVariables : ArrayBuffer[MaxSumId]= neighborSetOfSource - ownedVariable
  
  //a table storing the preferences needed in the computation of this message
  private var preferenceTable : ArrayBuffer[Double] = triple._2
  
  // a structure storing all the messages received by the source node of this edge; the maximum of these is summed in the computation
  private var messageMaximizations : ArrayBuffer[ArrayBuffer[Tuple3[MaxSumId,Int,Double]]] = null
  
  // a structure storing the values of the cross products between the variables in subtractiveTermVariables
  private var tableForSubtractiveTerms : ArrayBuffer[Tuple3[MaxSumId, Int, ArrayBuffer[Tuple3[MaxSumId,Int,Double]]]] = null
  
  // the variable on which the computed message depends, example : R_3_2(x2) has dependentVariable x2
  private var dependingVariable : MaxSumId = targetId
  
  // computation of R_m_n 
  def R_m_n : MaxSumMessage = {
	
//    println
//    println("--------------------------------------------------")
//    println("Computing message R_" +source.id.id + "->" + targetId.id)
//    println("--------------------------------------------------")
//    println("OwnedVariable = " + ownedVariable.id)
//    println("DependingVariable = "+ dependingVariable.id)
    
    // fill table with needed data for the cross products in the subtractive part
	subtractiveStructure()
	
	// aggregate all received messages at source vertex
	messageStructure()
//    println;print("neighborSetOfSource:  "); neighborSetOfSource.foreach(v => print(v.id + "-"));println
	var neighborHood = neighborSetOfSource
    var found : Boolean = false
    //rearrange neighborhood so that the dependingVariable is in the first cell of the ArrayBuffer
    for(i <- 0 to neighborHood.length - 1){
      if(!found){
        if(neighborHood(i) == dependingVariable){
          neighborHood -= neighborHood(i)
          found = true
        }
      }
    }
	var variableNames : ArrayBuffer[MaxSumId] = neighborHood
	dependingVariable +=: variableNames //dependingVariable in the first cell
//	println;print("variableNames:  "); variableNames.foreach(v => print(v.id + "-"));println
	//initialize variableValues of the variables in variableNames to zero
    val variableValues : ArrayBuffer[Int] = ArrayBuffer.fill(variableNames.length)(0)
	
    val R_m_n : ArrayBuffer[Double] = ArrayBuffer.fill(ProblemConstants.numOfColors)(0.0)
    
//    println("Computing maximization: ")
    /*
     * loop over outerColor
     * outerColor is the color of the dependent variable
     * the result of message R_m_n is a vector of length numOfColors, indexed by the colors of dependent variable
     */
    for(outerColor <- 0 to ProblemConstants.numOfColors - 1){
      variableValues(0) = outerColor
//      println("R_m_n("+outerColor +") : ")
      R_m_n(outerColor) = backtrack(variableNames, variableValues, 1)
    }
    
    
//	println("R_" +source.id.id + "->" + targetId.id + " = " + R_m_n)
//	println("--------------------------------------------------")
    new MaxSumMessage(source.id,targetId,R_m_n)
  }
  
  
  private def backtrack(varnames : ArrayBuffer[MaxSumId], varvalues : ArrayBuffer[Int], indexpos : Int) : Double = {
    var max : Double = Integer.MIN_VALUE
    
    // exit condition
    if(indexpos == varnames.length -1){
      max = Integer.MIN_VALUE
      for(color <- 0 to ProblemConstants.numOfColors - 1){
        varvalues(indexpos) = color
        max = math.max(max,functionValue(varnames, varvalues)) 
      }
      return max
    
    // recursion
    }else{
      max = Integer.MIN_VALUE
      for(color <- 0 to ProblemConstants.numOfColors - 1){
        varvalues(indexpos) = color
        max = math.max(max, backtrack(varnames, varvalues, indexpos + 1))
      }
      return max
    }
  }
  
  private def functionValue(varnames : ArrayBuffer[MaxSumId], varvalues : ArrayBuffer[Int]):Double = {
    val ownedVarIndex = varnames.indexOf(ownedVariable)
    val ownedVarValue = varvalues(ownedVarIndex)
    
    //preference
    val preference = preferenceTable(ownedVarValue)
    
    //subtractive part (the sum of the cross products of the variables)
    var subtractiveTerm = 0.0
    
    
    for(current <- 0 to varnames.length - 1){
      if(varnames(current) != ownedVariable){
    	val subTable = tableForSubtractiveTerms.find(entry => entry._1 == varnames(current) && entry._2 == varvalues(current)).get._3
    	val crossValue = subTable.find(entry => entry._1 == ownedVariable && entry._2 == ownedVarValue).get._3
    	subtractiveTerm += crossValue
      }
    }
    
    //the aggregated messages Q_n_m
    var messageSum = 0.0
    
    for(current <- 0 to varnames.length - 1){
      messageSum += findMessageVal(varnames(current), varvalues(current))
    }

    //compute the total value 
    val result = preference - subtractiveTerm + messageSum
//    varnames.foreach(n => print(n.id + " ")) ; print(" with config ") ; varvalues.foreach(value => print(value + " ")) ; println(" max = " +  result)
    result
  } 
  
  private def findMessageVal(variable : MaxSumId, color : Int):Double = {
    if(variable != targetId.asInstanceOf[MaxSumId]){
    	val subTable = messageMaximizations.find(subtable => subtable(0)._1 == variable).get
    	subTable.find(entry => entry._2 == color).get._3
    }else{
      0.0
    }
  }
  
  private def messageSumSet = {
    //the sum in the Function-to-variable formula goes over the neighbor ids except the target id:
    val variableIdSet = source.getNeighborIds - targetId.asInstanceOf[MaxSumId] 
	var summationSet : ArrayBuffer[MaxSumMessage] = ArrayBuffer()

	//iterate over variable set and gather the messages into a summationSet
    variableIdSet.foreach{variableId =>
    		summationSet += source.receivedMessages(variableId)
    }
    summationSet
  }
  
  private def messageStructure() = {
	val sumSet = messageSumSet
    messageMaximizations = ArrayBuffer.fill(sumSet.length)(null)
    
    //a set containing the ids of variables (VariableVertex) over which the maximization in R_m_n is taken
	val maximizationVariables : ArrayBuffer[MaxSumId] = neighborSetOfSource - targetId.asInstanceOf[MaxSumId]

    
    // aggregation of the messages Q_n_m 
    for(k <- 0 to messageSumSet.length - 1){
      val message = sumSet(k)
      val messageMaximizationResults : ArrayBuffer[Tuple3[MaxSumId,Int,Double]] = ArrayBuffer.fill(message.value.length)(null)
      //create table for Q_n_m
      for(i <- 0 to message.value.length -1){
        messageMaximizationResults(i) = (message.source,i,message.value(i))
      }
      //store table
      messageMaximizations(k) = messageMaximizationResults
    }
  }
  
  private def initializeConstants() = {
    subtractiveTermVariables = neighborSetOfSource - ownedVariable
  }
  
  private def subtractiveStructure() = {
      // store variable color combination rewards in a table 
      tableForSubtractiveTerms = ArrayBuffer.fill(subtractiveTermVariables.length * ProblemConstants.numOfColors)(null)
      var index = -1
      subtractiveTermVariables.foreach{currentVar =>
        for (color <- 0 to ProblemConstants.numOfColors - 1) {
          index = index + 1
          val subTable: ArrayBuffer[Tuple3[MaxSumId, Int, Double]] = ArrayBuffer.fill(ProblemConstants.numOfColors)(null)
          for (color2 <- 0 to ProblemConstants.numOfColors - 1) {
            if (color == color2) {
              subTable(color2) = (ownedVariable, color2, 1)
            } else {
              subTable(color2) = (ownedVariable, color2, 0)
            }
          }
          tableForSubtractiveTerms(index) = (currentVar,color,subTable)
        }
      }
  }
  
  
  
}