package uk.ac.aber.adk15

import scalax.collection.immutable.Graph

/**
  * A simple representation of a piece of paper. It
  * uses a graph and a series of points to describe
  * the internal fold structure.
  *
  * @param creasePattern a graph describing the series of folds or edges that exist in the model
  *
  */
class PaperModel(creasePattern: Graph[Point, PaperEdge])

/**
  * Companion object for a paper model.
  */
object PaperModel {
  /**
    * Constructs a new graph using the supplied folds.
    *
    * @param folds a variable length list of folds to add to the model's pattern
    * @return a new paper model
    */
  def apply(folds: PaperEdge[Point]*): PaperModel = new PaperModel(Graph.from(Nil, folds.toTraversable))
}
