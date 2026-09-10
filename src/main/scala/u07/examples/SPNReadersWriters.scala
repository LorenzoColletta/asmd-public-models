package u07.examples

import u07.modelling.{CTMC, SPN}
import u07.utils.MSet
import java.util.Random

object SPNReadersWriters extends App:
  // Specification of my data-type for states
  enum Place:
    case P1,P2,P3,P4,P5,P6,P7

  export Place.*
  export u07.modelling.CTMCSimulation.*
  export u07.modelling.SPN.*

  val spn = SPN[Place](
    Trn(MSet(P1), m => 1.0,   MSet(P2),  MSet()),
    Trn(MSet(P2), m => 20000.0,  MSet(P3),  MSet()),
    Trn(MSet(P3, P5), m => 10000.0,  MSet(P6),  MSet()),
    Trn(MSet(P6), m => m(P6) * 0.1,  MSet(P1),  MSet()),
    Trn(MSet(P2), m => 10000.0,  MSet(P4),  MSet()),
    Trn(MSet(P4, P5), m => 10000.0,  MSet(P7),  MSet(P6)),
    Trn(MSet(P7), m => 0.2,  MSet(P1),  MSet()))

  println:
    toCTMC(spn).newSimulationTrace(MSet(P1,P1,P5),new Random)
      .take(20)
      .toList.mkString("\n")
