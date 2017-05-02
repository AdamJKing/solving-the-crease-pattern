package uk.ac.aber.adk15.paper.constants

import uk.ac.aber.adk15.geometry.Point
import uk.ac.aber.adk15.paper.fold.PaperEdgeHelpers._
import uk.ac.aber.adk15.paper.{CreasePattern, PaperLayer}

object FoldedModels {

  val SimpleFoldedModel: CreasePattern = {
    CreasePattern from (
      PaperLayer(
        Point(0, 100) ~~ Point(100, 0),
        Point(100, 100) -- Point(0, 100),
        Point(100, 0) -- Point(100, 100)
      ),
      PaperLayer(
        Point(0, 100) ~~ Point(100, 0),
        Point(100, 100) -- Point(0, 100),
        Point(100, 0) -- Point(100, 100)
      )
    )
  }

  val MediumComplexityModel: CreasePattern = {
    CreasePattern from (
      PaperLayer(
        Point(25.0, 25.0) ~~ Point(50.0, 0.0),
        Point(50.0, 50.0) -- Point(50.0, 0.0),
        Point(50.0, 50.0) ~~ Point(25.0, 25.0)
      ),
      PaperLayer(
        Point(50.0, 0.0) ~~ Point(25.0, 25.0),
        Point(50.0, 50.0) -- Point(50.0, 0.0),
        Point(50.0, 50.0) ~~ Point(25.0, 25.0)
      ),
      PaperLayer(
        Point(100.0, 0.0) ~~ Point(50.0, 50.0),
        Point(50.0, 0.0) ~~ Point(25.0, 25.0),
        Point(50.0, 0.0) -- Point(100.0, 0.0),
        Point(25.0, 25.0) ~~ Point(50.0, 50.0)
      ),
      PaperLayer(
        Point(50.0, 50.0) ~~ Point(100.0, 0.0),
        Point(25.0, 25.0) ~~ Point(50.0, 0.0),
        Point(50.0, 0.0) -- Point(100.0, 0.0),
        Point(25.0, 25.0) ~~ Point(50.0, 50.0)
      ),
      PaperLayer(
        Point(50.0, 50.0) ~~ Point(100.0, 0.0),
        Point(100.0, 0.0) -- Point(0.0, 0.0),
        Point(50.0, 50.0) ~~ Point(0.0, 0.0)
      ),
      PaperLayer(
        Point(100.0, 0.0) ~~ Point(50.0, 50.0),
        Point(100.0, 0.0) -- Point(0.0, 0.0),
        Point(50.0, 50.0) ~~ Point(0.0, 0.0)
      )
    )
  }

  val AlternativeMediumComplexityModel: CreasePattern = {
    CreasePattern from (
      PaperLayer(
        Point(75.0, 75.0) ~~ Point(50.0, 100.0),
        Point(50.0, 50.0) -- Point(50.0, 100.0),
        Point(50.0, 50.0) ~~ Point(75.0, 75.0)
      ),
      PaperLayer(
        Point(50.0, 100.0) ~~ Point(75.0, 75.0),
        Point(50.0, 50.0) -- Point(50.0, 100.0),
        Point(50.0, 50.0) ~~ Point(75.0, 75.0)
      ),
      PaperLayer(
        Point(0.0, 100.0) ~~ Point(50.0, 50.0),
        Point(50.0, 100.0) ~~ Point(75.0, 75.0),
        Point(50.0, 100.0) -- Point(0.0, 100.0),
        Point(75.0, 75.0) ~~ Point(50.0, 50.0)
      ),
      PaperLayer(
        Point(50.0, 50.0) ~~ Point(0.0, 100.0),
        Point(75.0, 75.0) ~~ Point(50.0, 100.0),
        Point(50.0, 100.0) -- Point(0.0, 100.0),
        Point(75.0, 75.0) ~~ Point(50.0, 50.0)
      ),
      PaperLayer(
        Point(50.0, 50.0) ~~ Point(0.0, 100.0),
        Point(0.0, 100.0) -- Point(100.0, 100.0),
        Point(50.0, 50.0) ~~ Point(100.0, 100.0)
      ),
      PaperLayer(
        Point(0.0, 100.0) ~~ Point(50.0, 50.0),
        Point(0.0, 100.0) -- Point(100.0, 100.0),
        Point(50.0, 50.0) ~~ Point(100.0, 100.0)
      )
    )
  }

  val AlternativeMediumFolds = List(Point(0.0, 0.0) \/ Point(25.0, 25.0),
                                    Point(0.0, 50.0) /\ Point(25.0, 25.0),
                                    Point(50.0, 50.0) \/ Point(0.0, 100.0))
}
