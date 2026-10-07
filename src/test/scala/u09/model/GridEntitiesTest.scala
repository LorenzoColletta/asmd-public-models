package u09.model

import org.scalatest.funsuite.AnyFunSuite
import u09.model.GridEntities.*
import u09.model.QMatrix.Node

class GridEntitiesTest extends AnyFunSuite:

  test("CrossVision computes four adjacent cells excluding the center"):
    val center: Node = (2, 2)
    val visionArea = CrossVision.area(center)

    assert(visionArea.size == 4)
    assert(visionArea.contains((3, 2)))
    assert(visionArea.contains((1, 2)))
    assert(visionArea.contains((2, 3)))
    assert(visionArea.contains((2, 1)))

    assert(!visionArea.contains(center))

  test("generateDangerZones maps adjacent cells to a given penalty"):
    val enemy = Enemy((1, 1), CrossVision, -10.0)
    val dangerMap = generateDangerZones(Set(enemy))

    assert(dangerMap.size == 4)
    assert(dangerMap((2, 1)) == -10.0)
    assert(dangerMap((0, 1)) == -10.0)
    assert(dangerMap((1, 2)) == -10.0)
    assert(dangerMap((1, 0)) == -10.0)

    assert(!dangerMap.contains((1, 1)))

  test("generateDangerZones maps the cells where the areas of multiple enemies overlap to the maximum penalty " +
    "(minimum value)."):
    val enemy1 = Enemy((2, 2), CrossVision, -10.0)
    val enemy2 = Enemy((4, 2), CrossVision, -20.0)

    val dangerMap = generateDangerZones(Set(enemy1, enemy2))

    assert(dangerMap((3, 2)) == -20.0)

    assert(dangerMap((1, 2)) == -10.0)
    assert(dangerMap((5, 2)) == -20.0)

  test("generateDangerZones returns an empty map in case of no enemies"):
    val dangerMap = generateDangerZones(Set.empty)
    assert(dangerMap.isEmpty)