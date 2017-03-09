package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.Point

trait Foldable {
  def fold(edge: PaperEdge[Point]): Foldable
}
