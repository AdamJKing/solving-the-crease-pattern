package uk.ac.aber.adk15.paper

import org.scalatest.{FlatSpec, Matchers}
import uk.ac.aber.adk15.CommonTestConstants.ModelConstants._
import uk.ac.aber.adk15.paper.CreasePatternPredef.Helpers._
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._

class CreasePatternSpec extends FlatSpec with Matchers {

  "The crease pattern" should "be capable of being folded" in {

    // given
    val creasePattern = FlatCreasePattern

    // when
    val foldedCreasePattern = creasePattern <~~ Point(0, 100) /\ Point(100, 0)

    // then
    foldedCreasePattern should be(
      CreasePattern(
        Set(
          Point(0, 100) ~~ Point(100, 0),
          Point(0, 0) -- Point(0, 100),
          Point(100, 0) -- Point(0, 0)
        ),
        Set(
          Point(0, 100) ~~ Point(100, 0),
          Point(0, 0) -- Point(0, 100),
          Point(100, 0) -- Point(0, 0)
        )
      )
    )
  }

  "Crease patterns with the same edges" should "equal each other" in {
    // given
    val first = CreasePattern(
      Set(
        Point(0, 100) ~~ Point(100, 0),
        Point(0, 0) -- Point(0, 100),
        Point(100, 0) -- Point(0, 0)
      ),
      Set(
        Point(0, 100) ~~ Point(100, 0),
        Point(0, 0) -- Point(0, 100),
        Point(100, 0) -- Point(0, 0)
      )
    )

    val second = CreasePattern(
      Set(
        Point(0.0, 100.0) ~~ Point(100.0, 0.0),
        Point(0.0, 0.0) -- Point(0.0, 100.0),
        Point(100.0, 0.0) -- Point(0.0, 0.0)
      ),
      Set(
        Point(0.0, 100.0) ~~ Point(100.0, 0.0),
        Point(0.0, 100.0) -- Point(0.0, 0.0),
        Point(0.0, 0.0) -- Point(100.0, 0.0)
      )
    )

    // then
    first should equal(second)
    first.hashCode() should equal(second.hashCode())
  }

  "A crease pattern with multiple layered folds" should "be fold-able" in {
    // when
    val creases = List(Point(50, 50) \/ Point(100, 100),
                       Point(100, 0) /\ Point(50, 50),
                       Point(100, 50) /\ Point(75, 75))

    val foldedCreasePattern =
      (creases foldLeft MultiLayeredUnfoldedPaper)(_ <~~ _)

    // then
    withClue(foldedCreasePattern)(foldedCreasePattern.size should be(6))
    foldedCreasePattern should be(MultiLayeredFoldedPaper)
  }

  "A crease pattern with multiple layered folds" should "be fold-able regardless of direction" in {
    // given
    val RotatedMultiLayeredUnfoldedPaper: Foldable =
      CreasePattern from (
        Point(0, 0) -- Point(50, 0),
        Point(50, 0) -- Point(100, 0),
        Point(0, 0) -- Point(0, 50),
        Point(0, 50) -- Point(0, 100),
        Point(0, 100) -- Point(100, 100),
        Point(100, 0) -- Point(100, 100),
        Point(0, 0) /\ Point(50, 50),
        Point(50, 50) \/ Point(100, 100),
        Point(0, 50) \/ Point(25, 75),
        Point(25, 75) \/ Point(50, 50),
        Point(0, 100) \/ Point(25, 75),
        Point(50, 50) \/ Point(100, 0),
        Point(25, 75) /\ Point(50, 100)
    )

    // when
    val creases = List(Point(0, 100) \/ Point(100, 0),
                       Point(50, 50) \/ Point(0, 0),
                       Point(50, 100) /\ Point(25, 75))

    val foldedCreasePattern =
      (creases foldLeft RotatedMultiLayeredUnfoldedPaper)(_ <~~ _)

    // then
    withClue(foldedCreasePattern)(foldedCreasePattern.size should be(6))
    foldedCreasePattern should be(
      CreasePattern(
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
      ))
  }
}
