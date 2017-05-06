package uk.ac.aber.adk15.paper.fold

import org.mockito.BDDMockito._
import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.geometry.{Line, Point}
import uk.ac.aber.adk15.paper.PaperLayer
import uk.ac.aber.adk15.paper.constants.UnfoldedCreasePatterns.{
  MediumComplexityCreasePattern,
  MediumFolds
}
import uk.ac.aber.adk15.paper.fold.Fold.Helpers._

class FoldSelectionSpec extends CommonFlatSpec {

  "Creating a fold selection with one layer" should "accurately report if a fold is available" in {
    // given
    val availableFold = mock[Fold]
    val layer         = mock[PaperLayer]
    val foldSelection = new FoldSelection(List(layer))

    given(layer.valleyFolds) willReturn Set(availableFold)
    given(layer.mountainFolds) willReturn Set.empty[Fold]

    // when
    val availableOperations = foldSelection.getAvailableOperations

    // then
    availableOperations should contain(availableFold)
  }

  "Creating a fold selection with multiple layers" should "accurately report if a fold is available" in {
    // given
    val availableFold = mock[Fold]
    val layers        = List.fill(5)(mock[PaperLayer])
    val foldSelection = new FoldSelection(layers)

    layers foreach (layer => {
      given(layer.valleyFolds) willReturn Set(availableFold)
      given(layer.mountainFolds) willReturn Set.empty[Fold]
      given(layer.creasedFolds) willReturn Set.empty[Fold]
    })

    // when
    val availableOperations = foldSelection.getAvailableOperations

    // then
    availableOperations should contain(availableFold)
  }

  "Creating a fold selection with multiple layers and a corresponding creased fold plus positive test continuity" should "accurately report available folds" in {
    // given
    val intersection        = mock[Point]
    val availableFold       = Fold(mock[Line], ValleyFold)
    val correspondingCrease = Fold(mock[Line], CreasedFold)
    val layers              = List.fill(5)(mock[PaperLayer])
    val foldSelection       = new FoldSelection(layers)

    given(correspondingCrease.line intersectWith availableFold.line) willReturn Some(intersection)

    layers foreach (layer => {
      given(layer.valleyFolds) willReturn Set(availableFold)
      given(layer.mountainFolds) willReturn Set.empty[Fold]
      given(layer.creasedFolds) willReturn Set(correspondingCrease)
      given(layer coversPoint intersection) willReturn true
      given(layer.surfaceArea) willReturn 10
      given(layer contains availableFold) willReturn true
    })

    given(layers.head.surfaceArea) willReturn 50

    // when
    val availableFolds = foldSelection.getAvailableOperations

    // then
    availableFolds should contain(availableFold)
  }

  "Creating a fold selection with multiple layers and a corresponding creased fold plus partial test continuity" should "accurately report available folds" in {
    // given
    val intersection  = mock[Point]
    val availableFold = Fold(mock[Line], ValleyFold)
    val layers        = List.fill(5)(mock[PaperLayer])
    val foldSelection = new FoldSelection(layers)

    layers foreach (layer => {
      given(layer.valleyFolds) willReturn Set(availableFold)
      given(layer.mountainFolds) willReturn Set.empty[Fold]

      val correspondingCrease = Fold(mock[Line], CreasedFold)
      given(layer.creasedFolds) willReturn Set(correspondingCrease)
      given(correspondingCrease.line intersectWith availableFold.line) willReturn Some(
        intersection)

      given(layer coversPoint intersection) willReturn true
      given(layer.surfaceArea) willReturn 10
      given(layer contains availableFold.line) willReturn true
    })

    given(layers.head.surfaceArea) willReturn 50
    given(layers.last contains availableFold.line) willReturn false

    // when
    val availableFolds = foldSelection.getAvailableOperations

    // then
    availableFolds shouldBe empty
  }

  "Creating a fold selection with multiple layers when the top layer doesn't block the one below" should "accurately report if a fold is available" in {
    // given
    val availableFold = mock[Fold]
    val layers        = List.fill(5)(mock[PaperLayer])
    val foldSelection = new FoldSelection(layers)

    layers.tail foreach (layer => {
      given(layer.valleyFolds) willReturn Set(availableFold)
      given(layer.mountainFolds) willReturn Set.empty[Fold]
      given(layer.creasedFolds) willReturn Set.empty[Fold]
      given(layer.surfaceArea) willReturn 10
    })

    given(layers.head.valleyFolds) willReturn Set.empty[Fold]
    given(layers.head.mountainFolds) willReturn Set.empty[Fold]
    given(layers.head.creasedFolds) willReturn Set.empty[Fold]
    given(layers.head.surfaceArea) willReturn 5

    // when
    val availableOperations = foldSelection.getAvailableOperations

    // then
    availableOperations should contain(availableFold)
  }

  "(Real data) Creating a fold selection with one layer" should "accurately report if a fold is available" in {
    // given
    val foldedCreasePatternA = MediumComplexityCreasePattern <~~ Point(0, 50) /\ Point(25, 25)
    val foldedCreasePatternB = (MediumComplexityCreasePattern /: (MediumFolds take 2))(_ <~~ _)
    val foldedCreasePatternC =
      (MediumComplexityCreasePattern /: List(Point(25.0, 25.0) \/ Point(50.0, 0.0),
                                             Point(0.0, 100.0) \/ Point(50.0, 50.0)))(_ <~~ _)

    // when
    val availableOperationsA =
      new FoldSelection(foldedCreasePatternA.layers).getAvailableOperations
    val availableOperationsB =
      new FoldSelection(foldedCreasePatternB.layers).getAvailableOperations
    val availableOperationsC =
      new FoldSelection(foldedCreasePatternC.layers).getAvailableOperations

    // then
    availableOperationsA shouldBe Set(
      Point(0, 100) \/ Point(50, 50),
      Point(50, 50) /\ Point(100, 0)
    )

    availableOperationsB shouldBe Set(
      Point(25, 25) /\ Point(0, 50)
    )

    (foldedCreasePatternB <~~ Point(25, 25) /\ Point(0, 50)).hasRemainingFolds shouldBe false

    new FoldSelection(
      (MediumComplexityCreasePattern /: List(
        Point(0.0, 100.0) \/ Point(50.0, 50.0),
        Point(0.0, 50.0) /\ Point(25.0, 25.0)
      ))(_ <~~ _).layers).getAvailableOperations shouldBe empty

    availableOperationsC should be(empty)
  }
}
