package uk.ac.aber.adk15

import uk.ac.aber.adk15.geometry.Point
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._
import uk.ac.aber.adk15.paper.{CreasePattern, PaperLayer}

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
    val BlankPaper: CreasePattern = CreasePattern from (
      PaperLayer from (
        Point(0, 0) -- Point(1, 0),
        Point(1, 0) -- Point(1, 1),
        Point(1, 1) -- Point(0, 1),
        Point(0, 1) -- Point(0, 0)
      )
    )

    val MultiLayeredFoldedPaper: CreasePattern = CreasePattern(
      List(
        PaperLayer from (
          Point(0.0, 100.0) -- Point(0.0, 0.0),
          Point(50.0, 50.0) ~~ Point(0.0, 0.0),
          Point(50.0, 50.0) ~~ Point(0.0, 100.0)
        ),
        PaperLayer(
          List(
            Point(0.0, 100.0) -- Point(0.0, 0.0),
            Point(50.0, 50.0) ~~ Point(0.0, 0.0),
            Point(0.0, 100.0) ~~ Point(50.0, 50.0)
          )
        ),
        PaperLayer(
          List(
            Point(0.0, 50.0) -- Point(0.0, 100.0),
            Point(25.0, 25.0) ~~ Point(50.0, 50.0),
            Point(50.0, 50.0) ~~ Point(0.0, 100.0),
            Point(25.0, 25.0) ~~ Point(0.0, 50.0)
          )
        ),
        PaperLayer(
          List(
            Point(0.0, 50.0) -- Point(0.0, 100.0),
            Point(25.0, 25.0) ~~ Point(50.0, 50.0),
            Point(0.0, 100.0) ~~ Point(50.0, 50.0),
            Point(0.0, 50.0) ~~ Point(25.0, 25.0)
          )
        ),
        PaperLayer(
          List(
            Point(50.0, 50.0) -- Point(0.0, 50.0),
            Point(50.0, 50.0) ~~ Point(25.0, 25.0),
            Point(25.0, 25.0) ~~ Point(0.0, 50.0)
          )
        ),
        PaperLayer(
          List(
            Point(50.0, 50.0) -- Point(0.0, 50.0),
            Point(50.0, 50.0) ~~ Point(25.0, 25.0),
            Point(0.0, 50.0) ~~ Point(25.0, 25.0)
          ))
      ))

    val FlatCreasePattern: CreasePattern = CreasePattern from (
      PaperLayer from (
        Point(0, 0) -- Point(100, 0),
        Point(100, 0) -- Point(100, 100),
        Point(100, 100) -- Point(0, 100),
        Point(0, 100) -- Point(0, 0),
        Point(0, 100) /\ Point(100, 0)
      )
    )

    val MultiLayeredUnfoldedPaper: CreasePattern = CreasePattern from (
      PaperLayer from (
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
    )
  }
}
