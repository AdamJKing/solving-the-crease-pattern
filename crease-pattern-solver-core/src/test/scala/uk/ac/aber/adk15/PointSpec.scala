package uk.ac.aber.adk15

import org.scalatest.{FlatSpec, Matchers}

class PointSpec extends FlatSpec with Matchers {

  "Two points with the same X, Y values" should "be equal" in {
    Point(0, 0) should be(Point(0, 0))
  }

  "A point" should "be correctly comparable to a line" in {
    Point(0, 1) compareTo(Point(0, 0), Point(1, 1)) should be < 0
    Point(1, 0) compareTo(Point(0, 0), Point(1, 1)) should be > 0
    Point(0, 0) compareTo(Point(0, 0), Point(1, 1)) should be
    0
  }
}
