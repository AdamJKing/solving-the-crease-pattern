package uk.ac.aber.adk15

import org.scalatest.{Matchers, WordSpec}

class PaperEdgeSpec extends WordSpec with Matchers {
  val OriginPoint: Point = Point(0, 0)

  "A paper edge" when {
    "constructed using the same two points" should {
      "throw an EdgeException" in {
        val ex = intercept[IllegalArgumentException] {
          new PaperEdge[OriginPoint.type](OriginPoint, OriginPoint, new MountainFold)
          new PaperEdge[OriginPoint.type](OriginPoint, OriginPoint, new ValleyFold)
          new PaperEdge[OriginPoint.type](OriginPoint, OriginPoint, new HardEdge)
        }

        ex.toString should include("An edge cannot have zero length (start point and end point are the same!)")
      }
    }
  }
}
