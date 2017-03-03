package uk.ac.aber.adk15

import scala.math.{abs, pow}

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

    def dotProduct(p2: Point): Double = (p.x * p2.x) + (p.y * p2.y)

    def -(p2: Point): Point = Point(p.x - p2.x, p.y - p2.y)

    def *(constant: Double): Point = constant * p

    /**
      * Returns the reflection the given point over the line defined by { start, end }.
      * If given a point on the line, will return the same point.
      *
      * given (x, y) and y = ax + c
      * d := (x + a(y - c) / (1 + a**2)
      *
      * x' = 2d - x
      * y' = 2da - y + 2c
      *
      * Source:
      * http://stackoverflow.com/questions/3306838/algorithm-for-reflecting-a-point-across-a-line
      *
      * @param start the beginning of the line
      * @param end the end of the line
      * @return the reflected point
      */
    def reflectedOver(start: Point, end: Point): Point = {
      val m = (end.y - start.y) / (end.x - start.x)
      val c = start.y - (m * start.x)

      // if the point is on the line, we need only return the point
      if (p.y == (m * p.x + c)) return p

      val d = abs((p.x + (p.y - c) * m) / (1 + pow(m, 2)))
      Point(2 * d - p.x, 2 * d * m - p.y + 2 * c)
    }
  }

  implicit class Num2PointArithmetic(constant: Double) {
    def *(point: Point): Point = Point(constant * point.x, constant * point.y)
  }
}
