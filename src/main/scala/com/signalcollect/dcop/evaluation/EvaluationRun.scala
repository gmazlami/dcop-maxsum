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

package com.signalcollect.dcop.evaluation

import com.signalcollect.dcop.io.FileGraphReader

object EvaluationRun extends App{

  val reader : FileGraphReader = new FileGraphReader
  
  val simpleGraph = reader.read("SOMEFILENAME")
  
  //TODO: transform the graph into a factor graph
  
  //TODO: map the factor graph to S/C MaxSum Vertices and Edges
  
  //TODO: run MaxSum with the transformed graph as an input
}