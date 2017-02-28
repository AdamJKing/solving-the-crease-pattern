package uk.ac.aber.adk15

import org.scalatest.{Matchers, WordSpec}
import uk.ac.aber.adk15.paper._

class PaperEdgeSpec extends WordSpec with Matchers {
  val OriginPoint: Point = Point(0, 0)

  "A paper edge" when {
    "constructed using the same two points" should {
      "throw an EdgeException" in {
        val ex = intercept[IllegalArgumentException] {
          new UnfoldedPaperEdge[OriginPoint.type](OriginPoint, OriginPoint, new MountainFoldType)
          new UnfoldedPaperEdge[OriginPoint.type](OriginPoint, OriginPoint, new ValleyFoldType)
          new FoldedPaperEdge[OriginPoint.type](OriginPoint, OriginPoint)
        }

        ex.toString should include("An edge cannot have zero length (start point and end point are the same!)")
      }
    }
  }
}
