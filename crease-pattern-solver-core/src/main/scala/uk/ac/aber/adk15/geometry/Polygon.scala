package uk.ac.aber.adk15.geometry

import scala.annotation.tailrec

class Polygon(val points: Set[Point]) {

  private lazy val boundingBox = {
    val largestY  = (points maxBy (_.y)).y
    val smallestY = (points minBy (_.y)).y
    val largestX  = (points maxBy (_.x)).x
    val smallestX = (points minBy (_.x)).x

    Rectangle(Point(smallestX, largestY), Point(largestX, smallestY))
  }

  lazy val surfaceArea: Double = {
    // define a way of ordering points that works for our calculation
    implicit val pointOrder = Ordering by ((p: Point) => p.x + p.y)

    // run a sliding window over the list of points
    // we take three points at a time, calculating the area of that triangle
    // and summing them to find the total area
    (0.0 /: (points.toList.sorted sliding 3))((totalArea, triangle) =>
      totalArea + (triangle match {
        case a :: b :: c :: _ => areaOfTriangle(a, b, c)
        case _                => 0.0
      }))
  }

  def slice(line: Line): (Polygon, Polygon) = {
    val onLeft = points filter { p =>
      (p compareTo line) >= 0
    }

    val onRight = points filter { p =>
      (p compareTo line) <= 0
    }

    (new Polygon(onLeft), new Polygon(onRight))
  }

  def compareTo(line: Line): Int = {
    val comparisons = points map (_ compareTo line)

    if ((comparisons exists (_ > 0)) && (comparisons exists (_ < 0))) 0
    else if (comparisons exists (_ > 0)) 1
    else if (comparisons exists (_ < 0)) -1
    else 0
  }

  def overlaps(point: Point): Boolean = {
    (boundingBox overlaps point) && (isOnEdge(point) || findWindingNumber(point, points.toList) != 0)
  }

  def overlaps(polygon: Polygon): Boolean = polygon.points exists { point =>
    this overlaps point
  }

  def isOnEdge(point: Point): Boolean = {
    if (boundingBox overlaps point) {
      // todo: won't be properly ordered!
      val lines = (points sliding 2) map (pair => Line(pair.head, pair.last))

      lines exists { line =>
        val isInRange = line map { (a, b) =>
          val isInRangeX = (point.x >= math.min(a.x, b.x)) && (point.x <= math.max(a.x, b.x))
          val isInRangeY = (point.y >= math.min(a.y, b.y)) && (point.y <= math.max(a.y, b.y))

          isInRangeX && isInRangeY
        }

        val equalsEitherPoint = line map ((a, b) => point == a || point == b)

        val isOnSameLine = line map { (a, b) =>
          (point.x == a.x && point.x == b.x && a.x == b.x) ||
          (point.y == a.y && point.y == b.y && a.y == b.y) ||
          (point gradientTo a) == (point gradientTo b)
        }

        isInRange && (equalsEitherPoint || isOnSameLine)
      }
    } else false
  }

  def flatMap(f: Point => Point) = new Polygon(points map f)

  def contains(point: Point): Boolean = points contains point

  override def equals(obj: scala.Any): Boolean = obj match {
    case Polygon(otherPoints) => otherPoints == this.points
  }

  override def hashCode(): Int = points.hashCode()

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

  private def areaOfTriangle(a: Point, b: Point, c: Point) = {
    // http://www.mathopenref.com/coordtrianglearea.html
    def f(a: Point, b: Point, c: Point) = a.x * (b.y - c.y)
    math.abs(f(a, b, c) + f(b, c, a) + f(c, a, b)) / 2
  }
}

case class Rectangle(upperLeft: Point, lowerRight: Point)
    extends Polygon(
      Set(upperLeft,
          Point(upperLeft.x, lowerRight.y),
          lowerRight,
          Point(lowerRight.x, upperLeft.y))) {

  override def overlaps(point: Point): Boolean = {
    val inRangeX = point.x >= upperLeft.x && point.x <= lowerRight.x
    val inRangeY = point.y >= lowerRight.y && point.y <= upperLeft.y

    inRangeX && inRangeY
  }
}

object Polygon {
  def apply(points: Point*): Polygon                = new Polygon(points.toSet)
  def unapply(polygon: Polygon): Option[Set[Point]] = Some(polygon.points)
}
