package uk.ac.aber.adk15.paper

import org.scalatest.{FlatSpec, Matchers}
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._

class PaperEdgeSpec extends FlatSpec with Matchers {
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
    edgeOne should equal(edgeThree)
  }

  "Two unfolded edges with different values" should "not be equal" in {
    val edgeOne = Point(0, 0) /\ Point(0, 1)
    val edgeTwo = Point(1, 1) \/ Point(0, 0)

    edgeOne should not equal edgeTwo
  }

  "Two folded edges with the same values" should "be equal" in {
    val edgeOne   = Point(0, 0) /\ Point(0, 1)
    val edgeTwo   = Point(0, 0) /\ Point(0, 1)
    val edgeThree = Point(0, 1) /\ Point(0, 0)

    edgeOne should equal(edgeTwo)
    edgeOne should equal(edgeThree)
  }

  "Two folded edges with different values" should "not be equal" in {
    val edgeOne = Point(0, 0) /\ Point(0, 1)
    val edgeTwo = Point(1, 1) /\ Point(0, 0)

    edgeOne should not equal edgeTwo
  }
}
