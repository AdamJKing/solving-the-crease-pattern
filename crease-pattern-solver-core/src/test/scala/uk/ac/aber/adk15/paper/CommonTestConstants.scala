package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.paper.PaperEdgeHelpers._

object CommonTestConstants {
  val FlatCreasePattern: CreasePattern = CreasePattern from (
    Point(0, 0) -- Point(100, 0),
    Point(100, 0) -- Point(100, 100),
    Point(100, 100) -- Point(0, 100),
    Point(0, 100) -- Point(0, 0),
    Point(0, 100) /\ Point(100, 0)
  )

  val LayeredCreasePattern: CreasePattern = CreasePattern from (
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
