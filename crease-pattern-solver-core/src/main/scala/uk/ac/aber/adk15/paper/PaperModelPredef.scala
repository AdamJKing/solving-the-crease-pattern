package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.Point

/**
  * Object containing useful constants with respect to the paper model.
  *
  */
object PaperModelPredef {
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
    FoldedPaperEdge(Point(0, 0), Point(1, 0)),
    FoldedPaperEdge(Point(1, 0), Point(1, 1)),
    FoldedPaperEdge(Point(1, 1), Point(0, 1)),
    FoldedPaperEdge(Point(0, 1), Point(0, 0))
  )

  implicit class FoldedPaperModel(val paperModel: PaperModel) extends AnyVal {
    @inline def <~~(paperEdge: PaperEdge[Point]): PaperModel = paperModel.fold(paperEdge)
  }
}
