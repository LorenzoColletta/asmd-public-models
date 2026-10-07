package u09.examples

import u09.model.GridBuilders
import u09.model.GridEntities.{Enemy, CrossVision}

object TryGenericTasks extends App:

  println("TASK 1: ZIGZAG WALK")

  val obs = Set((1, 0), (1, 1), (1, 2), (3, 2), (3, 3), (3, 4))
  val hls = Set((2, 0))
  val enms = Set(Enemy((2, 4), CrossVision, -15.0))
  val startNode = (0, 0)
  val goalNode = (4, 4)
  val gamma = 0.9
  val alpha = 0.5
  val epsilon = 0.3

  val builder1 = GridBuilders.spatial(5, 5)
    .withObstacles(obs, penalty = -5.0)

  val rlTask1 = builder1.build(
    initial = startNode,
    terminal = { case `goalNode` => true; case _ => false },
    goalReward = { case (s, a) if builder1.nextNode(s, a) == goalNode => 100.0 },
    gamma, alpha, epsilon
  )

  val q1 = rlTask1.makeLearningInstance().learn(15000, 100, rlTask1.qFunction)
  println(GridBuilders.printGrid(5, 5, identity, s =>
    if obs.contains(s) || enms.map(_.pos).contains(s) then "X" else q1.bestPolicy(s).toString
  ))

  println("\nTASK 2: COLLECT ITEM AND RETURN")

  val builder2 = GridBuilders.withItems(5, 5, itemPos = (4, 4), basePos = (0, 0), 50.0, 100.0, -2.0)
    .withObstacles(Set((2, 2)), penalty = -5.0)

  val rlTask2 = builder2.build(
    initial = ((0, 0), false),
    terminal = { case (pos, true) if pos == (0, 0) => true; case _ => false },
    goalReward = PartialFunction.empty,
    gamma, alpha, epsilon
  )

  val q2 = rlTask2.makeLearningInstance().learn(20000, 100, rlTask2.qFunction)

  println("Policy: acquire the item:")
  println(GridBuilders.printGrid(5, 5, pos => (pos, false), s => q2.bestPolicy(s).toString))

  println("\nPolicy: return to base:")
  println(GridBuilders.printGrid(5, 5, pos => (pos, true), s => q2.bestPolicy(s).toString))