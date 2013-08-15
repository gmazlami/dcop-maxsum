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

package com.signalcollect.dcop

import com.signalcollect.GraphBuilder
import com.signalcollect.dcop.util.ProblemConstants
import com.signalcollect.dcop.vertices.id.MaxSumId
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.dcop.io.FileGraphReader
import com.signalcollect.dcop.graphs.FactorGraphTransformer



object MaxSumAlgorithm extends App{

  
    //dummy utility function
  val utilityFunction = (s : Set[Double]) => 0.0
  
  val reader : FileGraphReader = new FileGraphReader
  val transformer : FactorGraphTransformer = new FactorGraphTransformer
  
  val simpleGraph = reader.readToMap("SOMEFILENAME")
  val signalCollectFactorGraph = transformer.transform(simpleGraph, utilityFunction,2)
  
  ProblemConstants.numOfColors = 2
  ProblemConstants.colors = Set(0,1)
  ProblemConstants.initialPreferences + (new MaxSumId(1,0) -> ArrayBuffer(0.1 , -0.1))
  ProblemConstants.initialPreferences + (new MaxSumId(2,0) -> ArrayBuffer(-0.1 , 0.1))
  ProblemConstants.initialPreferences + (new MaxSumId(3,0) -> ArrayBuffer(-0.1 , 0.1))
  
}