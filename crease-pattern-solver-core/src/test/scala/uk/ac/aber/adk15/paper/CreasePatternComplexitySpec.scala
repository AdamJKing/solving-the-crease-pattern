package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.CommonTestConstants.ModelConstants._
import uk.ac.aber.adk15.paper.CreasePatternPredef.Helpers._
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._

class CreasePatternComplexitySpec extends CommonFlatSpec {

  "The crease pattern" should "be capable of being folded" in {

    // given
    val creasePattern = FlatCreasePattern

    // when
    val foldedCreasePattern = creasePattern <~~ Point(0, 100) /\ Point(100, 0)

    // then
    foldedCreasePattern should be(
      CreasePattern(List(
        PaperLayer(
          List(
            Point(0, 100) ~~ Point(100, 0),
            Point(100, 100) -- Point(0, 100),
            Point(100, 0) -- Point(100, 100)
          )),
        PaperLayer(
          List(
            Point(0, 100) ~~ Point(100, 0),
            Point(100, 100) -- Point(0, 100),
            Point(100, 0) -- Point(100, 100)
          ))
      )))
  }

  "A crease pattern with multiple layered folds" should "be fold-able" in {
    // when
    val creases = List(Point(50, 50) \/ Point(100, 100),
                       Point(50, 50) \/ Point(0, 100),
                       Point(0, 50) /\ Point(25, 25))

    val foldedCreasePattern =
      (creases foldLeft MultiLayeredUnfoldedPaper)(_ <~~ _)

    // then
    withClue(foldedCreasePattern)(foldedCreasePattern.size should be(6))
    foldedCreasePattern should be(MultiLayeredFoldedPaper)
  }

  "A crease pattern with multiple layered folds" should "be fold-able regardless of direction" in {
    // given
    val RotatedMultiLayeredUnfoldedPaper: CreasePattern = CreasePattern from (
      PaperLayer from (
        Point(0, 0) -- Point(100, 0),
        Point(100, 0) -- Point(100, 100),
        Point(100, 100) -- Point(50, 100),
        Point(50, 100) -- Point(0, 100),
        Point(0, 100) -- Point(0, 50),
        Point(0, 50) -- Point(0, 0),
        Point(0, 0) /\ Point(50, 50),
        Point(50, 50) \/ Point(100, 100),
        Point(0, 50) \/ Point(25, 75),
        Point(25, 75) \/ Point(50, 50),
        Point(0, 100) \/ Point(25, 75),
        Point(50, 50) \/ Point(100, 0),
        Point(25, 75) /\ Point(50, 100)
      )
    )

    // when
    val creases = List(Point(0, 100) \/ Point(100, 0),
                       Point(100, 100) \/ Point(50, 50),
                       Point(50, 0) \/ Point(75, 25))

    val foldedCreasePattern =
      (creases foldLeft RotatedMultiLayeredUnfoldedPaper)(_ <~~ _)

    // then
    withClue(foldedCreasePattern)(foldedCreasePattern.size should be(6))
    foldedCreasePattern should be(
      CreasePattern(List(
        PaperLayer(
          List(Point(50.0, 50.0) -- Point(100.0, 50.0),
               Point(50.0, 50.0) ~~ Point(75.0, 25.0),
               Point(100.0, 50.0) ~~ Point(75.0, 25.0))
        ),
        PaperLayer(
          List(Point(100.0, 50.0) -- Point(50.0, 50.0),
               Point(50.0, 50.0) ~~ Point(75.0, 25.0),
               Point(75.0, 25.0) ~~ Point(100.0, 50.0))
        ),
        PaperLayer(
          List(Point(100.0, 50.0) -- Point(100.0, 100.0),
               Point(75.0, 25.0) ~~ Point(50.0, 50.0),
               Point(100.0, 100.0) ~~ Point(50.0, 50.0),
               Point(100.0, 50.0) ~~ Point(75.0, 25.0))
        ),
        PaperLayer(
          List(Point(100.0, 100.0) -- Point(100.0, 50.0),
               Point(75.0, 25.0) ~~ Point(50.0, 50.0),
               Point(50.0, 50.0) ~~ Point(100.0, 100.0),
               Point(75.0, 25.0) ~~ Point(100.0, 50.0))
        ),
        PaperLayer(
          List(Point(100.0, 100.0) -- Point(100.0, 0.0),
               Point(50.0, 50.0) ~~ Point(100.0, 0.0),
               Point(100.0, 100.0) ~~ Point(50.0, 50.0))
        ),
        PaperLayer(
          List(Point(100.0, 0.0) -- Point(100.0, 100.0),
               Point(50.0, 50.0) ~~ Point(100.0, 0.0),
               Point(50.0, 50.0) ~~ Point(100.0, 100.0))
        )
      )))
  }
}
