package u09.model

import org.scalatest.funsuite.AnyFunSuite
import u09.model.QMatrix.{Move, Node}
import u09.model.GridBuilders.*
import u09.model.GridEntities.*

class GridBuildersTest extends AnyFunSuite:

  test("standardMove doesn't allow moves outside the grid"):
    val move = standardMove(5, 5)

    assert(move((0, 0), Move.UP) == (0, 0))
    assert(move((0, 0), Move.LEFT) == (0, 0))
    assert(move((4, 4), Move.DOWN) == (4, 4))
    assert(move((4, 4), Move.RIGHT) == (4, 4))
    assert(move((2, 2), Move.UP) == (2, 1))

  test("withObstacles doesn't allow a move towards an adjacent obstacle and applies a penalty"):
    val b = spatial(5, 5).withObstacles(Set((1, 1)), penalty = -5.0)
    val s: Node = (1, 0)

    assert(b.transition(s, Move.DOWN) == (1, 0))
    assert(b.rewardFunc(s, Move.DOWN) == -5.0)
    assert(b.transition(s, Move.RIGHT) == (2, 0))
    assert(b.rewardFunc(s, Move.RIGHT) == 0.0)

  test("withHoles jumps to a respawn spot"):
    val start: Node = (0, 0)
    val b = spatial(5, 5).withHoles(Set((2, 2)), penalty = -50.0, respawn = start)
    val s: Node = (2, 1)

    assert(b.transition(s, Move.DOWN) == start)
    assert(b.rewardFunc(s, Move.DOWN) == -50.0)

  test("withEnemies acts as an obstacle and its adjacent positions give negative penalty"):
    val enemy = Enemy((2, 2), CrossVision, -10.0)
    val b = spatial(5, 5).withEnemies(Set(enemy), bumpPenalty = -20.0)

    assert(b.transition((2, 1), Move.DOWN) == (2, 1))
    assert(b.rewardFunc((2, 1), Move.DOWN) == -20.0)
    assert(b.transition((1, 1), Move.RIGHT) == (2, 1))
    assert(b.rewardFunc((1, 1), Move.RIGHT) == -10.0)

  test("withItems changes the state and applies wanderPenalty"):
    val b = withItems(5, 5, itemPos = (4, 4), basePos = (0, 0), itemReward = 50.0, returnReward = 100.0, wanderPenalty = -2.0)

    val s1: (Node, Boolean) = ((4, 3), false)
    assert(b.transition(s1, Move.DOWN) == ((4, 4), true))
    assert(b.rewardFunc(s1, Move.DOWN) == 50.0)

    val s2: (Node, Boolean) = ((4, 4), true)
    assert(b.transition(s2, Move.UP) == ((4, 3), true))
    assert(b.rewardFunc(s2, Move.UP) == -2.0)

    val s3: (Node, Boolean) = ((1, 0), true)
    assert(b.transition(s3, Move.LEFT) == ((0, 0), true))
    assert(b.rewardFunc(s3, Move.LEFT) == 100.0)

  test("Bumping into an obstacle doesn't change the state regarding item acquisition"):
    val b = withItems(5, 5, itemPos = (4, 4), basePos = (0, 0), itemReward = 50.0, returnReward = 100.0, wanderPenalty = -2.0)
      .withObstacles(Set((2, 2)), penalty = -5.0)
    val s: (Node, Boolean) = ((2, 1), true)

    assert(b.transition(s, Move.DOWN) == ((2, 1), true)) 
    assert(b.rewardFunc(s, Move.DOWN) == -5.0)           

  test("An obstacle overlapping an item doesn't allow the item to be acquired"):
    val b = withItems(5, 5, itemPos = (2, 2), basePos = (0, 0), itemReward = 50.0, returnReward = 100.0, wanderPenalty = -2.0)
      .withObstacles(Set((2, 2)), penalty = -10.0)
    val s: (Node, Boolean) = ((2, 1), false)

    assert(b.transition(s, Move.DOWN) == ((2, 1), false))
    assert(b.rewardFunc(s, Move.DOWN) == -10.0)