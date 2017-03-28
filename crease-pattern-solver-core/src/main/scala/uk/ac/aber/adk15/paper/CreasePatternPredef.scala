package uk.ac.aber.adk15.paper

object CreasePatternPredef {
  object Helpers {
    implicit class FoldedCreasePattern(val paperModel: Foldable) extends AnyVal {
      @inline def <~~(paperEdge: Fold): Foldable = paperModel fold paperEdge
    }
  }
}
