package uk.ac.aber.adk15

import scalax.collection.edge.Implicits._
import scalax.collection.edge.LUnDiEdge
import scalax.collection.immutable.Graph


object PaperModel {
  type Paper = Graph[Point, LUnDiEdge]

  /**
    * Represents a blank piece of paper with no folds.
    * This blank paper is a square with four corners.
    *
    *    0,0        1,0
    *     +----------+
    *     |          |
    *     |          |
    *     |          |
    *     |          |
    *     +----------+
    *    0,1        1,1
    *
    */
  val BlankPaper: Graph[Point, LUnDiEdge] = Graph[Point, LUnDiEdge](
    (Point(0, 0) ~+ Point(1, 0))(HardEdge),
    (Point(1, 0) ~+ Point(1, 1))(HardEdge),
    (Point(1, 1) ~+ Point(0, 1))(HardEdge),
    (Point(0, 1) ~+ Point(0, 0))(HardEdge)
  )
}

