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

package com.signalcollect.dcop.io

import scala.io.Source
import scala.collection.mutable.LinkedHashSet

class AdoptFileReader(fileName: String) {

  val lines = Source.fromFile(fileName).getLines.toList

  def read() = getFromText(lines)
      
  var undirectedEdges = LinkedHashSet[Set[Int]]()

  var vertices = scala.collection.mutable.Set[Int]()

  def getGraphTuple = {
    (undirectedEdges,vertices)
  }

  def getFromText(textLines: List[String]): Unit = {
    textLines match {
      case Nil => Unit

      case tl :: tls => {
        val splitTextLine = tl.split("\\s+")
        splitTextLine(0) match {

          case "AGENT" => getFromText(tls) //lose it

          case "VARIABLE" => getFromText(tls) //lose it

          case "NOGOOD" => getFromText(tls)//lose it

          case "CONSTRAINT" => {

            val tlsRest = tls.dropWhile(x => x.split("\\s+")(0) == "NOGOOD")

            val constraintVariables = splitTextLine.toList.drop(1) map (x => x.toInt)
            val first = constraintVariables(0)
            val second = constraintVariables(1)
            
            undirectedEdges.add(Set(first,second))
            
            if(!vertices.contains(first)){
              vertices += first
            }
            if(!vertices.contains(second)){
              vertices += second
            }
            getFromText(tls)
          }

        }
      }
    }
  }

}