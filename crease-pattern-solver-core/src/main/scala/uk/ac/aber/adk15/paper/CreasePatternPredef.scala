package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.paper.PaperEdgeHelpers._

object CreasePatternPredef {
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
      val BlankPaper: Foldable = CreasePattern from
        (
          Point(0, 0) -- Point(1, 0),
          Point(1, 0) -- Point(1, 1),
          Point(1, 1) -- Point(0, 1),
          Point(0, 1) -- Point(0, 0)
      )

      val MultiLayeredFoldedPaper: Foldable = CreasePattern(
        Set[Fold](
          Point(75, 75) ~~ Point(50, 100),
          Point(50, 50) -- Point(50, 100),
          Point(50, 50) ~~ Point(75, 75)
        ),
        Set[Fold](
          Point(50, 100) ~~ Point(75, 75),
          Point(50, 50) -- Point(50, 100),
          Point(50, 50) ~~ Point(75, 75)
        ),
        Set[Fold](
          Point(50, 100) ~~ Point(75, 75),
          Point(0, 100) ~~ Point(50, 50),
          Point(50, 100) -- Point(0, 100),
          Point(75, 75) ~~ Point(50, 50)
        ),
        Set[Fold](
          Point(75, 75) ~~ Point(50, 100),
          Point(50, 50) ~~ Point(0, 100),
          Point(50, 100) -- Point(0, 100),
          Point(75, 75) ~~ Point(50, 50)
        ),
        Set[Fold](
          Point(50, 50) ~~ Point(100, 100),
          Point(50, 50) ~~ Point(0, 100),
          Point(0, 100) -- Point(100, 100)
        ),
        Set[Fold](
          Point(50, 50) ~~ Point(100, 100),
          Point(0, 100) ~~ Point(50, 50),
          Point(0, 100) -- Point(100, 100)
        )
      )
    }

  }

  object Helpers {

    implicit class FoldedCreasePattern(val paperModel: Foldable) extends AnyVal {
      @inline def <~~(paperEdge: Fold): Foldable = paperModel fold paperEdge
    }

  }

}
