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

  override def equals(that: Any): Boolean =
    canEqual(that) && creasePattern.equals(that.asInstanceOf[this.type])

  override def toString: String = creasePattern.toOuterEdges.toString

  def canEqual(that: Any): Boolean = that.isInstanceOf[this.type]

  def fold(edge: PaperEdge[Point]): PaperModel = {

    val creaseLine = getCrease(edge).get
    val isOnLeft   = (_: PaperEdge[Point]) map (_ compareTo (edge._1, edge._2)) map (_ >= 0) reduce (_ && _)

    val edges = creasePattern.toOuterEdges.map(
      e =>
        if (e == creaseLine) PaperEdge(e.start, e.end, e.foldType)
        else if (isOnLeft(e)) rotateEdge(e)
        else e)

    PaperModel(Graph.from(edges.toOuterNodes, edges))
  }

  protected def getCrease(crease: PaperEdge[Point]): Try[PaperEdge[Point]] = {
    creasePattern.toOuterEdges find (_ == crease) match {
      case None =>
        Failure(
          new IllegalArgumentException(
            "Crease was not found in crease pattern, are you breaking the rules?"))

      case Some(PaperEdge(_, _, CreasedFold)) =>
        Failure(
          new IllegalArgumentException("Anticipated crease was already folded in crease pattern."))
      case Some(edge: PaperEdge[Point]) => Success(edge)
    }
  }

  private def rotateEdge(edge: PaperEdge[Point]) = {
    PaperEdge(
      edge.start reflectedOver (edge._1, edge._2),
      edge.end reflectedOver (edge._1, edge._2),
      edge.foldType match {
        // when we fold a piece of paper, all unfolded creases receive the inverse assignment
        case MountainFold  => ValleyFold
        case ValleyFold    => MountainFold
        case CreasedFold   => CreasedFold
        case PaperBoundary => PaperBoundary
      }
    )
  }
}

/**
  * Companion object for a paper model.
  */
object PaperModel {

  /**
    * Constructs a new graph using the supplied folds.
    *
    * @return a new paper model
    */
  def apply(creasePattern: Graph[Point, PaperEdge]) =
    new PaperModel(creasePattern)
}
