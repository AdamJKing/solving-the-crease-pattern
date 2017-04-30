package uk.ac.aber.adk15.geometry

case class Line(a: Point, b: Point) {
  def points: (Point, Point) = (a, b)

  def alignsWith(otherLine: Line): Boolean = {
    otherLine map { (otherA, otherB) =>
      (this == otherLine) || {
        def onSameLine(a: Point, b: Point, c: Point) = (a gradientTo b) == (b gradientTo c)

        if (a == otherA || b == otherA) onSameLine(a, b, otherB)
        else if (b == otherB || b == otherB) onSameLine(a, b, otherA)
        else onSameLine(a, b, otherA) && onSameLine(a, b, otherB)
      }
    }
  }

  /**
    * Determines if the two lines are parallel.
    *
    * source: https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection
    *
    * @param otherLine the line to compare to this one
    * @return true if parallel, false if not
    */
  def isParallelTo(otherLine: Line): Boolean = {
    val (c, d) = otherLine.points

    !(a == c || a == d || b == c || b == d) && (a.x - b.x) * (c.y - d.y) - (a.y - b.y) * (c.x - d.x) == 0
  }

  /**
    * Determines, if one exists, the intersecting point between two lines.
    * This solution may be prone to failure if the intersection is distant,
    * and as such outputs of the function should be checked against the expected
    * range.
    *
    * source: https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection
    *
    * @param otherLine the line to compare to this one
    * @return the intersecting point, if one exists
    */
  def intersectWith(otherLine: Line): Option[Point] = {
    if (this isParallelTo otherLine) None
    else {
      val (c, d) = otherLine.points

      // if they share a point, then just return that point
      // since a shared point ''is'' the intersection
      if (a == c || b == c) return Some(c)
      if (a == d || b == d) return Some(d)

      // calculate the determinants

      val px = determinant(
        determinant(a.x, a.y, b.x, b.y),
        determinant(a.x, 1, b.x, 1),
        determinant(c.x, c.y, d.x, d.y),
        determinant(c.x, 1, d.x, 1)
      ) / determinant(
        determinant(a.x, 1, b.x, 1),
        determinant(a.y, 1, b.y, 1),
        determinant(c.x, 1, d.x, 1),
        determinant(c.y, 1, d.y, 1)
      )

      val py = determinant(
        determinant(a.x, a.y, b.x, b.y),
        determinant(a.y, 1, b.y, 1),
        determinant(c.x, c.y, d.x, d.y),
        determinant(c.y, 1, d.y, 1)
      ) / determinant(
        determinant(a.x, 1, b.x, 1),
        determinant(a.y, 1, b.y, 1),
        determinant(c.x, 1, d.x, 1),
        determinant(c.y, 1, d.y, 1)
      )

      Some(Point(px, py))
    }
  }

  def contains(point: Point): Boolean = a == point || b == point

  def map[T](f: (Point, Point) => T): T  = f(a, b)
  def mapValues(f: Point => Point): Line = Line(f(a), f(b))

  override def equals(other: Any): Boolean = {
    other match {
      case Line(otherA, otherB) =>
        (a == otherA || a == otherB) && (b == otherA || b == otherB)
      case _ => false
    }
  }

  override def hashCode(): Int = {
    var hash = 17
    val m    = a gradientTo b

    hash = hash * 31 + math.abs(m).hashCode()
    hash = hash * 31 + (a.y - (m * a.x)).hashCode()

    hash
  }

  private def determinant(i: Double, j: Double, k: Double, l: Double) = (i * l) - (j * k)
}
