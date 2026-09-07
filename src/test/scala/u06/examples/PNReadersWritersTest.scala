package u06.examples

import org.scalatest.funsuite.AnyFunSuite
import u06.utils.MSet

import u06.examples.PNReadersWriters.*

class PNReadersWritersTest extends AnyFunSuite:

  def reachableStates(initial: MSet[Place], depth: Int): Set[MSet[Place]] =
    pnME.paths(initial, depth).flatten.toSet

  test("Readers can access concurrently to P6"):
    val initial = MSet(P1, P1, P1, P5)
    val states = reachableStates(initial, depth = 10)

    val canReadConcurrently = states.exists(m => m(P6) > 1)

    assert(canReadConcurrently, "The net doesn't allow multiple tokens in P6")

  test("Reader-Writer Mutual Exclusion)"):
    val initial = MSet(P1, P1, P1, P5)
    val states = reachableStates(initial, depth = 15)

    for m <- states do
      if m(P7) > 0 then
        assert(m(P6) == 0, s"An inconsistent marking has occurred presenting token in P6 and P7.")

  test("Writer-Writer Exclusion"):
    val initial = MSet(P1, P1, P1, P1, P5)
    val states = reachableStates(initial, depth = 15)

    for m <- states do
      assert(m(P7) <= 1, s"Multiple token in P7.")

  test("Absence of deadlock"):
    val initial = MSet(P1, P1, P5)

    val completePaths = pnME.completePathsUpToDepth(initial, depth = 12)
    assert(completePaths.isEmpty, "Normal form found.")