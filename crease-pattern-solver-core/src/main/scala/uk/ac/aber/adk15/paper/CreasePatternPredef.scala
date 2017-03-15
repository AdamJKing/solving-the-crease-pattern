package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.Point
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._

object CreasePatternPredef {

  type Layer = Set[PaperEdge[Point]]
  type Fold  = PaperEdge[Point]

  object Layer {
    def apply(edges: PaperEdge[Point]*): Layer     = edges.toSet
    def apply(edges: Set[PaperEdge[Point]]): Layer = edges
  }

  object Constants {

    object ModelConstants {

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
      val BlankPaper: CreasePattern = CreasePattern from
        (
          Point(0, 0) -- Point(1, 0),
          Point(1, 0) -- Point(1, 1),
          Point(1, 1) -- Point(0, 1),
          Point(0, 1) -- Point(0, 0)
      )

      val MultiLayeredFoldedPaper: CreasePattern = CreasePattern(
        Layer(
          Point(75, 75) ~~ Point(50, 100),
          Point(50, 50) -- Point(50, 100),
          Point(50, 50) ~~ Point(75, 75)
        ),
        Layer(
          Point(50, 100) ~~ Point(75, 75),
          Point(50, 50) -- Point(50, 100),
          Point(50, 50) ~~ Point(75, 75)
        ),
        Layer(
          Point(50, 100) ~~ Point(75, 75),
          Point(0, 100) ~~ Point(50, 50),
          Point(50, 100) -- Point(0, 100),
          Point(75, 75) ~~ Point(50, 50)
        ),
        Layer(
          Point(75, 75) ~~ Point(50, 100),
          Point(50, 50) ~~ Point(0, 100),
          Point(50, 100) -- Point(0, 100),
          Point(75, 75) ~~ Point(50, 50)
        ),
        Layer(
          Point(50, 50) ~~ Point(100, 100),
          Point(50, 50) ~~ Point(0, 100),
          Point(0, 100) -- Point(100, 100)
        ),
        Layer(
          Point(50, 50) ~~ Point(100, 100),
          Point(0, 100) ~~ Point(50, 50),
          Point(0, 100) -- Point(100, 100)
        )
      )
    }

  }

  object Helpers {

    implicit class FoldedCreasePattern(val paperModel: CreasePattern) extends AnyVal {
      @inline def <~~(paperEdge: PaperEdge[Point]): CreasePattern = paperModel fold paperEdge
    }

  }

}
