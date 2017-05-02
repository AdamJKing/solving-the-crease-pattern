package uk.ac.aber.adk15.paper.fold

import org.mockito.BDDMockito._
import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.geometry.Line
import uk.ac.aber.adk15.paper.{CreasePattern, PaperLayer}

class FoldContextSpec extends CommonFlatSpec {

  "The fold context for a mountain fold" should "correctly identify fold layers" in {
    // given
    val creasePattern   = mock[CreasePattern]
    val affectedLayer   = mock[PaperLayer]
    val unaffectedLayer = mock[PaperLayer]
    val fold            = Fold(mock[Line], MountainFold)

    given(creasePattern.layers) willReturn List(affectedLayer, unaffectedLayer)

    given(affectedLayer contains fold) willReturn true
    given(affectedLayer coversLine fold.line) willReturn false

    given(unaffectedLayer contains fold) willReturn false
    given(unaffectedLayer coversLine fold.line) willReturn true

    val (layerToFold, layerToLeave) = (mock[PaperLayer], mock[PaperLayer])
    given(affectedLayer segmentOnLine fold.line) willReturn ((Some(layerToFold),
                                                              Some(layerToLeave)))
    given(layerToFold.surfaceArea) willReturn 10
    given(layerToLeave.surfaceArea) willReturn 20

    // then
    val foldContext = new FoldContext(creasePattern, fold)

    // when
    foldContext.foldableLayers shouldBe Map(
      0 -> layerToFold
    )

    foldContext.unaffectedLayers shouldBe Map(
      1 -> unaffectedLayer,
      0 -> layerToLeave
    )
  }

  "The fold context for a valley fold" should "correctly identify fold layers" in {
    // given
    val creasePattern   = mock[CreasePattern]
    val affectedLayer   = mock[PaperLayer]
    val unaffectedLayer = mock[PaperLayer]
    val fold            = Fold(mock[Line], MountainFold)

    given(creasePattern.layers) willReturn List(affectedLayer, unaffectedLayer)

    given(affectedLayer contains fold) willReturn true
    given(affectedLayer coversLine fold.line) willReturn false

    given(unaffectedLayer contains fold) willReturn false
    given(unaffectedLayer coversLine fold.line) willReturn true

    val (layerToFold, layerToLeave) = (mock[PaperLayer], mock[PaperLayer])
    given(affectedLayer segmentOnLine fold.line) willReturn ((Some(layerToFold),
                                                              Some(layerToLeave)))
    given(layerToFold.surfaceArea) willReturn 10
    given(layerToLeave.surfaceArea) willReturn 20

    // then
    val foldContext = new FoldContext(creasePattern, fold)

    // when
    foldContext.foldableLayers shouldBe Map(
      0 -> layerToFold
    )

    foldContext.unaffectedLayers shouldBe Map(
      1 -> unaffectedLayer,
      0 -> layerToLeave
    )
  }
}
