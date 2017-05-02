package uk.ac.aber.adk15.geometry

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

  def combineWith(rectangle: Rectangle): Rectangle = {
    rectangle match {
      case Rectangle(otherUpperLeft, otherLowerRight) =>
        val newUpperLeft = Point(
          math.min(upperLeft.x, otherUpperLeft.x),
          math.max(upperLeft.y, otherUpperLeft.y)
        )

        val newLowerRight = Point(
          math.max(lowerRight.x, otherLowerRight.x),
          math.min(lowerRight.y, otherLowerRight.y)
        )

        Rectangle(newUpperLeft, newLowerRight)
    }
  }
}

object RectangleSizeOrdering extends Ordering[Rectangle] {
  override def compare(a: Rectangle, b: Rectangle): Int = a.surfaceArea compareTo b.surfaceArea
}
