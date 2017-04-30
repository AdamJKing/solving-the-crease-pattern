package uk.ac.aber.adk15.paper.newapi

import org.mockito.BDDMockito._
import org.mockito.Matchers.any
import org.mockito.Mockito._
import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.geometry.Line

class OngoingFoldSpec extends CommonFlatSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
  }

  "Creasing an on-going valley fold" should "correctly flatten the model" in {
    // given
    val foldContext         = mock[FoldContext]
    val expectedTopLayer    = mock[NewPaperLayer]
    val expectedBottomLayer = mock[NewPaperLayer]
    val foldLine            = mock[Line]

    given(expectedTopLayer rotateAround foldLine) willReturn expectedTopLayer
    given(foldContext.foldableLayers) willReturn Map(0   -> expectedTopLayer)
    given(foldContext.unaffectedLayers) willReturn Map(0 -> expectedBottomLayer)
    given(foldContext.foldLine) willReturn foldLine
    given(foldContext.foldAbove) willReturn true

    // when
    val creasePattern = new OngoingFold(foldContext).crease

    // then
    verify(expectedTopLayer) rotateAround foldLine
    creasePattern shouldBe (
      NewCreasePattern from (
        expectedTopLayer,
        expectedBottomLayer
      )
    )
  }

  "Creasing an on-going mountain fold" should "correctly flatten the model" in {
    // given
    val foldContext         = mock[FoldContext]
    val expectedTopLayer    = mock[NewPaperLayer]
    val expectedBottomLayer = mock[NewPaperLayer]
    val foldLine            = mock[Line]

    given(expectedBottomLayer rotateAround foldLine) willReturn expectedBottomLayer
    given(foldContext.foldableLayers) willReturn Map(0   -> expectedBottomLayer)
    given(foldContext.unaffectedLayers) willReturn Map(0 -> expectedTopLayer)
    given(foldContext.foldLine) willReturn foldLine
    given(foldContext.foldAbove) willReturn false
    given(expectedBottomLayer rotateAround foldLine) willReturn expectedBottomLayer

    // when
    val creasePattern = new OngoingFold(foldContext).crease

    // then
    verify(expectedBottomLayer) rotateAround foldLine
    creasePattern shouldBe (
      NewCreasePattern from (
        expectedTopLayer,
        expectedBottomLayer
      )
    )
  }

  "Creasing an on-going valley fold" should "correctly flatten the model with some unaffected layers" in {
    // given
    val foldContext             = mock[FoldContext]
    val expectedTopLayer        = mock[NewPaperLayer]("top")
    val expectedBottomLayer     = mock[NewPaperLayer]("bottom")
    val expectedUnaffectedLayer = mock[NewPaperLayer]("unaffected")
    val foldLine                = mock[Line]

    given(expectedTopLayer rotateAround foldLine) willReturn expectedTopLayer
    given(foldContext.foldableLayers) willReturn Map(0   -> expectedTopLayer)
    given(foldContext.unaffectedLayers) willReturn Map(0 -> expectedBottomLayer,
                                                       1 -> expectedUnaffectedLayer)
    given(foldContext.foldLine) willReturn foldLine
    given(foldContext.foldAbove) willReturn true

    given(expectedUnaffectedLayer mergeWith expectedTopLayer) willReturn None
    given(expectedUnaffectedLayer mergeWith expectedBottomLayer) willReturn None

    // when
    val creasePattern = new OngoingFold(foldContext).crease

    // then
    verify(expectedTopLayer) rotateAround foldLine
    creasePattern shouldBe (
      NewCreasePattern from (
        expectedTopLayer,
        expectedBottomLayer,
        expectedUnaffectedLayer
      )
    )
  }

  "Creasing an on-going mountain fold" should "correctly flatten the model with some unaffected layers" in {
    // given
    val foldContext             = mock[FoldContext]
    val expectedTopLayer        = mock[NewPaperLayer]
    val expectedBottomLayer     = mock[NewPaperLayer]
    val expectedUnaffectedLayer = mock[NewPaperLayer]
    val foldLine                = mock[Line]

    given(expectedBottomLayer rotateAround foldLine) willReturn expectedBottomLayer
    given(foldContext.foldableLayers) willReturn Map(1   -> expectedBottomLayer)
    given(foldContext.unaffectedLayers) willReturn Map(1 -> expectedTopLayer,
                                                       0 -> expectedUnaffectedLayer)
    given(foldContext.foldLine) willReturn foldLine
    given(foldContext.foldAbove) willReturn false

    given(expectedUnaffectedLayer mergeWith expectedTopLayer) willReturn None
    given(expectedUnaffectedLayer mergeWith expectedBottomLayer) willReturn None

    // when
    val creasePattern = new OngoingFold(foldContext).crease

    // then
    verify(expectedBottomLayer) rotateAround foldLine
    creasePattern shouldBe (
      NewCreasePattern from (
        expectedUnaffectedLayer,
        expectedTopLayer,
        expectedBottomLayer
      )
    )
  }

  "Creasing an on-going valley fold" should "correctly flatten the model with some merges" in {
    // given
    val foldContext         = mock[FoldContext]
    val expectedTopLayer    = mock[NewPaperLayer]
    val expectedBottomLayer = mock[NewPaperLayer]
    val foldLine            = mock[Line]

    val unaffectedMergeableLayer = mock[NewPaperLayer]
    val mergedLayer              = mock[NewPaperLayer]

    given(expectedTopLayer rotateAround foldLine) willReturn expectedTopLayer
    given(foldContext.foldableLayers) willReturn Map(1   -> expectedTopLayer)
    given(foldContext.unaffectedLayers) willReturn Map(1 -> expectedBottomLayer,
                                                       0 -> unaffectedMergeableLayer)
    given(foldContext.foldLine) willReturn foldLine
    given(foldContext.foldAbove) willReturn true

    given(expectedTopLayer rotateAround foldLine) willReturn expectedTopLayer

    given(unaffectedMergeableLayer mergeWith expectedTopLayer) willReturn Some(mergedLayer)
    given(unaffectedMergeableLayer mergeWith expectedBottomLayer) willReturn None

    // when
    val creasePattern = new OngoingFold(foldContext).crease

    // then
    verify(expectedTopLayer) rotateAround foldLine
    creasePattern shouldBe (
      NewCreasePattern from (
        mergedLayer,
        expectedBottomLayer
      )
    )
  }

  "Creasing an on-going mountain fold" should "correctly flatten the model with some merges" in {
    // given
    val foldContext         = mock[FoldContext]
    val expectedTopLayer    = mock[NewPaperLayer]("top layer")
    val expectedBottomLayer = mock[NewPaperLayer]("bottom layer")
    val foldLine            = mock[Line]

    val unaffectedMergeableLayer = mock[NewPaperLayer]
    val mergedLayer              = mock[NewPaperLayer]("merged layer")

    given(expectedBottomLayer rotateAround foldLine) willReturn expectedBottomLayer
    given(foldContext.foldableLayers) willReturn Map(0   -> expectedBottomLayer)
    given(foldContext.unaffectedLayers) willReturn Map(0 -> expectedTopLayer,
                                                       1 -> unaffectedMergeableLayer)
    given(foldContext.foldLine) willReturn foldLine
    given(foldContext.foldAbove) willReturn false

    given(expectedTopLayer rotateAround foldLine) willReturn expectedTopLayer

    given(unaffectedMergeableLayer mergeWith expectedBottomLayer) willReturn Some(mergedLayer)
    given(unaffectedMergeableLayer mergeWith expectedTopLayer) willReturn None

    // when
    val creasePattern = new OngoingFold(foldContext).crease

    // then
    verify(expectedBottomLayer) rotateAround foldLine
    creasePattern shouldBe (
      NewCreasePattern from (
        expectedTopLayer,
        mergedLayer
      )
    )
  }

  "Creasing an on-going valley fold" should "correctly flatten the model with multi-layer merges" in {
    // given
    val foldContext = mock[FoldContext]
    val foldLine    = mock[Line]

    val foldableLayers = {
      for (i <- 5 until 10) yield i -> mock[NewPaperLayer]("foldable")
    }.toMap

    val unaffectedLayers = {
      for (i <- 0 until 6) yield i -> mock[NewPaperLayer]("unaffected")
    }.toMap

    val mergedLayers = List.fill(5)(mock[NewPaperLayer]("merged"))

    given(foldContext.foldableLayers) willReturn foldableLayers
    given(foldContext.unaffectedLayers) willReturn unaffectedLayers
    given(foldContext.foldLine) willReturn foldLine
    given(foldContext.foldAbove) willReturn true

    unaffectedLayers.values foreach (layer =>
      given(layer mergeWith any[NewPaperLayer]) willReturn None)

    foldableLayers.values foreach (layer => given(layer rotateAround foldLine) willReturn layer)

    for (i <- 0 until 5) {
      given(unaffectedLayers(i) mergeWith foldableLayers(9 - i)) willReturn Some(mergedLayers(i))
    }

    // when
    val creasePattern = new OngoingFold(foldContext).crease

    // then
    foldableLayers.values foreach (layer => verify(layer) rotateAround foldLine)
    creasePattern shouldBe (
      NewCreasePattern from (
        mergedLayers :+ unaffectedLayers(5): _*
      )
    )
  }

  "Creasing an on-going mountain fold" should "correctly flatten the model with multi-layer merges" in {
    // given
    val foldContext = mock[FoldContext]
    val foldLine    = mock[Line]

    val foldableLayers = {
      for (i <- 0 until 5) yield i -> mock[NewPaperLayer](s"foldable $i")
    }.toMap

    val unaffectedLayers = {
      for (i <- 4 until 10) yield i -> mock[NewPaperLayer](s"unaffected $i")
    }.toMap

    val mergedLayers = List.fill(5)(mock[NewPaperLayer]("merged"))

    given(foldContext.foldableLayers) willReturn foldableLayers
    given(foldContext.unaffectedLayers) willReturn unaffectedLayers
    given(foldContext.foldLine) willReturn foldLine
    given(foldContext.foldAbove) willReturn false

    unaffectedLayers.values foreach (layer =>
      given(layer mergeWith any[NewPaperLayer]) willReturn None)

    foldableLayers.values foreach (layer => given(layer rotateAround foldLine) willReturn layer)

    for (i <- 0 until 5) {
      given(unaffectedLayers(i + 5) mergeWith foldableLayers(4 - i)) willReturn Some(
        mergedLayers(i))
    }

    // when
    val creasePattern = new OngoingFold(foldContext).crease

    // then
    foldableLayers.values foreach (layer => verify(layer) rotateAround foldLine)
    creasePattern shouldBe (
      NewCreasePattern from (
        unaffectedLayers(4) +: mergedLayers: _*
      )
    )
  }
}
