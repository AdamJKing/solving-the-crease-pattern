package uk.ac.aber.adk15.services

import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.CommonTestConstants.ModelConstants._
import uk.ac.aber.adk15.paper.CreasePatternPredef.Helpers._
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._
import uk.ac.aber.adk15.paper.Point

class FoldSelectionServiceSpec extends CommonFlatSpec {

  private var foldSelectionService: FoldSelectionService = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    foldSelectionService = new FoldSelectionServiceImpl
  }

  "Fold selection service" should "accurately find all available operations of flat crease pattern" in {
    // when
    val availableOperations = foldSelectionService.getAvailableOperations(FlatCreasePattern)

    // then
    availableOperations shouldBe Set(Point(0, 100) /\ Point(100, 0))
  }

  "Fold selection service" should "accurately find all available operations of layered crease pattern" in {
    // when
    val availableOperations =
      foldSelectionService.getAvailableOperations(MultiLayeredUnfoldedPaper)

    val folded                  = MultiLayeredUnfoldedPaper <~~ Point(0, 0) \/ Point(100, 100)
    val nextAvailableOperations = foldSelectionService.getAvailableOperations(folded)

    val finalFolded              = folded <~~ Point(0, 50) /\ Point(25, 25)
    val finalAvailableOperations = foldSelectionService.getAvailableOperations(finalFolded)

    // then
    availableOperations should be(
      Set(
        Point(0, 0) \/ Point(25, 25),
        Point(25, 25) \/ Point(50, 50),
        Point(50, 50) \/ Point(100, 100),
        Point(0, 100) \/ Point(50, 50),
        Point(50, 50) /\ Point(100, 0),
        Point(0, 50) /\ Point(25, 25),
        Point(25, 25) \/ Point(50, 0)
      ))

    nextAvailableOperations should {
      contain(Point(0, 50) /\ Point(25, 25)) and contain(Point(0, 100) \/ Point(50, 50))
    }

    finalAvailableOperations should contain(Point(0, 100) \/ Point(50, 50))
  }

  "When folding a fold that invalidates other folds those folds" should "no longer be available" in {
    // given
    val folded = MultiLayeredUnfoldedPaper <~~ (Point(0, 50) /\ Point(25, 25))

    // when
    val availableFolds = foldSelectionService.getAvailableOperations(folded)

    // then
    availableFolds should not contain (Point(25, 25) \/ Point(50, 50))
    availableFolds should not contain (Point(100, 100) /\ Point(75, 75))
    availableFolds should {
      contain(Point(0, 100) \/ Point(50, 50)) and contain(Point(50, 50) /\ Point(100, 0))
    }
  }

  "Blocked folds" should "not be reported as foldable" in {
    // given
    val folded = MultiLayeredUnfoldedPaper <~~
      Point(0, 100) \/ Point(50, 50) <~~
      Point(50, 100) /\ Point(75, 75)

    // when
    val availableFolds = foldSelectionService.getAvailableOperations(folded)

    // then
    availableFolds should not contain Point(50, 50) /\ Point(0, 0)
  }

  "Fold selection service" should "accurately find all available operations of layered crease pattern (alt.)" in {
    // when
    val availableOperations =
      foldSelectionService.getAvailableOperations(MultiLayeredUnfoldedPaper)

    val folded                  = MultiLayeredUnfoldedPaper <~~ Point(0, 0) \/ Point(100, 100)
    val nextAvailableOperations = foldSelectionService.getAvailableOperations(folded)

    val finalFolded              = folded <~~ Point(50, 50) \/ Point(0, 100)
    val finalAvailableOperations = foldSelectionService.getAvailableOperations(finalFolded)

    // then
    availableOperations should be(
      Set(
        Point(0, 0) \/ Point(25, 25),
        Point(25, 25) \/ Point(50, 50),
        Point(50, 50) \/ Point(100, 100),
        Point(0, 100) \/ Point(50, 50),
        Point(50, 50) /\ Point(100, 0),
        Point(0, 50) /\ Point(25, 25),
        Point(25, 25) \/ Point(50, 0)
      ))

    nextAvailableOperations should {
      contain(Point(0, 50) /\ Point(25, 25)) and contain(Point(0, 100) \/ Point(50, 50))
    }

    finalAvailableOperations should contain(Point(0, 50) /\ Point(25, 25))
  }
}
