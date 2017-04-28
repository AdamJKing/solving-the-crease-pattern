package uk.ac.aber.adk15.paper.newapi

import org.mockito.BDDMockito._
import org.mockito.Matchers._
import org.mockito.Mockito.verify
import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.geometry.Line
import uk.ac.aber.adk15.paper.{MountainFold, ValleyFold}

class NewCreasePatternSpec extends CommonFlatSpec {

  "Crease patterns with the same edges" should "equal each other" in {
    // given
    val paperLayer         = mock[NewPaperLayer]
    val creasePattern      = NewCreasePattern(List(paperLayer))
    val otherCreasePattern = NewCreasePattern(List(paperLayer))

    // then
    creasePattern shouldBe otherCreasePattern
  }

  "Repairing two layers from a mountain fold when no merge is needed" should "yield the proper results" in {
    // given
    val paperLayer            = mock[NewPaperLayer]
    val creasePattern         = NewCreasePattern(List(paperLayer))
    val (leftSide, rightSide) = (mock[NewPaperLayer], mock[NewPaperLayer])
    val fold                  = mock[NewFold]
    val foldLine              = mock[Line]

    given(fold.line) willReturn foldLine
    given(paperLayer contains fold) willReturn true
    given(paperLayer segmentOnLine foldLine) willReturn ((leftSide, rightSide))
    given(rightSide.surfaceArea) willReturn 10
    given(leftSide.surfaceArea) willReturn 20
    given(leftSide rotateAround foldLine) willReturn leftSide
    given(leftSide mergeWith rightSide) willReturn None

    // when
    val creased = creasePattern crease fold

    // then
    verify(leftSide) rotateAround foldLine
    creased shouldBe NewCreasePattern(List(leftSide, rightSide))
  }

  "Repairing two layers from a valley fold when no merge is needed" should "yield the proper results" in {}

  "Repair two layers from a mountain fold when a shift is needed" should "yield the proper results" in {
    // given
    val firstLayers  = List.range(0, 10) map (i => mock[NewPaperLayer](s"FirstLayer$i"))
    val secondLayers = List.range(0, 6) map (i => mock[NewPaperLayer](s"SecondLayer$i"))
    val healedLayers = List.range(0, 3) map (i => mock[NewPaperLayer](s"HealedLayer$i"))

    for (i <- 0 until 10) given(firstLayers(i) mergeWith any[NewPaperLayer]) willReturn None
    for (i <- 7 until 10)
      given(firstLayers(i) mergeWith secondLayers(i - 7)) willReturn Some(healedLayers(i - 7))

    // when
    val result = NewCreasePattern.repair(firstLayers, secondLayers, MountainFold)

    // then
    result should be((firstLayers dropRight 3) ++ healedLayers ++ (secondLayers drop 3))
  }

  "Repair two layers from a valley fold when a shift is needed" should "yield the proper results" in {
    // given
    val firstLayers  = List.range(0, 10) map (i => mock[NewPaperLayer](s"FirstLayer$i"))
    val secondLayers = List.range(0, 10) map (i => mock[NewPaperLayer](s"SecondLayer$i"))
    val healedLayers = List.range(0, 5) map (i => mock[NewPaperLayer](s"HealedLayer$i"))

    for (i <- 0 until 10) given(firstLayers(i) mergeWith any[NewPaperLayer]) willReturn None
    for (i <- 0 until 5)
      given(firstLayers(i) mergeWith secondLayers(i + 5)) willReturn Some(healedLayers(i))

    // when
    val result = NewCreasePattern.repair(firstLayers, secondLayers, ValleyFold)

    // then
    result should be((secondLayers dropRight 5) ++ healedLayers ++ (firstLayers drop 5))
  }
}
