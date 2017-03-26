package uk.ac.aber.adk15.paper

import org.scalatest.{FlatSpec, Matchers}
import uk.ac.aber.adk15.CommonTestConstants._
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
        Set[Fold](
          Point(100, 0) -- Point(100, 100),
          Point(100, 100) -- Point(0, 100),
          Point(0, 100) ~~ Point(100, 0)
        ),
        Set[Fold](
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

  // TODO: Re-enable when fixed
//  "A crease pattern with multiple layered folds" should "be foldable" in {
//    // when
//    val creases = List(Point(50, 50) \/ Point(100, 100),
//                       Point(0, 50) /\ Point(25, 25),
//                       Point(0, 100) \/ Point(50, 50))
//
//    val foldedCreasePattern =
//      (creases foldRight CommonTestConstants.MultiLayeredUnfoldedPaper)((fold, model) =>
//        model <~~ fold)
//
//    // then
//    foldedCreasePattern.size should be(6)
//    foldedCreasePattern should be(MultiLayeredFoldedPaper)
//  }
}
