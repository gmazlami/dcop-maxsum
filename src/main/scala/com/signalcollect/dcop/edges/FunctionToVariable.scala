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
import com.signalcollect.dcop.exceptions.TableEntryNotFoundException

class FunctionToVariable(t: MaxSumId) extends DefaultEdge(t){
  
  override type Source = FunctionVertex

  //the signal that is computed by this edge
  def signal = R_m_n
  
  private val ownedVariable = ProblemConstants.getOwnedVariable(source.id)

  private val subtractiveTermVariables = neighborSetOfSource - ownedVariable
  
  private val preferenceTable  = ProblemConstants.getPreferenceTable(ownedVariable)
  
  private val neighborSetOfSource : ArrayBuffer[MaxSumId] = ProblemConstants.neighborStructure(source.id)
  
  private val messageMaximizations : ArrayBuffer[ArrayBuffer[Tuple3[MaxSumId,Int,Double]]] = ArrayBuffer.fill(messageSumSet.length)(null)
  
  private val tableForSubtractiveTerms : ArrayBuffer[Tuple3[MaxSumId, Int, ArrayBuffer[Tuple3[MaxSumId,Int,Double]]]] = ArrayBuffer.fill(subtractiveTermVariables.length * 2)(null)
  
  private val dependingVariable : MaxSumId = targetId
  
  
  def R_m_n : MaxSumMessage = {
	
	preferenceStructure()
	messageStructure()
    
    val variableNames : ArrayBuffer[MaxSumId] = neighborSetOfSource
    val variableValues : ArrayBuffer[Int] = ArrayBuffer.fill(variableNames.length)(0)
	
    val R_m_n : ArrayBuffer[Double] = ArrayBuffer.fill(ProblemConstants.numOfColors)(0.0)
    
    for(outerColor <- 0 to ProblemConstants.numOfColors - 1){
      variableNames(0) = dependingVariable
      variableValues(0) = outerColor
      
      R_m_n(outerColor) = backtrack(variableNames, variableValues, 1)
      
    }

    new MaxSumMessage(source.id,targetId,R_m_n)
  }
  
  
  private def backtrack(varnames : ArrayBuffer[MaxSumId], varvalues : ArrayBuffer[Int], indexpos : Int) : Double = {
    var max : Double = Integer.MIN_VALUE
    if(indexpos == varnames.length -1){
      max = Integer.MIN_VALUE
      
      for(color <- 0 to ProblemConstants.numOfColors - 1){
        varvalues(indexpos) = color
        max = math.max(max,functionValue(varnames, varvalues)) 
      }
      return max
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
    val preference = preferenceTable(ownedVarValue)._3
    
    //subtractive part (the sum of the cross products of the variables)
    var subtractiveTerm = 0.0
    
    for(current <- 0 to varnames.length - 1){
    	
      val subTable = tableForSubtractiveTerms.find(entry => entry._1 == varnames(current) && entry._2 == varvalues(current)).get._3
      val crossValue = subTable.find(entry => entry._1 == ownedVariable && entry._2 == ownedVarValue).get._3
      subtractiveTerm += crossValue
      
    }
    
    //the aggregated messages Q_n_m
    var messageSum = 0.0
    
    for(current <- 0 to varnames.length - 1){
      messageSum += findMessageVal(varnames(current), varvalues(current))
    }

    preference - subtractiveTerm + messageSum
  } 
  
  private def findMessageVal(variable : MaxSumId, color : Int):Double = {
    val subTable = messageMaximizations.find(subtable => subtable(0)._1 == variable).get
    subTable.find(entry => entry._2 == color).get._3
  }
  
  private def messageSumSet = {
    var summationSet : ArrayBuffer[MaxSumMessage] = ArrayBuffer()
    //the sum in the Function-to-variable formula goes over the neighbor ids except the target id:
    val variableIdSet = source.getNeighborIds - targetId.asInstanceOf[MaxSumId] 
    //iterate over variable set and gather the messages into a summationSet
    variableIdSet.foreach{variableId =>
    		summationSet :+ source.receivedMessages(variableId)
    }
    summationSet
  }
  
  private def messageStructure() = {
    	//a set containing the ids of variables (VariableVertex) over which the maximization in R_m_n is taken
	val maximizationVariables : ArrayBuffer[MaxSumId] = neighborSetOfSource - targetId.asInstanceOf[MaxSumId]

    val sumSet = messageSumSet
    
    //-------------------------------- aggregation of the messages Q_n_m ------------------------------------
    for(k <- 0 to messageSumSet.length){
      val message = sumSet(k)
      val messageMaximizationResults : ArrayBuffer[Tuple3[MaxSumId,Int,Double]] = ArrayBuffer.fill(message.value.length)(null)
      //create table for Q_n_m
      for(i <- 0 to message.value.length){
        messageMaximizationResults(i) = (message.source,i,message.value(i))
      }
      //store table
      messageMaximizations(k) = messageMaximizationResults
    }
  }
  
  private def preferenceStructure() = {
      //-------------------------------- store variable color combination rewards in a table ----------------------------------------
      
      var index = -1
      for (i <- 0 to subtractiveTermVariables.length) {
        val currentVar = subtractiveTermVariables(i)
        for (color <- 0 to ProblemConstants.numOfColors) {
          index = index + 1
          val subTable: ArrayBuffer[Tuple3[MaxSumId, Int, Double]] = ArrayBuffer()
          for (color2 <- 0 to ProblemConstants.numOfColors) {
            if (color == color2) {
              subTable(color2) = (ownedVariable, color2, 1)
            } else {
              subTable(color2) = (ownedVariable, color2, 0)
            }
          }
          tableForSubtractiveTerms(index) = (ownedVariable,color,subTable)
        }
      }        

  }
  
  
  
}