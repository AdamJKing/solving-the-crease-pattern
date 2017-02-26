package uk.ac.aber.adk15

import scalax.collection.GraphEdge.{NodeProduct, UnDiEdge}

/**
  * A class for representing to relationship between two points on a piece of paper.
  *
  * This line is directionless, meaning `start` and `end` can be swapped arbitrarily.
  *
  * @param start the starting point of the line
  * @param end   the finishing point of the line
  */
case class PaperEdge[+N](start: N, end: N, edgeType: PaperEdgeType)
  extends UnDiEdge[N](NodeProduct(start, end)) {
  require(start != end, "An edge cannot have zero length (start point and end point are the same!)")
}

abstract class PaperEdgeType()

case class MountainFold() extends PaperEdgeType

case class ValleyFold() extends PaperEdgeType

case class HardEdge() extends PaperEdgeType
