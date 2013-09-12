/*
 *  @author Robin Hafen
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

package com.signalcollect.dcop.evaluation.candidates

/**
 * Holder object for updating averages
 */
case class Average private (val value: Double, age: Int) {
  def updated(newValue: Double) = {
    val newAvgValue = (value * age + newValue)/(age+1)
    Average(newAvgValue, age + 1)
  }
}


object Average {
  def create(newValue: Double) = Average(newValue, 1)
  def empty = Average(0, 0)
}

