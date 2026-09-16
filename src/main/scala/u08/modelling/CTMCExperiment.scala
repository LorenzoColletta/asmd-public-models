package u08.modelling

import java.util.Random

object CTMCExperiment:

  import CTMCSimulation.*

  opaque type Property[A] = Trace[A] => Boolean

  given rnd: Random = new Random

  extension [S](self: CTMC[S])
    // globally is simply achieved by equivalence not G x= F not x
    def eventually[A](filt: A => Boolean): Property[A] =
      trace => trace exists (e => filt(e.state))

    // takes a property and makes it time bounded by the magics of streams
    def bounded[A](timeBound: Double)(prop: Property[A]): Property[A] =
      trace => prop(trace takeWhile (_.time <= timeBound))

    // a PRISM-like experiment, giving a statistical result (in [0,1])
    def experiment(runs: Int, prop: Property[S], s0: S, timeBound: Double): Double =
      (0 until runs).count: _ =>
        bounded(timeBound)(prop)(self.newSimulationTrace(s0 ,rnd))
      .toDouble/runs

    def globally[A](filt: A => Boolean): Property[A] =
      trace => !eventually((s: A) => !filt(s))(trace)

    def next[A](filt: A => Boolean): Property[A] =
      trace => trace.drop(1).headOption.exists(e => filt(e.state))

    def until[A](phi: A => Boolean, psi: A => Boolean): Property[A] =
      trace => trace.dropWhile(e => phi(e.state) && !psi(e.state)).headOption.exists(e => psi(e.state))

    private def timeFractionInState(filt: S => Boolean, s0: S, timeBound: Double)(using rnd: Random): Double =
      @annotation.tailrec
      def loop(trace: Trace[S], acc: Double): Double = trace match
        case curr #:: next #:: _ if curr.time < timeBound && curr.time == next.time =>
          acc + (if filt(curr.state) then timeBound - curr.time else 0.0)

        case curr #:: next #:: tail if curr.time < timeBound =>
          val addedTime = if filt(curr.state) then Math.min(next.time, timeBound) - curr.time else 0.0
          loop(next #:: tail, acc + addedTime)

        case _ => acc

      loop(self.newSimulationTrace(s0, rnd), 0.0) / timeBound

    def steadyStateExperiment(runs: Int, filt: S => Boolean, s0: S, timeBound: Double)(using rnd: Random): Double =
      (0 until runs).iterator.map(_ => self.timeFractionInState(filt, s0, timeBound)).sum / runs