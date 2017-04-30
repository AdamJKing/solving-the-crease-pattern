package uk.ac.aber.adk15.paper.newapi

import org.mockito.BDDMockito._
import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.geometry.{Line, Point}
import uk.ac.aber.adk15.paper.{CreasedFold, ValleyFold}

class FoldSelectionSpec extends CommonFlatSpec {

  "Creating a fold selection with one layer" should "accurately report if a fold is available" in {
    // given
    val availableFold = mock[NewFold]
    val layer         = mock[NewPaperLayer]
    val foldSelection = new FoldSelection(List(layer))

    given(layer.valleyFolds) willReturn Set(availableFold)
    given(layer.mountainFolds) willReturn Set.empty[NewFold]

    // when
    val availableOperations = foldSelection.getAvailableOperations

    // then
    availableOperations should contain(availableFold)
  }

  "Creating a fold selection with multiple layers" should "accurately report if a fold is available" in {
    // given
    val availableFold = mock[NewFold]
    val layers        = List.fill(5)(mock[NewPaperLayer])
    val foldSelection = new FoldSelection(layers)

    layers foreach (layer => {
      given(layer.valleyFolds) willReturn Set(availableFold)
      given(layer.mountainFolds) willReturn Set.empty[NewFold]
      given(layer.creasedFolds) willReturn Set.empty[NewFold]
    })

    // when
    val availableOperations = foldSelection.getAvailableOperations

    // then
    availableOperations should contain(availableFold)
  }

  "Creating a fold selection with multiple layers and a corresponding creased fold plus positive test continuity" should "accurately report available folds" in {
    // given
    val intersection        = mock[Point]
    val availableFold       = NewFold(mock[Line], ValleyFold)
    val correspondingCrease = NewFold(mock[Line], CreasedFold)
    val layers              = List.fill(5)(mock[NewPaperLayer])
    val foldSelection       = new FoldSelection(layers)

    given(correspondingCrease.line intersectWith availableFold.line) willReturn Some(intersection)

    layers foreach (layer => {
      given(layer.valleyFolds) willReturn Set(availableFold)
      given(layer.mountainFolds) willReturn Set.empty[NewFold]
      given(layer.creasedFolds) willReturn Set(correspondingCrease)
      given(layer coversPoint intersection) willReturn true
      given(layer.surfaceArea) willReturn 10
      given(layer contains availableFold.line) willReturn true
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
    val availableFold = NewFold(mock[Line], ValleyFold)
    val layers        = List.fill(5)(mock[NewPaperLayer])
    val foldSelection = new FoldSelection(layers)

    layers foreach (layer => {
      given(layer.valleyFolds) willReturn Set(availableFold)
      given(layer.mountainFolds) willReturn Set.empty[NewFold]

      val correspondingCrease = NewFold(mock[Line], CreasedFold)
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
    val availableFold = mock[NewFold]
    val layers        = List.fill(5)(mock[NewPaperLayer])
    val foldSelection = new FoldSelection(layers)

    layers.tail foreach (layer => {
      given(layer.valleyFolds) willReturn Set(availableFold)
      given(layer.mountainFolds) willReturn Set.empty[NewFold]
      given(layer.creasedFolds) willReturn Set.empty[NewFold]
      given(layer.surfaceArea) willReturn 10
    })

    given(layers.head.valleyFolds) willReturn Set.empty[NewFold]
    given(layers.head.mountainFolds) willReturn Set.empty[NewFold]
    given(layers.head.creasedFolds) willReturn Set.empty[NewFold]
    given(layers.head.surfaceArea) willReturn 5

    // when
    val availableOperations = foldSelection.getAvailableOperations

    // then
    availableOperations should contain(availableFold)
  }
}
