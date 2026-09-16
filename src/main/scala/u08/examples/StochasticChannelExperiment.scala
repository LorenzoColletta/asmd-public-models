package u08.examples

import scala.math.BigDecimal.double2bigDecimal
import scala.u08.utils.Time
import java.util.Random

object StochasticChannelExperiment extends App with de.sciss.chart.module.Charting:
  import u08.modelling.CTMCExperiment.*
  import u08.examples.StochasticChannel.*

  given rnd: Random = new Random

  val runsCount = 26000 

  Time.timed:
    val dataEventually = for t <- 0.1 to 10.0 by 0.1 yield
      (t, stocChannel.experiment(runsCount, stocChannel.eventually(_ == DONE), IDLE, t.toDouble))

    val dataGlobally = for t <- 0.1 to 10.0 by 0.1 yield
      (t, stocChannel.experiment(runsCount, stocChannel.globally(_ != FAIL), IDLE, t.toDouble))

    val dataNext = for t <- 0.1 to 10.0 by 0.1 yield
      (t, stocChannel.experiment(runsCount, stocChannel.next(_ == SEND), IDLE, t.toDouble))

    val dataUntil = for t <- 0.1 to 10.0 by 0.1 yield
      (t, stocChannel.experiment(runsCount, stocChannel.until(_ != FAIL, _ == DONE), IDLE, t.toDouble))

    val dataSteadyState = for t <- 0.1 to 10.0 by 0.1 yield
      (t, stocChannel.steadyStateExperiment(runsCount, _ == DONE, IDLE, t.toDouble))

    given ChartTheme = ChartTheme.Default

    val chart = de.sciss.chart.api.XYLineChart(Seq(
      "Eventually DONE" -> dataEventually,
      "Globally NOT FAIL" -> dataGlobally,
      "Next is SEND" -> dataNext,
      "Until DONE (No FAIL)" -> dataUntil,
      "SteadyState DONE (Time Fraction)" -> dataSteadyState
    ))
    chart.show("Stochastic Channel - Operators Comparison")