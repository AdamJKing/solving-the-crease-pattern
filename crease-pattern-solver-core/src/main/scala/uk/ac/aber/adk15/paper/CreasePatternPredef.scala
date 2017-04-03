package uk.ac.aber.adk15.paper

object CreasePatternPredef {
  object Helpers {
    implicit class FoldedCreasePattern(val paperModel: CreasePattern) extends AnyVal {
      @inline def <~~(paperEdge: Fold): CreasePattern = paperModel fold paperEdge
    }
  }
}
