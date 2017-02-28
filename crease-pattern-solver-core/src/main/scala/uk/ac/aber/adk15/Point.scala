package uk.ac.aber.adk15

/**
  * Represents a simple X, Y coordinate.
  *
  * @param x the position on the X axis
  * @param y the position on the Y axis
  *
  */
final class Point(val x: Int, val y: Int) {

  def canEqual(a: Any): Boolean = a.isInstanceOf[Point]

  override def equals(that: Any): Boolean =
    that match {
      case that: Point => that.canEqual(this) && that.x == this.x && that.y == this.y
    }

  override def toString: String = s"{$x, $y}"
}

/** Companion object for a Point */
object Point {
  def apply(x: Int, y: Int): Point = new Point(x, y)

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
    def compareTo(start: Point, end: Point): Int = {
      ((p.x - start.x) * (end.y - start.y)) - ((p.y - start.y) * (end.x - start.x))
    }

    // http://stackoverflow.com/questions/3306838/algorithm-for-reflecting-a-point-across-a-line
    def reflectedOver(start: Point, end: Point): Point = {
      val m = (start.y - end.y) / (start.x - end.x)
      val c = (-m * start.x) + start.y

      val (x, y): (Int, Int) = (p.x, p.y)
      val d = (x + (y - c) * m) / (1 + m ^ 2)
      Point((2 * d) - x, (2 * d * m) - y + (2 * c))
    }
  }

}
