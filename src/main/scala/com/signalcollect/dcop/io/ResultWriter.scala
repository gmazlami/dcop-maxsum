package com.signalcollect.dcop.io

import com.signalcollect.dcop.benchmark.BenchmarkModes
import java.io.PrintWriter
import java.io.File
import java.util.Date

class ResultWriter(mode: BenchmarkModes.Value, graphName: String, algorithm : String , results: Any) {

  val path = {
    mode match {
      case BenchmarkModes.SyncConflictsOverTime => "results/conflictsOverTime/"
      case BenchmarkModes.AsyncResultingConflicts => "results/resultingConflicts/"
      case BenchmarkModes.SyncResultingConflicts => "results/resultingConflicts/"
      case BenchmarkModes.SyncStepsToConvergence => "results/stepsToConvergence/"
      case BenchmarkModes.AsyncTimeToConvergence => "results/timeToConvergence/"
      case BenchmarkModes.SyncTimeToConvergence => "results/timeToConvergence/"
    }
  }

  val modeName: String = mode.toString()
  val dateTime : Date = new Date(System.currentTimeMillis())
  val timeStamp = dateTime.getDate() +"-"+(dateTime.getMonth()+1)+"-"+(dateTime.getYear()+1900)+"T"+dateTime.getHours()+"-"+dateTime.getMinutes()+"-"+dateTime.getSeconds()
  val fileName = modeName + "_" + algorithm + "_" + graphName + "_" + timeStamp + ".txt"
  val newLine = "\n"
  val tabulator = "\t"
  val writer = new PrintWriter(new File(path + fileName))

  def write() = {

    results match {
      case list: List[_] => {
        list.foreach { el =>
          val tuple = el.asInstanceOf[Tuple2[_, _]]
          writer.write(tuple._1.toString)
          writer.write(" ")
          writer.write(tuple._2.toString)
          writer.write(newLine)
        }
      }
      case num: Int => {
        writer.write(num)
      }
      case longNum: Long => {
        val string = longNum.toString
        writer.write(string)
      }
    }
    writer.close()
  }

}