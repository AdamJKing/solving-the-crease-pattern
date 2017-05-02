package uk.ac.aber.adk15.paper.fold

import uk.ac.aber.adk15.geometry.{Line, Point}

/**
  * A class for representing to relationship between two points on a piece of paper.
  *
  * This line is directionless, meaning `start` and `end` can be swapped arbitrarily.
  *
  */
case class Fold(line: Line, foldType: FoldType) {

  /**
    * Check that two edges are equal, the start and edge nodes should be interchangeable
    * so this method is directionless when it compares if two edges are the same.
    *
    * @param that the object to check for equality
    * @return if they are the same paper edge
    */
  override def equals(that: Any): Boolean = that match {
    case Fold(otherLine: Line, otherFoldType: FoldType) =>
      (line == otherLine) && (foldType == otherFoldType)

    case _ => false
  }

  override def hashCode(): Int = {
    var hash = 17

    hash = hash * 31 + line.hashCode()
    hash = hash * 31 + foldType.hashCode()

    hash
  }

  override def toString = s"${line.a} $foldType ${line.b}"

  def points = Set(line.a, line.b)

  def crease: Fold = foldType match {
    case PaperBoundary | CreasedFold => this
    case MountainFold | ValleyFold   => Fold(line, CreasedFold)
  }

  def contains(point: Point): Boolean  = points contains point
  def flatMap(f: Point => Point): Fold = Fold(line mapValues f, foldType)
}

/**
  * Contains implicit definitions of operations as
  * shorthand for describing folds in a crease pattern.
  *
  * ie. Point(0, 0) /\ Point(10, 10) would be a mountain
  * fold between two points.
  *
  */
object PaperEdgeHelpers {
  final implicit class FoldOps(val start: Point) extends AnyVal {
    @inline def /\(end: Point) = Fold(Line(start, end), MountainFold)
    @inline def \/(end: Point) = Fold(Line(start, end), ValleyFold)
    @inline def ~~(end: Point) = Fold(Line(start, end), CreasedFold)
    @inline def --(end: Point) = Fold(Line(start, end), PaperBoundary)
  }
}
