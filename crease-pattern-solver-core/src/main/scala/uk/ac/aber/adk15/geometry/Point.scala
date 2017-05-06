package uk.ac.aber.adk15.geometry

import scala.Double.{NegativeInfinity, PositiveInfinity}
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

  /**
    * The equation
    *
    * d = (x - x1)(y2 - y1) * (y - y1)(x2 - x1)
    *
    * tells us on which side of a line a given point {x,y}
    * lies in relation to points A{x1,y1} and B{x2,y2} on the line.
    *
    * Source: https://math.stackexchange.com/q/274728
    *
    * @param line the line to compare to
    * @return -1 or 1 denoting which side of the line it is on, 0 if the point lies on the line
    */
  @inline final def compareTo(line: Line): Double = {
    val (a, b) = line.points
    ((x - a.x) * (b.y - a.y)) - ((y - a.y) * (b.x - a.x))
  }

  /**
    * calculates the dot product of the two points
    *
    * @param p2 the second point
    * @return the dot product
    */
  @inline final def dotProduct(p2: Point): Double = (x * p2.x) + (y * p2.y)

  /**
    * arbitrarily negates original point by second point
    *
    * @param p2 point to negate this point by
    * @return point A - point B
    */
  @inline final def -(p2: Point): Point = Point(x - p2.x, y - p2.y)

  /**
    * Calculates the gradient between two points.
    *
    * Note: the gradient is directional and will change the (+-) sign based
    * on which point is first
    *
    * @param p2 the second point
    * @return the gradient between p and p2
    */
  @inline final def gradientTo(p2: Point): Double = (p2.y - y, p2.x - x) match {
    case (a, 0)   => if (a > 0) PositiveInfinity else NegativeInfinity
    case (0, _)   => 0
    case (dy, dx) => dy / dx
  }

  /**
    * calculates the manhattan distance between two points
    *
    * @param p2 the second point
    * @return the manhattan distance
    */
  @inline final def distanceTo(p2: Point): Double = abs(x - p2.x) + abs(y - p2.y)

  /**
    * Returns the reflection the given point over the line defined by { start, end }.
    * If given a point on the line, will return the same point.
    *
    * given (x, y) and y = ax + c
    * d := (x + a(y - c) / (1 + a**2)
    *
    * x' =  2d - x
    * y' = 2da - y + 2c
    *
    * Source: http://stackoverflow.com/a/3307181
    *
    * @param line the line to reflect over
    * @return the reflected point
    */
  @inline
  def reflectedOver(line: Line): Point = {
    line match {
      case Line(a, b) =>
        if (a.x == b.x) return Point(2 * (x + a.x), y)
        if (a.y == b.y) return Point(x, 2 * (y + a.y))

        lazy val m = b gradientTo a
        lazy val c = a.y - m * a.x
        lazy val d = abs((x + (y - c) * m) / (1 + pow(m, 2)))

        Point(2 * d - x, 2 * d * m - y + 2 * c)
    }
  }

  /**
    * Applies a constant to the point,
    *
    * @param constant the constant to multiply the point by
    * @return (x * constant, y * constant)
    */
  @inline final def applyConstant(constant: Double): Point = Point(constant * x, constant * y)

  override def equals(other: Any): Boolean = other match {
    case Point(otherX, otherY) => x == otherX && y == otherY
    case _                     => false
  }

  override def toString = f"{ $x% 6.1f, $y% 6.1f }"

}

/**
  * A specialised ordering for ordering points by their distance from origin (0, 0)
  */
object DistanceFromOriginPointOrdering extends Ordering[Point] {
  override def compare(a: Point, b: Point): Int = math.signum((a.x + a.y) + (b.x + b.y)).toInt
}
