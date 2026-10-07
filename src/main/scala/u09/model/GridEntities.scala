package u09.model

import u09.model.QMatrix.Node

object GridEntities:

  trait Vision:
    def area(pos: Node): Set[Node]

  val CrossVision: Vision = p =>
    Set((p._1 + 1, p._2), (p._1 - 1, p._2), (p._1, p._2 + 1), (p._1, p._2 - 1))

  case class Enemy(pos: Node, vision: Vision, penalty: Double)

  def generateDangerZones(enemies: Set[Enemy]): Map[Node, Double] =
    enemies.flatMap(e => e.vision.area(e.pos).map(node => node -> e.penalty))
      .groupMapReduce(_._1)(_._2)(math.min)