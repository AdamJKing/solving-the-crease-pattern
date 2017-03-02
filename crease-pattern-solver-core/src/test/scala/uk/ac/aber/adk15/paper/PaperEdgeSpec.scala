package uk.ac.aber.adk15.paper

import org.scalatest.{FlatSpec, Matchers}
import uk.ac.aber.adk15.Point

class PaperEdgeSpec extends FlatSpec with Matchers {
  val OriginPoint: Point = Point(0, 0)

  "A paper edge if constructed using the same two points" should "throw an EdgeException" in {
    assertThrows[Exception] {
      PaperEdge(OriginPoint, OriginPoint, MountainFold)
      PaperEdge(OriginPoint, OriginPoint, ValleyFold)
      PaperEdge(OriginPoint, OriginPoint, CreasedFold)
      PaperEdge(OriginPoint, OriginPoint, PaperBoundary)
    }
  }

  "Two unfolded edges with the same values" should "be equal" in {
    val edgeOne   = PaperEdge(Point(0, 0), Point(0, 1), MountainFold)
    val edgeTwo   = PaperEdge(Point(0, 0), Point(0, 1), MountainFold)
    val edgeThree = PaperEdge(Point(0, 1), Point(0, 0), MountainFold)

    edgeOne should equal(edgeTwo)
    edgeOne should equal(edgeThree)
  }

  "Two unfolded edges with different values" should "not be equal" in {
    val edgeOne = PaperEdge(Point(0, 0), Point(0, 1), MountainFold)
    val edgeTwo = PaperEdge(Point(1, 1), Point(0, 0), ValleyFold)

    edgeOne should not equal edgeTwo
  }

  "Two folded edges with the same values" should "be equal" in {
    val edgeOne   = PaperEdge(Point(0, 0), Point(0, 1), MountainFold)
    val edgeTwo   = PaperEdge(Point(0, 0), Point(0, 1), MountainFold)
    val edgeThree = PaperEdge(Point(0, 1), Point(0, 0), MountainFold)

    edgeOne should equal(edgeTwo)
    edgeOne should equal(edgeThree)
  }

  "Two folded edges with different values" should "not be equal" in {
    val edgeOne = PaperEdge(Point(0, 0), Point(0, 1), MountainFold)
    val edgeTwo = PaperEdge(Point(1, 1), Point(0, 0), MountainFold)

    edgeOne should not equal edgeTwo
  }
}
