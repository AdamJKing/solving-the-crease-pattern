package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.paper.CreasePatternPredef.Layer

trait Foldable {
  def fold(edge: PaperEdge[Point]): Foldable
  val creases: Seq[Layer]
}
