package uk.ac.aber.adk15.paper

import org.scalatest.{FlatSpec, Matchers}
import uk.ac.aber.adk15.geometry.Point
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._

class FoldSpec extends FlatSpec with Matchers {
  val OriginPoint: Point = Point(0, 0)

  "A paper edge if constructed using the same two points" should "throw an EdgeException" in {
    assertThrows[Exception] {
      OriginPoint /\ OriginPoint
      OriginPoint \/ OriginPoint
      OriginPoint ~~ OriginPoint
      OriginPoint -- OriginPoint
    }
  }

  "Two unfolded edges with the same values" should "be equal" in {
    val edgeOne   = Point(0, 0) /\ Point(0, 1)
    val edgeTwo   = Point(0, 0) /\ Point(0, 1)
    val edgeThree = Point(0, 1) /\ Point(0, 0)

    edgeOne should equal(edgeTwo)
    edgeOne.hashCode() should equal(edgeTwo.hashCode())

    edgeOne should equal(edgeThree)
    edgeOne.hashCode() should equal(edgeThree.hashCode())

    (Point(0, 0) \/ Point(25, 25)) should equal(Point(25, 25) \/ Point(50, 50))
  }

  "Two unfolded edges with different values" should "not be equal" in {
    val edgeOne   = Point(0, 0) /\ Point(0, 1)
    val edgeTwo   = Point(1, 1) \/ Point(0, 0)
    val edgeThree = Point(2, 2) /\ Point(1, 1)

    Point(25, 25) /\ Point(50, 50) should not equal Point(50, 50) \/ Point(25, 25)
    Point(0, 100) \/ Point(50, 50) should not equal Point(50, 50) \/ Point(25, 25)
    Point(25, 25) \/ Point(50, 50) should not equal Point(25, 25) ~~ Point(50, 0)

    edgeOne should not equal edgeTwo
    edgeOne.hashCode() should not equal edgeTwo.hashCode()
    edgeTwo should not equal edgeThree
  }

  "Two folded edges with the same values" should "be equal" in {
    val edgeOne   = Point(0, 0) ~~ Point(0, 1)
    val edgeTwo   = Point(0, 0) ~~ Point(0, 1)
    val edgeThree = Point(0, 1) ~~ Point(0, 0)

    edgeOne should equal(edgeTwo)
    edgeOne.hashCode() should equal(edgeTwo.hashCode())

    edgeOne should equal(edgeThree)
    edgeOne.hashCode() should equal(edgeThree.hashCode())
  }

  "Two folded edges with different values" should "not be equal" in {
    val edgeOne = Point(0, 0) ~~ Point(0, 1)
    val edgeTwo = Point(1, 1) ~~ Point(0, 0)

    edgeOne should not equal edgeTwo
    edgeOne.hashCode() should not equal edgeTwo.hashCode()
  }

  "Two edges with the same type but different lengths" should "be the same" in {
    val edgeOne   = Point(0, 0) /\ Point(100, 100)
    val edgeTwo   = Point(25, 25) /\ Point(75, 75)
    val edgeThree = Point(50, 50) /\ Point(100, 100)
    val edgeFour  = Point(50, 50) /\ Point(0, 100)

    edgeOne should equal(edgeTwo)
    edgeOne.hashCode() should equal(edgeTwo.hashCode())

    edgeOne should equal(edgeThree)
    edgeOne.hashCode() should equal(edgeThree.hashCode())

    all(List(edgeOne, edgeTwo, edgeThree)) should not equal edgeFour
    withClue(edgeFour.hashCode()) {
      all(List(edgeOne, edgeTwo, edgeThree) map (_.hashCode())) should not equal edgeFour
        .hashCode()
    }
  }
}
