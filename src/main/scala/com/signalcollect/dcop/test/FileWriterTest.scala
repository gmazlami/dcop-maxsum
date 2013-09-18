package com.signalcollect.dcop.test

import java.io.PrintWriter
import java.io.File

object FileWriterTest extends App {

      val writer = new PrintWriter(new File("results/conflicts/test.txt" ))
      writer.write("\t")
      writer.write("Hello Scala")
      writer.write("\n")
      writer.write("\t")
      writer.write("Next String ")
      writer.close()
}