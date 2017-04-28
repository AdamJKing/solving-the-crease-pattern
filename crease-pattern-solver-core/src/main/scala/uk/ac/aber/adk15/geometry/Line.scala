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

  def map[T](f: (Point, Point) => T): T = f(a, b)
  def flatMap(f: Point => Point): Line  = Line(f(a), f(b))
}
