package uk.ac.aber.adk15.paper.newapi.constants

import uk.ac.aber.adk15.geometry.Point
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._
import uk.ac.aber.adk15.paper.newapi.{NewCreasePattern, NewFold, NewPaperLayer}

object UnfoldedCreasePatterns {
  val SimpleCreasePattern: NewCreasePattern = {
    NewCreasePattern from NewPaperLayer(
      Point(0, 0) +-- Point(100, 0),
      Point(100, 0) +-- Point(100, 100),
      Point(100, 100) +-- Point(0, 100),
      Point(0, 100) +-- Point(0, 0),
      Point(0, 100) +/\ Point(100, 0)
    )
  }

  val SimpleFold: NewFold = Point(0, 100) +/\ Point(100, 0)

  val MediumComplexityCreasePattern: NewCreasePattern = {
    NewCreasePattern from NewPaperLayer(
      Point(0, 0) +-- Point(100, 0),
      Point(100, 0) +-- Point(100, 100),
      Point(100, 100) +-- Point(50, 100),
      Point(50, 100) +-- Point(0, 100),
      Point(0, 100) +-- Point(0, 50),
      Point(0, 50) +-- Point(0, 0),
      Point(0, 0) +/\ Point(50, 50),
      Point(50, 50) +\/ Point(100, 100),
      Point(0, 50) +\/ Point(25, 75),
      Point(25, 75) +\/ Point(50, 50),
      Point(0, 100) +\/ Point(25, 75),
      Point(50, 50) +\/ Point(100, 0),
      Point(25, 75) +/\ Point(50, 100)
    )
  }

  val MediumFolds: List[NewFold] = List(Point(50, 50) +\/ Point(100, 0),
                                        Point(0, 0) +/\ Point(50, 50),
                                        Point(50, 0) +/\ Point(75, 25))
}
