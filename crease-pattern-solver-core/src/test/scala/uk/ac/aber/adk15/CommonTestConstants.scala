package uk.ac.aber.adk15

import uk.ac.aber.adk15.paper.PaperEdgeHelpers._
import uk.ac.aber.adk15.paper.{CreasePattern, Foldable, Point}

object CommonTestConstants {

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
      Set(
        Point(100.0, 0.0) ~~ Point(50.0, 50.0),
        Point(50.0, 50.0) ~~ Point(100.0, 100.0),
        Point(100.0, 0.0) -- Point(100.0, 100.0)
      ),
      Set(
        Point(50.0, 50.0) ~~ Point(100.0, 0.0),
        Point(50.0, 50.0) ~~ Point(100.0, 100.0),
        Point(100.0, 0.0) -- Point(100.0, 100.0)
      ),
      Set(
        Point(100.0, 100.0) -- Point(100.0, 50.0),
        Point(75.0, 75.0) ~~ Point(100.0, 50.0),
        Point(100.0, 100.0) ~~ Point(75.0, 75.0)
      ),
      Set(
        Point(100.0, 100.0) -- Point(100.0, 50.0),
        Point(100.0, 50.0) ~~ Point(75.0, 75.0),
        Point(100.0, 100.0) ~~ Point(75.0, 75.0)
      ),
      Set(
        Point(75.0, 75.0) ~~ Point(50.0, 50.0),
        Point(100.0, 50.0) ~~ Point(75.0, 75.0),
        Point(100.0, 0.0) ~~ Point(50.0, 50.0)
      ),
      Set(
        Point(75.0, 75.0) ~~ Point(50.0, 50.0),
        Point(75.0, 75.0) ~~ Point(100.0, 50.0),
        Point(50.0, 50.0) ~~ Point(100.0, 0.0)
      )
    )

    val FlatCreasePattern: Foldable = CreasePattern from (
      Point(0, 0) -- Point(100, 0),
      Point(100, 0) -- Point(100, 100),
      Point(100, 100) -- Point(0, 100),
      Point(0, 100) -- Point(0, 0),
      Point(0, 100) /\ Point(100, 0)
    )

    val MultiLayeredUnfoldedPaper: Foldable = CreasePattern from (
      Point(0, 0) -- Point(50, 0),
      Point(50, 0) -- Point(100, 0),
      Point(0, 0) -- Point(0, 50),
      Point(0, 50) -- Point(0, 100),
      Point(0, 100) -- Point(100, 100),
      Point(100, 0) -- Point(100, 100),
      Point(0, 0) \/ Point(25, 25),
      Point(25, 25) \/ Point(50, 50),
      Point(50, 50) \/ Point(100, 100),
      Point(0, 100) \/ Point(50, 50),
      Point(50, 50) /\ Point(100, 0),
      Point(0, 50) /\ Point(25, 25),
      Point(25, 25) \/ Point(50, 0)
    )
  }
}
