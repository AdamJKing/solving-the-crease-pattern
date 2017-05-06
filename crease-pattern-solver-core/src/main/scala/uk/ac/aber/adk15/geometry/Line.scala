package uk.ac.aber.adk15.geometry

/**
  * Represents a line between two points:
  *
  *   A ----------- B
  *
  * Points A and B are arbitrary identifiers and are entirely interchangeable.
  * Lines AB and BA will both be considered equal, ie. lines have no direction.
  *
  * For mathematical purposes this can be considered to be a line that is
  * defined by two points.
  *
  * @param a a point on the line
  * @param b another point on the line
  */
case class Line(a: Point, b: Point) {

  /**
    * converts this line to it's two part representation.
    *
    * @return a [[Tuple2]] of the points
    */
  def points: (Point, Point) = (a, b)

  /**
    * Determines if the lines align with each other.
    *
    * @example line AB and CD ''do not'' align
    *
    *               A          C
    *               |          |
    *               |          |
    *               |          |
    *               B          D
    *
    * @example line AB and BC ''do'' align
    *
    *          A.
    *            `-.
    *               `-.
    *                  `B.
    *                     `-.
    *                        `-.
    *                           `C
    *
    * @example line AB and CD ''do'' align
    *
    *          A.
    *            `-.
    *               `B
    *                   D.
    *                     `-.
    *                        `C
    *
    * Works by comparing the gradients and y-crossings of the two lines.
    * If they are the same, the lines align.
    *
    * @param otherLine the other line to compare this line to
    * @return if the lines align or not
    */
  def alignsWith(otherLine: Line): Boolean = {
    otherLine map { (otherA, otherB) =>
      (this == otherLine) || {
        def onSameLine(a: Point, b: Point, c: Point) = (a gradientTo b) == (b gradientTo c)

        // in some cases the points are equal
        // this can cause `onSameLine` to fail
        // as the gradients will be opposite in sign (+-)
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
    // if the two lines are parallel there can be no intersection
    if (this isParallelTo otherLine) None
    else {
      val (c, d) = otherLine.points

      // if they share a point, then just return that point
      // since a shared point ''is'' the intersection
      if (a == c || b == c) return Some(c)
      if (a == d || b == d) return Some(d)

      // calculate the determinants of the position matrices
      // this code could potentially belong in a 'Matrix' class

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

  /**
    * Maps the points in this line using the supplied function.
    *
    * @param f the function to pass the points to
    * @tparam T the type that the function produces
    * @return the application of `f` to the points on this line
    */
  def map[T](f: (Point, Point) => T): T = f(a, b)

  /**
    * Applies f to each of the points and returns a new line with these
    * values. Ideal for functions that modify a line.
    *
    * @param f the function to apply to each point
    * @return a new line with the modified points
    */
  def flatMap(f: Point => Point): Line = Line(f(a), f(b))

  /**
    * Compares two lines for equality.
    * Two lines are equal regardless of direction of points,
    * ie. AB == BA = true
    *
    * Two lines will only equal each other if the points are the same.
    * To check if two lines are effectively in line with each other,
    * use [[alignsWith()]]
    *
    * @param other the other line to compare to
    * @return if the other line equals this one
    */
  override def equals(other: Any): Boolean = {
    other match {
      case Line(otherA, otherB) =>
        (a == otherA || a == otherB) && (b == otherA || b == otherB)

      case _ => false
    }
  }

  /**
    * calculates the hash of gradient between the two lines,
    * as well as the y-crossing.
    *
    * This means two lines that have the same equation will have the same hash
    *
    * @return
    */
  override def hashCode(): Int = {
    var hash = 17
    val m    = a gradientTo b

    hash = hash * 31 + math.abs(m).hashCode()
    hash = hash * 31 + (a.y - (m * a.x)).hashCode()

    hash
  }

  /**
    * Calculate the determinant of a 2 x 2 matrix, ie.
    *
    *                 .--   --.
    *                 | A,  B |
    *                 |       |
    *                 | C,  D |
    *                 '--   --'
    *
    * @param i the value in A
    * @param j the value in B
    * @param k the value in C
    * @param l the value in D
    *
    * @return the determinant of the matrix [i, j], [k, l]
    */
  private def determinant(i: Double, j: Double, k: Double, l: Double) = (i * l) - (j * k)
}
