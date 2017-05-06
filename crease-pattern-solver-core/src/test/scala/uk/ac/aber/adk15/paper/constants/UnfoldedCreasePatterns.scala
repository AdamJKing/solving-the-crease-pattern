package uk.ac.aber.adk15.paper.constants

import uk.ac.aber.adk15.geometry.Point
import uk.ac.aber.adk15.paper.fold.Fold
import uk.ac.aber.adk15.paper.fold.Fold.Helpers._
import uk.ac.aber.adk15.paper.{CreasePattern, PaperLayer}

/**
  * Constants for the unfolded states of models
  * as well as the folds that are used to fold them.
  */
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
                                     Point(0, 100) \/ Point(50, 50),
                                     Point(0, 50) /\ Point(25, 25))

  val AlternativeMediumFolds: List[Fold] = List(Point(0.0, 0.0) \/ Point(25.0, 25.0),
                                                Point(0.0, 50.0) /\ Point(25.0, 25.0),
                                                Point(50.0, 50.0) \/ Point(0.0, 100.0))
}
