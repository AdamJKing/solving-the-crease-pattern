package uk.ac.aber.adk15

/**
  * A class for representing to relationship between two points on a piece of paper.
  *
  * This line is directionless, meaning `start` and `end` can be swapped arbitrarily.
  *
  * @param start the starting point of the line
  * @param end the finishing point of the line
  */
abstract class PaperEdge(start: Point, end: Point) {
  require(start != end, "An edge cannot have zero length (start point and end point are the same!)")
}

case class MountainFold(start: Point, end: Point) extends PaperEdge(start, end)

case class ValleyFold(start: Point, end: Point) extends PaperEdge(start, end)

case class HardEdge(start: Point, end: Point) extends PaperEdge(start, end)
