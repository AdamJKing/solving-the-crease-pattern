package uk.ac.aber.adk15.paper

import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.Point
import uk.ac.aber.adk15.PointHelpers._
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._

import scalax.collection.immutable.Graph

/**
  * A simple representation of a piece of paper. It
  * uses a graph and a series of points to describe
  * the internal fold structure.
  *
  * @param creasePattern a graph describing the series of folds or edges that exist in the model
  */
class PaperModel(val creasePattern: Graph[Point, PaperEdge]) {

  private val logger = Logger[this.type]

  override def equals(that: Any): Boolean =
    canEqual(that) && creasePattern == that.asInstanceOf[PaperModel].creasePattern

  override def toString: String = creasePattern.toOuterEdges.toString

  def canEqual(that: Any): Boolean = that.isInstanceOf[PaperModel]

  /**
    * Method for affecting a fold on the paper model. The edge supplied to this function
    * *must* be a legal fold from the paper's existing crease pattern.
    *
    * @note Attempting to fold a crease that a) doesn't exist, b) has already been folded,
    * or c) doesn't match the crease pattern copy will result in an `IllegalArgumentException`.
    *
    * @example paperModel.fold Point(0, 0) /\ Point(100, 00)
    *
    * @param edge the edge to fold, will be come a `CreasedEdge`
    * @throws IllegalArgumentException if the supplied fold is invalid
    * @return the folded paper model
    */
  def fold(edge: PaperEdge[Point]): PaperModel = {
    logger debug s"Preparing to commence a fold for $edge"

    validateCrease(edge)

    def isOnLeft(e: PaperEdge[Point]) = (e map (_ compareTo (edge.start, edge.end))).sum > 0

    val edges = creasePattern.toOuterEdges map
        (e => {
         if (e == edge) {
           logger debug "found the same crease, folding it"
           PaperEdge(e.start, e.end, CreasedFold)

         } else if (isOnLeft(e)) {
           logger debug s"found leftward edge, rotating!, $e is on the left."
           rotateEdge(e, edge)

         } else {
           logger debug "found rightward crease, nothing to do here"
           e
         }
       })

    PaperModel(Graph.from(edges.toOuterNodes, edges))
  }

  /**
    * Validate the crease to make sure we're not doing something physically impossible / invalid.
    *
    * @param crease the crease to check
    */
  protected def validateCrease(crease: PaperEdge[Point]): Unit = {
    creasePattern.toOuterEdges find (_ == crease) match {
      case None =>
        throw new IllegalArgumentException(
          "Crease was not found in crease pattern, are you breaking the rules?")

      case Some(PaperEdge(_, _, CreasedFold)) =>
        throw new IllegalArgumentException(
          "Anticipated crease was already folded in crease pattern.")

      case Some(PaperEdge(_, _, _)) => // okay!
    }
  }

  /**
    * Helper function to rotate an edge around a given axis.
    *
    * @param edge the edge to be reflected
    * @param axis the axis to reflect it round
    * @return the new, rotated edge
    */
  private def rotateEdge(edge: PaperEdge[Point], axis: PaperEdge[Point]) = {
    PaperEdge(
      edge.start reflectedOver (axis._1, axis._2),
      edge.end reflectedOver (axis._1, axis._2),
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

/**
  * Object containing useful constants with respect to the paper model.
  *
  */
object PaperModelConstants {

  /**
    * Represents a blank piece of paper with no folds.
    * This blank paper is a square with four corners.
    *
    * 0,0        1,0
    * +----------+
    * |          |
    * |          |
    * |          |
    * |          |
    * +----------+
    * 0,1        1,1
    *
    */
  val BlankPaper: PaperModel = PaperModel(
    Graph(
      Point(0, 0) -- Point(1, 0),
      Point(1, 0) -- Point(1, 1),
      Point(1, 1) -- Point(0, 1),
      Point(0, 1) -- Point(0, 0)
    ))
}

object PaperModelHelpers {
  implicit class FoldedPaperModel(val paperModel: PaperModel) extends AnyVal {
    @inline def <~~(paperEdge: PaperEdge[Point]): PaperModel = paperModel.fold(paperEdge)
  }
}
