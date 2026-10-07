package u09.model

import u09.model.QMatrix.{Node, Move}
import u09.model.GridEntities.{Enemy, generateDangerZones}

object GridBuilders:

  def standardMove(width: Int, height: Int)(s: Node, a: Move): Node = (s, a) match
    case ((n1, n2), Move.UP) => (n1, (n2 - 1) max 0)
    case ((n1, n2), Move.DOWN) => (n1, (n2 + 1) min (height - 1))
    case ((n1, n2), Move.LEFT) => ((n1 - 1) max 0, n2)
    case ((n1, n2), Move.RIGHT) => ((n1 + 1) min (width - 1), n2)

  case class Builder[S](
                         width: Int, height: Int,
                         transition: (S, Move) => S,
                         rewardFunc: (S, Move) => Double,
                         extractNode: S => Node
                       ):

    def nextNode(s: S, a: Move): Node = extractNode(transition(s, a))

    def withObstacles(obs: Set[Node], penalty: Double): Builder[S] =
      copy(
        rewardFunc = (s, a) => if obs.contains(nextNode(s, a)) then penalty else this.rewardFunc(s, a),
        transition = (s, a) => if obs.contains(nextNode(s, a)) then s else this.transition(s, a)
      )

    def withHoles(holes: Set[Node], penalty: Double, respawn: S): Builder[S] =
      copy(
        rewardFunc = (s, a) => if holes.contains(nextNode(s, a)) then penalty else this.rewardFunc(s, a),
        transition = (s, a) => if holes.contains(nextNode(s, a)) then respawn else this.transition(s, a)
      )

    def withEnemies(enemies: Set[Enemy], bumpPenalty: Double): Builder[S] =
      val enemyPos = enemies.map(_.pos)
      val dangerMap = generateDangerZones(enemies)

      copy(
        rewardFunc = (s, a) =>
          val dest = nextNode(s, a)
          if enemyPos.contains(dest) then bumpPenalty
          else if dangerMap.contains(dest) then dangerMap(dest)
          else this.rewardFunc(s, a),
        transition = (s, a) =>
          if enemyPos.contains(nextNode(s, a)) then s else this.transition(s, a)
      )

    def build(initial: S, 
              terminal: PartialFunction[S, Boolean], 
              goalReward: PartialFunction[(S, Move), Double], 
              gamma: Double,
              alpha: Double,
              epsilon: Double, 
              v0: Double = 1.0): GenericFacade[S] =
      val finalReward: (S, Move) => Double = (s, a) => goalReward.applyOrElse((s, a), _ => rewardFunc(s, a))
      GenericFacade(initial, terminal, finalReward, transition, gamma, alpha, epsilon, v0)

  def spatial(width: Int, height: Int): Builder[Node] =
    Builder[Node](width, height, standardMove(width, height), (_, _) => 0.0, identity)

  def withItems(width: Int, height: Int, itemPos: Node, basePos: Node, itemReward: Double, returnReward: Double, wanderPenalty: Double): Builder[(Node, Boolean)] =
    val b = Builder[(Node, Boolean)](
      width, height,
      transition = { case ((pos, hasItem), a) => (standardMove(width, height)(pos, a), hasItem) },
      rewardFunc = (_, _) => 0.0,
      extractNode = _._1
    )
    val currTransition = b.transition
    b.copy(
      rewardFunc = (s, a) => {
        val dest = currTransition(s, a)._1
        if !s._2 && dest == itemPos then itemReward
        else if s._2 && dest == basePos then returnReward
        else if s._2 then wanderPenalty
        else 0.0
      },
      transition = (s, a) => {
        val destS = currTransition(s, a)
        if !s._2 && destS._1 == itemPos then (destS._1, true) else destS
      }
    )

  def printGrid[S](width: Int, height: Int, mapState: Node => S, display: S => String): String =
    (for {row <- 0 until height; col <- 0 until width}
      yield "%7s".format(display(mapState((col, row)))) + (if (col == width - 1) "\n" else "\t")).mkString("")  