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

  override def beforeEach(): Unit = {
    super.beforeEach()
    foldSelectionService = new FoldSelectionServiceImpl
  }

  "Fold selection service" should "accurately find all available operations of flat crease pattern" in {
    // when
    val availableOperations = foldSelectionService getAvailableOperations FlatCreasePattern

    // then
    availableOperations shouldBe Set(Point(0, 100) /\ Point(100, 0))
  }

  "Fold selection service" should "accurately find all available operations of layered crease pattern" in {
    // when
    val availableOperations = foldSelectionService getAvailableOperations MultiLayeredUnfoldedPaper
    val nextAvailableOperations = foldSelectionService getAvailableOperations
      (MultiLayeredUnfoldedPaper <~~ Point(0, 0) \/ Point(100, 100))

    // then
    availableOperations shouldBe Set(
      Point(0, 0) \/ Point(25, 25),
      Point(25, 25) \/ Point(50, 50),
      Point(50, 50) \/ Point(100, 100),
      Point(0, 100) \/ Point(50, 50),
      Point(50, 50) /\ Point(100, 0),
      Point(0, 50) /\ Point(25, 25),
      Point(25, 25) \/ Point(50, 0)
    )

    nextAvailableOperations shouldBe Set(Point(0, 50) /\ Point(25, 25),
                                         Point(0, 100) \/ Point(50, 50))
  }

}
