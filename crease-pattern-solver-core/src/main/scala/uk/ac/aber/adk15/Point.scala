package uk.ac.aber.adk15

import scala.util.Try

/**
  * Represents a simple X, Y coordinate.
  *
  * @param x the position on the X axis
  * @param y the position on the Y axis
  *
  */
case class Point(x: Double, y: Double) {

  def canEqual(a: Any): Boolean = a.isInstanceOf[Point]

  override def equals(that: Any): Boolean = canEqual(that) && {
    val point = that.asInstanceOf[Point]
    point.x == x && point.y == y
  }

  override def toString = s"{$x, $y}"
}

object PointImplicits {

  /**
    * A class for infix operations based on a single point
    *
    * @param p the point to be operated on, as reference
    */
  implicit class PointArithmetic(p: Point) {

    /**
      * The equation
      *
      * d = (x - x1)(y2 - y1) * (y - y1)(x2 - x1)
      *
      * tells us on which side of a line a given point {x,y}
      * lies in relation to points A{x1,y1} and B{x2,y2} on the line.
      *
      * Source: http://math.stackexchange.com/questions/274712/calculate-on-which-side-of-a-straight-line-is-a-given-point-located
      *
      * @param start the first point on the line
      * @param end   the second point on the line
      * @return -1 or 1 denoting which side of the line it is on, 0 if the point lies on the line
      */
    def compareTo(start: Point, end: Point): Double = {
      ((p.x - start.x) * (end.y - start.y)) - ((p.y - start.y) * (end.x - start.x))
    }

    def dotProduct(a: Point, b: Point): Double = (p.x * b.x) + (p.y * b.y)

    def reflectedOver(start: Point, end: Point): Point = {
      val m = (end.y - start.y) / (end.x - start.x)
      val b = (m * start.x) - start.y

      val d = (p.x + (p.y - b) * m) / (1 + Math.pow(m, 2))
      val (x, y) = ((2 * d) - p.x, (2 * d * m) - p.y + (2 * b))

      Point(x, y)
    }
  }
}
