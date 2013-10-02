package com.signalcollect.dcop.test

import com.signalcollect.dcop.graphs.GraphGenerator

object GraphGeneratorTest extends App {

  val g = new GraphGenerator(500,3,3,"graphs/graph500.txt")
  g.generateToFile
  println("Finished!")
}