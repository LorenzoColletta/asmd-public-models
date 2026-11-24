package scala.u07.examples

import u07.utils.Time
import java.util.Random
import u07.examples.StochasticChannel.*
import scala.u07.utils.TraceAnalytics.{takeWhileInclusive,averageTimeTo, averageRelativeTime}

@main def mainTimedStochasticChannelSimulation(): Unit =
  val nRuns = 1000
  val rnd = new Random()

  println(s"Starting $nRuns runs...")

  Time.timed:
    val runs: List[Trace[State]] = (1 to nRuns).map(_ =>
      stocChannel.newSimulationTrace(IDLE, rnd).takeWhileInclusive(_.state != DONE)
    ).toList

    val avgDoneTime = runs.averageTimeTo(DONE)
    println(f"Average time to get DONE: $avgDoneTime%.5f")

    val failPercentage = runs.averageRelativeTime(FAIL, DONE)
    println(f"Relative amount of time the system is in FAIL until DONE (Rate): $failPercentage%f%%")
