package uk.ac.aber.adk15.paper

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

  override def equals(that: Any): Boolean = canEqual(that) && {
    val point = that.asInstanceOf[Point]
    point.x == x && point.y == y
  }

  override def toString = s"{$x, $y}"
}

object Point {
  object Helpers {

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
        * Source: http://math.stackexchange.com/q/274728
        *
        * @param start the first point on the line
        * @param end   the second point on the line
        * @return -1 or 1 denoting which side of the line it is on, 0 if the point lies on the line
        */
      @inline final def compareTo(start: Point, end: Point): Double = {
        ((p.x - start.x) * (end.y - start.y)) - ((p.y - start.y) * (end.x - start.x))
      }

      @inline final def dotProduct(p2: Point): Double = (p.x * p2.x) + (p.y * p2.y)

      @inline final def -(p2: Point): Point        = Point(p.x - p2.x, p.y - p2.y)
      @inline final def *(constant: Double): Point = constant * p
      @inline final def gradientTo(p2: Point): Double = (p2.y - p.y, p2.x - p.x) match {
        case (a, 0)   => if (a > 0) PositiveInfinity else NegativeInfinity
        case (0, _)   => 0
        case (dy, dx) => dy / dx
      }

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
        * @param start the beginning of the line
        * @param end the end of the line
        * @return the reflected point
        */
      final def reflectedOver(start: Point, end: Point): Point = {
        if (start.x == end.x) return Point(2 * (p.x + start.x), p.y)
        if (start.y == end.y) return Point(p.x, 2 * (p.y + start.y))

        lazy val m = end gradientTo start
        lazy val c = start.y - (m * start.x)
        lazy val d = abs((p.x + (p.y - c) * m) / (1 + pow(m, 2)))

        Point(2 * d - p.x, 2 * d * m - p.y + 2 * c)
      }

    }

    /**
      * This implicit class is for executing numerical operations on points.
      *
      * @param constant the number to apply to the vector (point)
      */
    implicit class Num2PointArithmetic(constant: Double) {
      @inline final def *(point: Point): Point = Point(constant * point.x, constant * point.y)
    }
  }
}
