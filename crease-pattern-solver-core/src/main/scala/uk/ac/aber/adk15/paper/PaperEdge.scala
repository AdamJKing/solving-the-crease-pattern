package uk.ac.aber.adk15.paper

import scalax.collection.GraphEdge.{NodeProduct, UnDiEdge}
import scalax.collection.GraphPredef.OuterEdge

/**
  * A class for representing to relationship between two points on a piece of paper.
  *
  * This line is directionless, meaning `start` and `end` can be swapped arbitrarily.
  *
  * @param start the starting point of the line
  * @param end   the finishing point of the line
  */
abstract class PaperEdge[+N](start: N, end: N)
  extends UnDiEdge[N](NodeProduct(start, end))
    with OuterEdge[N, PaperEdge] {

  override def isValidCustom: Boolean = super.isValidCustom && start != end

  override def isValidCustomExceptionMessage: String =
    super.isValidCustomExceptionMessage ++ "Crease has no length, start point and end point cannot be the same"
}

case class FoldedPaperEdge[+N](start: N, end: N) extends PaperEdge(start, end)

case class UnfoldedPaperEdge[+N](start: N, end: N, foldType: FoldType) extends PaperEdge(start, end) {

  def crease: FoldedPaperEdge[N] = FoldedPaperEdge[N](start, end)
}

/**
  * TODO: Reword
  * An abstract class for folds or edges, represents every type of connection you
  * would reasonably expect in an Origami model.
  */
abstract class FoldType()

/**
  * Mountain type fold (/\)
  */
case class MountainFoldType() extends FoldType()

/**
  * Valley type fold (\/)
  */
case class ValleyFoldType() extends FoldType()

