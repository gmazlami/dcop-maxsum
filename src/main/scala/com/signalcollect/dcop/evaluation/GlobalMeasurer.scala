package com.signalcollect.dcop.evaluation

import scala.collection.mutable.ArrayBuffer

object GlobalMeasurer{

  val dsaInstrument : MeasuringInstrument = new MeasuringInstrument("DSA")
  
  val brInstrument : MeasuringInstrument = new MeasuringInstrument("BR")
  
  val maxsumInstrument : MeasuringInstrument = new MeasuringInstrument("Max-Sum")
  
}