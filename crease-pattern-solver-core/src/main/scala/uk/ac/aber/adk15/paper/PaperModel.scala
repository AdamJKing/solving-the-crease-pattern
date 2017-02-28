package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.Point
import uk.ac.aber.adk15.PointImplicits._

import scala.util.{Failure, Success, Try}
import scalax.collection.immutable.Graph

/**
  * A simple representation of a piece of paper. It
  * uses a graph and a series of points to describe
  * the internal fold structure.
  *
  * @param creasePattern a graph describing the series of folds or edges that exist in the model
  *
  */
class PaperModel(creasePattern: Graph[Point, PaperEdge]) {
  def fold(edge: UnfoldedPaperEdge[Point]): PaperModel = {

    val creaseLine = getCrease(edge).get
    val isOnLeft = (_: PaperEdge[Point]) map (_ compareTo(edge._1, edge._2)) map (_ > 0) reduce (_ && _)

    val (toLeave, toFold) = (creasePattern -! creaseLine).toOuterEdges partition isOnLeft
    val folded = toFold map {
      case UnfoldedPaperEdge(start, end, foldType) =>
        UnfoldedPaperEdge(start reflectedOver(edge._1, edge._2), end reflectedOver(edge._1, edge._2), foldType match {
            // when we fold a piece of paper, all unfolded creases receive the inverse assignment
          case MountainFoldType() => ValleyFoldType()
          case ValleyFoldType() => MountainFoldType()
        })
      case FoldedPaperEdge(start, end) =>
        FoldedPaperEdge(start reflectedOver(edge._1, edge._2), end reflectedOver(edge._1, edge._2))
    }

    // recollect modified nodes into final, new, crease pattern
    PaperModel(Graph.from(Nil, toLeave) ++ Graph.from(Nil, folded) + creaseLine.crease)
  }

  protected def getCrease(crease: PaperEdge[Point]): Try[UnfoldedPaperEdge[Point]] = creasePattern find crease match {
    case None =>
      Failure(new IllegalArgumentException("Crease was not found in crease pattern, are you breaking the rules?"))

    case Some(_: FoldedPaperEdge[Point]) =>
      Failure(new IllegalArgumentException("Cannot fold, it has already been folded."))

    case Some(unfoldedPaperEdge: UnfoldedPaperEdge[Point]) => Success(unfoldedPaperEdge)
  }
}

/**
  * Companion object for a paper model.
  */
object PaperModel {
  /**
    * Constructs a new graph using the supplied folds.
    *
    * @param edges a variable length list of folds to add to the model's pattern
    * @return a new paper model
    */
  def apply(edges: PaperEdge[Point]*): PaperModel = new PaperModel(Graph.from(Nil, edges))

  def apply(creasePattern: Graph[Point, PaperEdge]) = new PaperModel(creasePattern)
}
