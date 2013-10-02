package com.signalcollect.dcop.test

import com.signalcollect.nodeprovisioning.torque.TorqueHost
import com.signalcollect.nodeprovisioning.torque.LocalHost
import com.signalcollect.nodeprovisioning.torque.TorqueJobSubmitter
import com.signalcollect.nodeprovisioning.torque.TorquePriority
import java.io.File
import scala.io.Source
import com.signalcollect.dcop.io.GoogleDocsResultHandler
import com.signalcollect.dcop.scalability.Evaluation

object ScalingTest extends App {

  def jvmParameters = " -Xmx31000m" +
    " -Xms31000m" +
    " -XX:+AggressiveOpts" +
    " -XX:+AlwaysPreTouch" +
    " -XX:+UseNUMA" +
    " -XX:-UseBiasedLocking" +
    " -XX:MaxInlineSize=1024"

  def assemblyPath = "./target/scala-2.10/triplerush-assembly-1.0-SNAPSHOT.jar" //TODO: change to path for this project
  val assemblyFile = new File(assemblyPath)
  val kraken = new TorqueHost(
    jobSubmitter = new TorqueJobSubmitter(username = System.getProperty("user.name"), hostname = "kraken.ifi.uzh.ch"),
    localJarPath = assemblyPath, jvmParameters = jvmParameters, jdkBinPath = "/home/user/stutz/jdk1.7.0/bin/", priority = TorquePriority.fast) //TODO: change jdk path
  val localHost = new LocalHost
  val googleDocs = new GoogleDocsResultHandler(args(0), args(1), "triplerush", "data")

    /*********/
  def evalName = s"LUBM Triple counts."
  def runs = 1
  var evaluation = new Evaluation(evaluationName = evalName, executionHost = kraken).addResultHandler(googleDocs)
  //  var evaluation = new Evaluation(evaluationName = evalName, executionHost = localHost).addResultHandler(googleDocs)
  /*********/

  for (unis <- List(320, 160, 80, 40, 20, 10, 1)) {
    for (run <- 1 to runs) {
//      for (optimizer <- List(QueryOptimizer.Clever)) {
//        evaluation = evaluation.addEvaluationRun(lubmBenchmarkRun(
//          evalName,
//          false,
//          Long.MaxValue,
//          optimizer,
//          getRevision,
//          unis))
//      }
    }
  }
  evaluation.execute
  
  def getRevision: String = {
    try {
      val gitLogPath = ".git/logs/HEAD"
      val gitLog = new File(gitLogPath)
      val lines = Source.fromFile(gitLogPath).getLines
      val lastLine = lines.toList.last
      val revision = lastLine.split(" ")(1)
      revision
    } catch {
      case t: Throwable => "Unknown revision."
    }
  }

}