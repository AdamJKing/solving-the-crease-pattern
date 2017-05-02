package uk.ac.aber.adk15.paper.constants

import uk.ac.aber.adk15.geometry.Point
import uk.ac.aber.adk15.paper.fold.Fold
import uk.ac.aber.adk15.paper.fold.PaperEdgeHelpers._
import uk.ac.aber.adk15.paper.{CreasePattern, PaperLayer}

object UnfoldedCreasePatterns {
  val SimpleCreasePattern: CreasePattern = {
    CreasePattern from PaperLayer(
      Point(0, 0) -- Point(100, 0),
      Point(100, 0) -- Point(100, 100),
      Point(100, 100) -- Point(0, 100),
      Point(0, 100) -- Point(0, 0),
      Point(0, 100) /\ Point(100, 0)
    )
  }

  val SimpleFold: Fold = Point(0, 100) /\ Point(100, 0)

  val MediumComplexityCreasePattern: CreasePattern = {
    CreasePattern from PaperLayer(
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

  val MediumFolds: List[Fold] = List(Point(50, 50) \/ Point(25, 25),
                                     Point(100, 0) /\ Point(50, 50),
                                     Point(50, 0) \/ Point(25, 25))
}
