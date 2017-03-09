package uk.ac.aber.adk15.paper

import org.scalatest.{FlatSpec, Matchers}
import uk.ac.aber.adk15.Point
import uk.ac.aber.adk15.paper.CreasePatternPredef.Helpers._
import uk.ac.aber.adk15.paper.CreasePatternPredef.Layer
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._

class CreasePatternSpec extends FlatSpec with Matchers {

  "The crease pattern" should "be capable of being folded" in {

    // given
    val creasePattern = CreasePattern(
      Layer(
        Point(0, 0) -- Point(100, 0),
        Point(100, 0) -- Point(100, 100),
        Point(100, 100) -- Point(0, 100),
        Point(0, 100) -- Point(0, 0),
        Point(0, 100) /\ Point(100, 0)
      ))

    // when
    val foldedCreasePattern = creasePattern <~~ Point(0, 100) /\ Point(100, 0)

    // then
    foldedCreasePattern should be(
      CreasePattern(
        Layer(
          Point(100, 0) -- Point(100, 100),
          Point(100, 100) -- Point(0, 100),
          Point(0, 100) ~~ Point(100, 0)
        ),
        Layer(
          Point(100, 0) -- Point(100, 100),
          Point(100, 100) -- Point(0, 100),
          Point(0, 100) ~~ Point(100, 0)
        )
      ))
  }

  "Crease patterns with the same edges" should "equal each other" in {
    // given
    val a = CreasePattern from Point(100, 0) -- Point(0, 100)

    val b = CreasePattern from Point(100, 0) -- Point(0, 100)

    val c = CreasePattern from Point(0, 100) -- Point(100, 0)

    // then
    a should equal(b)
    b should equal(c)
  }
}
