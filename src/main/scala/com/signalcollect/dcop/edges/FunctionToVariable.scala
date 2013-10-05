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

/**
 * Edge type that computes messages from FunctionVertex instances to VariableVertex instances.
 * Computes messages R_m_n(x_n) according to specification of the Max-Sum algorithm and its (non-stabilized)
 * utility function in Farinelli's Paper: Decentralised coordination of low-power embedded devices using the max-sum algorithm.
 * 
 * @param t: The MaxSumId of the target VariableVertex
 * @param triple: A 3-tuple containing the following values:
 * 
 * 					- triple._1 : The ownedVariable of this FunctionToVariable edge. This is the VariableVertex that has the
 *      						  same id-number as the source FunctionVertex of this edge (e.g. source = f2 --> ownedVar = v2).
 *              
 *                  - triple._2 : An ArrayBuffer[Double] that contains the initial preferences of the ownedVariable of this edge.
 *                  			  The double value at index i represents the preference of the variable for the color i.
 *                  
 *                  - triple._3 : An ArrayBuffer[MaxSumId] that contains all the MaxSumId's of the neighbor VariableVertex instances
 *                  		      of this edges source FunctionVertex instance. 
 *                   
 */
class FunctionToVariable(t: MaxSumId, triple : Tuple3[MaxSumId, ArrayBuffer[Double], ArrayBuffer[MaxSumId]]) extends DefaultEdge(t){
  
  override type Source = FunctionVertex

  //the signal that is computed by this edge
  def signal = R_m_n
  
  //helping boolean to tell wether the constants were already initialized
  private var initializedConstants : Boolean = false
  
  //the variable that belongs to the source function vertex of this edge
  private val ownedVariable : MaxSumId = triple._1

  // a set containing all neighbor nodes of the source node of this edge
  private val neighborSetOfSource : ArrayBuffer[MaxSumId] = triple._3

  //the variables that are involved in the computation of the sum of cross-products, which is then subtracted
  private val subtractiveTermVariables : ArrayBuffer[MaxSumId]= neighborSetOfSource - ownedVariable
  
  //a table storing the preferences needed in the computation of this message
  private val preferenceTable : ArrayBuffer[Double] = triple._2
  
  // a structure storing all the messages received by the source node of this edge; the maximum of these is summed in the computation
  private var messageMaximizations : ArrayBuffer[ArrayBuffer[Tuple3[MaxSumId,Int,Double]]] = null
  
  // a structure storing the values of the cross products between the variables in subtractiveTermVariables
  private var tableForSubtractiveTerms : ArrayBuffer[Tuple3[MaxSumId, Int, ArrayBuffer[Tuple3[MaxSumId,Int,Double]]]] = null
  
  // the variable on which the computed message depends, example : R_3_2(x2) has dependentVariable x2
  private val dependingVariable : MaxSumId = targetId
  
  // computation of R_m_n 
  def R_m_n : MaxSumMessage = {
    
    // fill table with needed data for the cross products in the subtractive part
	subtractiveStructure()
	
	// aggregate all received messages at source vertex
	messageStructure()
	
	//------------------------------------------------------------------------------------------------
	//rearrange neighborhood so that the dependingVariable is in the first cell of the ArrayBuffer
	var neighborHood = neighborSetOfSource
    var found : Boolean = false
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
	//------------------------------------------------------------------------------------------------
	
	//initialize variableValues of the variables in variableNames to zero
    val variableValues : ArrayBuffer[Int] = ArrayBuffer.fill(variableNames.length)(0)
	
    // container buffer for the resulting preferences in the resulting 
    val R_m_n : ArrayBuffer[Double] = ArrayBuffer.fill(ProblemConstants.numOfColors)(0.0)
    
    
    /*
     * loop over outerColor
     * outerColor is the color of the dependent variable
     * the result of message R_m_n is a vector of length numOfColors, indexed by the colors of dependent variable
     */
    for(outerColor <- 0 to ProblemConstants.numOfColors - 1){
      variableValues(0) = outerColor
      R_m_n(outerColor) = backtrack(variableNames, variableValues, 1)
    }
    
	//the resulting message of this edge
    new MaxSumMessage(source.id,targetId,R_m_n)
  }
  
  /*
   * Backtracking heuristic according to section 4.3.2 in the thesis. This function computes the maxima
   * according to Equation (15) from Farinellis Paper.
   */
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
  
  /*
   * compute the value of R_m_n for the respective varvalues configuration of varnames
   */
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
  
  /*
   * This function gathers together all the messages at the source VariableVertex into
   * a sumSet.
   */
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
  
  /*
   * Generates the table containing the structure of the olor combination rewards x_i (x) x_m  used in the
   * utility function.
   */
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