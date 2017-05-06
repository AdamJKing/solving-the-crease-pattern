package uk.ac.aber.adk15.geometry

/**
  * A specialisation of a [[Polygon]] that only has four points.
  * Represents a rectangle defined by the point on the upper left and the lower right
  *
  * ie.
  * upper left
  *      O----------------------------.
  *      |                            |
  *      |                            |
  *      |                            |
  *      |                            |
  *      '----------------------------O
  *                              lower right
  *
  * @param upperLeft the point in the upper left corner
  * @param lowerRight the point in the lower right corner
  */
case class Rectangle(upperLeft: Point, lowerRight: Point)
    extends Polygon(
      Set(upperLeft,
          Point(upperLeft.x, lowerRight.y),
          lowerRight,
          Point(lowerRight.x, upperLeft.y))) {

  /**
    * Combines two rectangles together to form a new larger rectangle.
    * The new rectangle will use the highest x and y values as well as
    * the lowest x and y values.
    *
    * With:
    *   .----------.
    *   |          |
    *   |       .--|-------.
    *   '-------|--'       |
    *           |          |
    *           '----------'
    * Becomes:
    *   .------------------.
    *   |                  |
    *   |                  |
    *   |                  |
    *   |                  |
    *   '------------------'
    *
    * Useful for combining multiple bounding-boxes
    *
    * @param rectangle the rectangle to combine with
    * @return
    */
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

  /**
    * Tests if the given point is within the rectangle.
    *
    * @param point the point to test
    * @return true if the point is in the rectangle, false if not
    */
  override def overlaps(point: Point): Boolean = {
    val inRangeX = point.x >= upperLeft.x && point.x <= lowerRight.x
    val inRangeY = point.y >= lowerRight.y && point.y <= upperLeft.y

    inRangeX && inRangeY
  }

}

/**
  * An ordering of rectangles with respect to their size
  */
object RectangleSizeOrdering extends Ordering[Rectangle] {
  override def compare(a: Rectangle, b: Rectangle): Int = a.surfaceArea compareTo b.surfaceArea
}
