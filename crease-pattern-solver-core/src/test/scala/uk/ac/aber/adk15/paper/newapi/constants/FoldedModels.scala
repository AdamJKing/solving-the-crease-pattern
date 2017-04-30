package uk.ac.aber.adk15.paper.newapi.constants

import uk.ac.aber.adk15.geometry.Point
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._
import uk.ac.aber.adk15.paper.newapi.{NewCreasePattern, NewPaperLayer}

object FoldedModels {

  val SimpleFoldedModel: NewCreasePattern = {
    NewCreasePattern from (
      NewPaperLayer(
        Point(0, 100) +~~ Point(100, 0),
        Point(100, 100) +-- Point(0, 100),
        Point(100, 0) +-- Point(100, 100)
      ),
      NewPaperLayer(
        Point(0, 100) +~~ Point(100, 0),
        Point(100, 100) +-- Point(0, 100),
        Point(100, 0) +-- Point(100, 100)
      )
    )
  }

  val MediumComplexityModel: NewCreasePattern = {
    NewCreasePattern from (
      NewPaperLayer(
        Point(0, 0) +-- Point(100, 0),
        Point(50, 50) +~~ Point(100, 0),
        Point(0, 0) +~~ Point(50, 50)
      ),
      NewPaperLayer(
        Point(0, 0) +-- Point(100, 0),
        Point(50, 50) +~~ Point(100, 0),
        Point(0, 0) +~~ Point(50, 50)
      ),
      NewPaperLayer(
        Point(0, 0) +-- Point(50, 0),
        Point(75, 25) +~~ Point(50, 50),
        Point(0, 0) +~~ Point(50, 50),
        Point(50, 0) +~~ Point(75, 25)
      ),
      NewPaperLayer(
        Point(0, 0) +-- Point(50, 0),
        Point(75, 25) +~~ Point(50, 50),
        Point(0, 0) +~~ Point(50, 50),
        Point(50, 0) +~~ Point(75, 25)
      ),
      NewPaperLayer(
        Point(50, 50) +-- Point(50, 0),
        Point(50, 50) +~~ Point(75, 25),
        Point(50, 0) +~~ Point(75, 25)
      ),
      NewPaperLayer(
        Point(50, 50) +-- Point(50, 0),
        Point(50, 50) +~~ Point(75, 25),
        Point(50, 0) +~~ Point(75, 25)
      )
    )
  }
}
