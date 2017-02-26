package uk.ac.aber.adk15

import scalax.collection.immutable.Graph

class PaperModel(creasePattern: Graph[Point, PaperEdge])

object PaperModel {
  def apply(folds: PaperEdge[Point]*): PaperModel = new PaperModel(Graph.from(Nil, folds.toTraversable))
}
