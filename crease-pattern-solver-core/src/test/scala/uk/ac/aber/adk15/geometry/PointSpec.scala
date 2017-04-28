package uk.ac.aber.adk15.geometry

import org.scalatest.{FlatSpec, Matchers}

class PointSpec extends FlatSpec with Matchers {

  "Two points with the same X, Y values" should "be equal" in {
    Point(0, 0) should be(Point(0, 0))
  }

  "Two points with different X, Y values" should "not be equal" in {
    Point(0, 1) should not be Point(10, 0)
  }

  "A point" should "be correctly comparable to a line" in {
    Point(0, 1) compareTo (Point(0, 0), Point(1, 1)) should be < 0.0
    Point(1, 0) compareTo (Point(0, 0), Point(1, 1)) should be > 0.0
    Point(0, 0) compareTo (Point(0, 0), Point(1, 1)) should be(0)
  }

  "A point" should "be reflect-able across a line" in {
    val pointA = Point(100, 0) reflectedOver (Point(0, 0), Point(100, -100))
    pointA should be(Point(0, -100))

    val pointB = Point(0, 0) reflectedOver (Point(100, 0), Point(0, -100))
    pointB should be(Point(100, -100))

    val pointC = Point(0, 50) reflectedOver (Point(50, 0), Point(50, 100))
    pointC should be(Point(100, 50))

    val pointD = Point(50, 0) reflectedOver (Point(0, 50), Point(100, 50))
    pointD should be(Point(50, 100))
  }

  "Calculating the gradient between two points" should "be safe and correct" in {
    Point(0, 0) gradientTo Point(10, 10) shouldBe 1
    Point(0, 0) gradientTo Point(0, 10) shouldBe Double.PositiveInfinity
    Point(0, 0) gradientTo Point(10, 0) shouldBe 0
  }
}
