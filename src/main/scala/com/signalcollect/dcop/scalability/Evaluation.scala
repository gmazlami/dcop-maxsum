package com.signalcollect.dcop.scalability

import com.signalcollect.nodeprovisioning.torque._
import scala.util.Random

case class Evaluation(
  evaluationName: String,
  executionHost: ExecutionHost = new LocalHost,
  evaluationRuns: List[() => List[Map[String, String]]] = List(),
  resultHandlers: List[Map[String, String] => Unit] = List(println(_)),
  extraStats: Map[String, String] = Map()) {
  
  def addEvaluationRun(evaluationRun: () => List[Map[String, String]]) = Evaluation(evaluationName, executionHost, evaluationRun :: evaluationRuns, resultHandlers, extraStats)
  def addResultHandler(resultHandler: Map[String, String] => Unit) = Evaluation(evaluationName, executionHost, evaluationRuns, resultHandler :: resultHandlers, extraStats)
  def addExtraStats(stats: Map[String, String]) = Evaluation(evaluationName, executionHost, evaluationRuns, resultHandlers, extraStats ++ stats)
  def execute {
    val jobs = evaluationRuns map { evaluationRun =>
      val jobId = Random.nextInt.abs % 1000000
      val jobFunction = () => {
        println(s"Job $jobId is being executed ...")
        val stats = evaluationRun() // Execute evaluation.
        for (stat <- stats) {
          resultHandlers foreach (handler => handler(stat ++ extraStats ++ Map("jobId" -> jobId.toString)))
        }
        println("Done.")
      }
      Job(jobFunction, jobId)
    }
    executionHost.executeJobs(jobs)
  }
}