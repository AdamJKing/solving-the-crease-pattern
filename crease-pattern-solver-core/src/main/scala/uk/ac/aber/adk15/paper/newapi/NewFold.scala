package uk.ac.aber.adk15.paper.newapi

import uk.ac.aber.adk15.geometry.{Line, Point}
import uk.ac.aber.adk15.paper._

/**
  * A class for representing to relationship between two points on a piece of paper.
  *
  * This line is directionless, meaning `start` and `end` can be swapped arbitrarily.
  *
  */
case class NewFold(line: Line, foldType: FoldType) {

  /**
    * Check that two edges are equal, the start and edge nodes should be interchangeable
    * so this method is directionless when it compares if two edges are the same.
    *
    * @param that the object to check for equality
    * @return if they are the same paper edge
    */
  override def equals(that: Any): Boolean = that match {
    case NewFold(otherLine: Line, otherFoldType: FoldType) =>
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

  def crease: NewFold = foldType match {
    case PaperBoundary | CreasedFold => this
    case MountainFold | ValleyFold   => NewFold(line, CreasedFold)
  }

  def contains(point: Point): Boolean     = points contains point
  def flatMap(f: Point => Point): NewFold = NewFold(line mapValues f, foldType)
}
