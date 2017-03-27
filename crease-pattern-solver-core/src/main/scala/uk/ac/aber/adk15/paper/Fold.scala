package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.paper.Point.Helpers._

import scala.math.abs

/**
  * A class for representing to relationship between two points on a piece of paper.
  *
  * This line is directionless, meaning `start` and `end` can be swapped arbitrarily.
  *
  */
case class Fold(start: Point, end: Point, foldType: FoldType) {

  require(start != end, " Edge has no length, start and end cannot be the same.")

  /**
    * Check that two edges are equal, the start and edge nodes should be interchangeable
    * so this method is directionless when it compares if two edges are the same.
    *
    * @param that the object to check for equality
    * @return if they are the same paper edge
    */
  override def equals(that: Any): Boolean = that match {
    case Fold(otherStart: Point, otherEnd: Point, otherFoldType: FoldType) =>
      if ((start == otherStart || start == otherEnd) && (end == otherStart || end == otherEnd))
        return foldType == otherFoldType

      def onSameLine(a: Point, b: Point, c: Point) = abs(a gradientTo b) == abs(b gradientTo c)

      List(onSameLine(start, end, otherStart),
           onSameLine(start, end, otherEnd),
           otherFoldType == this.foldType) reduce (_ && _)

    case _ => false
  }

  override def hashCode(): Int = {
    var hash = 17

    hash = hash * 31 + (start.x + end.x).hashCode()
    hash = hash * 31 + (start.y + end.y).hashCode()
    hash = hash * 31 + foldType.hashCode()

    hash
  }

  override def toString = s"$start $foldType $end"

  def toSet = Set(start, end)

  def crease: Fold = foldType match {
    case PaperBoundary             => this
    case MountainFold | ValleyFold => Fold(start, end, CreasedFold)
    case CreasedFold               => throw new IllegalCreaseException(this)
  }
}

/**
  * This trait is the top level class for all possible types of fold.
  * For simplicity and easier programming we also consider hard edges to be
  * part of this.
  *
  * - Mountain / Valley fold: a fold indicator with a direction
  * - Creased fold: A mountain / valley fold that has been creased
  * - Paper boundary: This is a physical feature of the paper, ie, the *actual* edge of the paper
  *
  */
sealed trait FoldType

case object MountainFold extends FoldType {
  override def toString = "/\\"
}

case object ValleyFold extends FoldType {
  override def toString = "\\/"
}

case object CreasedFold extends FoldType {
  override def toString = "~~"
}

case object PaperBoundary extends FoldType {
  override def toString = "---"
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
  final implicit class PaperEdgeAssoc(val start: Point) extends AnyVal {
    @inline def /\(end: Point) = Fold(start, end, MountainFold)
    @inline def \/(end: Point) = Fold(start, end, ValleyFold)
    @inline def ~~(end: Point) = Fold(start, end, CreasedFold)
    @inline def --(end: Point) = Fold(start, end, PaperBoundary)
  }
}
