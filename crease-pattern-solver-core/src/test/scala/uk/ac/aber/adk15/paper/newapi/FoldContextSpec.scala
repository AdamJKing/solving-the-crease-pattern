package uk.ac.aber.adk15.paper.newapi

import org.mockito.BDDMockito._
import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.geometry.Line
import uk.ac.aber.adk15.paper.MountainFold

class FoldContextSpec extends CommonFlatSpec {

  "Fold context" should "correctly identify unaffected layers" in {
    // given
    val creasePattern = mock[NewCreasePattern]
    val layers        = List.fill(3)(mock[NewPaperLayer])
    val fold          = NewFold(mock[Line], MountainFold)

    given(creasePattern.layers) willReturn layers

    given(layers.head contains fold) willReturn false
    given(layers.head coversLine fold.line) willReturn true

    layers.tail foreach { layer =>
      given(layer contains fold) willReturn true
      given(layer coversLine fold.line) willReturn false
    }

    // when
    val foldContext = new FoldContext(creasePattern, fold)

    // then
    foldContext.unaffectedLayers shouldBe List(layers.head)
  }

  "Fold context" should "correctly identify which layers should be folded" in {
    // given
    val creasePattern   = mock[NewCreasePattern]
    val affectedLayer   = mock[NewPaperLayer]
    val unaffectedLayer = mock[NewPaperLayer]
    val fold            = NewFold(mock[Line], MountainFold)

    given(creasePattern.layers) willReturn List(affectedLayer, unaffectedLayer)

    given(affectedLayer contains fold) willReturn true
    given(affectedLayer coversLine fold.line) willReturn false

    given(unaffectedLayer contains fold) willReturn false
    given(unaffectedLayer coversLine fold.line) willReturn true

    val (layerToFold, layerToLeave) = (mock[NewPaperLayer], mock[NewPaperLayer])
    given(affectedLayer segmentOnLine fold.line) willReturn ((layerToFold, layerToLeave))
    given(layerToFold.surfaceArea) willReturn 10
    given(layerToLeave.surfaceArea) willReturn 20

    // then
    val foldContext = new FoldContext(creasePattern, fold)

    // when
    foldContext.layersToFold shouldBe List(layerToFold)
  }

  "Fold context" should "correctly identify which layers should be untouched by the fold" in {
    // given
    val creasePattern   = mock[NewCreasePattern]
    val affectedLayer   = mock[NewPaperLayer]
    val unaffectedLayer = mock[NewPaperLayer]
    val fold            = NewFold(mock[Line], MountainFold)

    given(creasePattern.layers) willReturn List(affectedLayer, unaffectedLayer)

    given(affectedLayer contains fold) willReturn true
    given(affectedLayer coversLine fold.line) willReturn false

    given(unaffectedLayer contains fold) willReturn false
    given(unaffectedLayer coversLine fold.line) willReturn true

    val (layerToFold, layerToLeave) = (mock[NewPaperLayer], mock[NewPaperLayer])
    given(affectedLayer segmentOnLine fold.line) willReturn ((layerToFold, layerToLeave))
    given(layerToFold.surfaceArea) willReturn 10
    given(layerToLeave.surfaceArea) willReturn 20

    // then
    val foldContext = new FoldContext(creasePattern, fold)

    // when
    foldContext.layersToLeave shouldBe List(layerToLeave)
  }
}
