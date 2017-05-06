package uk.ac.aber.adk15.geometry

import scala.annotation.tailrec

/**
  * A representation of a polygon as defined by a series of points.
  *
  * @param points the points that make-up the polygon
  */
class Polygon(val points: Set[Point]) {
  require(points.size > 2, "A shape must have more than two points")

  /**
    * The bounding box of this polygon
    */
  lazy val boundingBox: Rectangle = {
    val largestY  = (points maxBy (_.y)).y
    val smallestY = (points minBy (_.y)).y
    val largestX  = (points maxBy (_.x)).x
    val smallestX = (points minBy (_.x)).x

    Rectangle(Point(smallestX, largestY), Point(largestX, smallestY))
  }

  /**
    * The surface area of the polygon
    */
  lazy val surfaceArea: Double = {
    // define a way of ordering points that works for our calculation
    implicit val pointOrder = DistanceFromOriginPointOrdering

    // run a sliding window over the list of points
    // we take three points at a time, calculating the area of that triangle
    // and summing them to find the total area
    (0.0 /: (points.toList.sorted sliding 3))((totalArea, triangle) =>
      totalArea + (triangle match {
        case a :: b :: c :: _ => areaOfTriangle(a, b, c)
        case _                => 0.0
      }))
  }

  /**
    * Slice a polygon into two new polygons, across a given line
    *
    * @param line the line to split using
    * @return two new polygons
    */
  def slice(line: Line): (Polygon, Polygon) = {
    val onLeft = points filter { p =>
      (p compareTo line) >= 0
    }

    val onRight = points filter { p =>
      (p compareTo line) <= 0
    }

    (new Polygon(onLeft), new Polygon(onRight))
  }

  /**
    * Compares the position of a shape to a given line.
    *
    * Shape is on the 'left' of the line -1
    * Shape is on the 'centre' of the line 0
    * Shape in on the 'right' of the line 1
    *
    * @param line the line to compare to
    * @return -1, 0, or 1 depending on the relative position
    */
  def compareTo(line: Line): Int = {
    val comparisons = points map (_ compareTo line)

    if ((comparisons exists (_ > 0)) && (comparisons exists (_ < 0))) 0
    else if (comparisons exists (_ > 0)) 1
    else if (comparisons exists (_ < 0)) -1
    else 0
  }

  /**
    * Tests if the given point is covered by the polygon.
    * Uses a winding number calculation.
    *
    * @param point the point to test
    * @return if the point is covered by the polygon
    */
  def overlaps(point: Point): Boolean = {
    (boundingBox overlaps point) && (isOnEdge(point) || findWindingNumber(point, points.toList) != 0)
  }

  /**
    * Tests if this polygon overlaps the other polygon.
    *
    * @example these two polygons would be considered overlapping
    *
    *     .---------.
    *   .'          |
    *   |     .-----|---------.
    *   |      `.   |       .'
    *   '--------`.-'     .'
    *              `.   .'
    *                `.'
    *
    * @param polygon tests
    * @return
    */
  def overlaps(polygon: Polygon): Boolean = polygon.points exists (this overlaps _)

  /**
    * Tests if a given point is on the edge of the polygon
    *
    * @param point the point to test
    * @return if the point is exclusively on the edge of the polygon
    */
  def isOnEdge(point: Point): Boolean = {
    if (boundingBox overlaps point) {
      // this is bug prone as the points are not guaranteed to be in order
      // future versions of this application would ideally remove this entirely
      val lines = (points sliding 2) map (pair => Line(pair.head, pair.last))

      // check if there is a line that intersects our point
      lines exists { line =>
        // firstly we check the range of the line to ensure they are
        // capable of intersection
        val isInRange = line map { (a, b) =>
          val isInRangeX = (point.x >= math.min(a.x, b.x)) && (point.x <= math.max(a.x, b.x))
          val isInRangeY = (point.y >= math.min(a.y, b.y)) && (point.y <= math.max(a.y, b.y))

          isInRangeX && isInRangeY
        }

        // if the point we're testing is actually a point on the polygon
        // then it definitely is on the edge
        val equalsEitherPoint = line map ((a, b) => point == a || point == b)

        // the final check is to check if the point is on the same line as
        // the other points
        val isOnSameLine = line map { (a, b) =>
          (point.x == a.x && point.x == b.x && a.x == b.x) ||
          (point.y == a.y && point.y == b.y && a.y == b.y) ||
          (point gradientTo a) == (point gradientTo b)
        }

        isInRange && (equalsEitherPoint || isOnSameLine)
      }

      // not in the bounding box
    } else false
  }

  /**
    * Apply operator `f` to every point in the polygon,
    * creating a new [[Polygon]]
    *
    * @param f the operator to apply to every point
    * @return a new polygon with `f` applied to every point
    */
  def flatMap(f: Point => Point) = new Polygon(points map f)

  /**
    * Checks if the point is one of the defining points on the polygon
    *
    * @param point the point to check for
    * @return if the point is one of the defining points on the polygon
    */
  def contains(point: Point): Boolean = points contains point

  override def equals(obj: scala.Any): Boolean = obj match {
    case Polygon(otherPoints) => otherPoints == this.points
  }

  override def hashCode(): Int = points.hashCode()

  /**
    * Calculates the winding number of the point with respect to the given polygon.
    * This reveals whether or not the point is inside of or outside of the polygon.
    *
    * source: http://geomalgorithms.com/a03-_inclusion.html
    *
    * @param point the point to test
    * @param polygon the polygon to test against
    * @param startWn an optional initial value for the winding number (used recursively)
    *
    * @return if the point is inside of or outside of the polygon
    */
  @tailrec
  private def findWindingNumber(point: Point, polygon: List[Point], startWn: Int = 0): Int = {
    polygon match {
      case a :: b :: rest =>
        val position = point compareTo Line(a, b)

        if (a.y <= point.y && b.y > point.y && position > 0)
          findWindingNumber(point, b :: rest, startWn + 1)
        else if (b.y <= point.y && position < 0)
          findWindingNumber(point, b :: rest, startWn - 1)
        else
          findWindingNumber(point, b :: rest, startWn)

      case _ => startWn
    }
  }

  /**
    * Calculates the area of a triangle
    *
    * source: http://www.mathopenref.com/coordtrianglearea.html
    *
    * @param a a single point on the triangle
    * @param b a single point on the triangle
    * @param c a single point on the triangle
    *
    * @return the area of the triangle
    */
  private def areaOfTriangle(a: Point, b: Point, c: Point) = {
    def f(a: Point, b: Point, c: Point) = a.x * (b.y - c.y)
    math.abs(f(a, b, c) + f(b, c, a) + f(c, a, b)) / 2
  }
}

/**
  * Companion object for a [[Polygon]]
  */
object Polygon {
  def apply(points: Point*): Polygon                = new Polygon(points.toSet)
  def unapply(polygon: Polygon): Option[Set[Point]] = Some(polygon.points)
}
