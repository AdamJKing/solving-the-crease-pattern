package uk.ac.aber.adk15.paper

import org.mockito.BDDMockito._
import org.mockito.Matchers._
import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._

class CreasePatternSpec extends CommonFlatSpec {

  "Crease patterns with the same edges" should "equal each other" in {
    // given
    val first = CreasePattern from (
      PaperLayer from (
        Point(0, 100) ~~ Point(100, 0),
        Point(0, 0) -- Point(0, 100),
        Point(100, 0) -- Point(0, 0)
      ),
      PaperLayer from (
        Point(0, 100) ~~ Point(100, 0),
        Point(0, 0) -- Point(0, 100),
        Point(100, 0) -- Point(0, 0)
      )
    )

    val second = CreasePattern from (
      PaperLayer from (
        Point(0.0, 100.0) ~~ Point(100.0, 0.0),
        Point(0.0, 0.0) -- Point(0.0, 100.0),
        Point(100.0, 0.0) -- Point(0.0, 0.0)
      ),
      PaperLayer from (
        Point(0.0, 100.0) ~~ Point(100.0, 0.0),
        Point(0.0, 100.0) -- Point(0.0, 0.0),
        Point(0.0, 0.0) -- Point(100.0, 0.0)
      )
    )

    // then
    first should equal(second)
    first.hashCode() should equal(second.hashCode())
  }

  "Repairing two layers from a mountain fold when no merge is needed" should "yield the proper results" in {
    // given
    val firstLayers  = List.range(0, 10) map (i => mock[PaperLayer](s"FirstLayer$i"))
    val secondLayers = List.range(0, 10) map (i => mock[PaperLayer](s"SecondLayer$i"))
    val healedLayers = List.range(0, 10) map (i => mock[PaperLayer](s"HealedLayer$i"))

    for (i <- 0 until 10) {
      given(firstLayers(i) mergeWith secondLayers(i)) willReturn Some(healedLayers(i))
    }

    // when
    val result = CreasePattern.repair(firstLayers, secondLayers, MountainFold)

    // then
    result should be(healedLayers)
  }

  "Repairing two layers from a valley fold when no merge is needed" should "yield the proper results" in {
    // given
    val firstLayers  = List.range(0, 10) map (i => mock[PaperLayer](s"FirstLayer$i"))
    val secondLayers = List.range(0, 10) map (i => mock[PaperLayer](s"SecondLayer$i"))
    val healedLayers = List.range(0, 10) map (i => mock[PaperLayer](s"HealedLayer$i"))

    for (i <- 0 until 10) {
      given(firstLayers(i) mergeWith secondLayers(i)) willReturn Some(healedLayers(i))
    }

    // when
    val result = CreasePattern.repair(firstLayers, secondLayers, ValleyFold)

    // then
    result should be(healedLayers)
  }

  "Repair two layers from a mountain fold when a shift is needed" should "yield the proper results" in {
    // given
    val firstLayers  = List.range(0, 10) map (i => mock[PaperLayer](s"FirstLayer$i"))
    val secondLayers = List.range(0, 6) map (i => mock[PaperLayer](s"SecondLayer$i"))
    val healedLayers = List.range(0, 3) map (i => mock[PaperLayer](s"HealedLayer$i"))

    for (i <- 0 until 10) given(firstLayers(i) mergeWith any[PaperLayer]) willReturn None
    for (i <- 7 until 10)
      given(firstLayers(i) mergeWith secondLayers(i - 7)) willReturn Some(healedLayers(i - 7))

    // when
    val result = CreasePattern.repair(firstLayers, secondLayers, MountainFold)

    // then
    result should be((firstLayers dropRight 3) ++ healedLayers ++ (secondLayers drop 3))
  }

  "Repair two layers from a valley fold when a shift is needed" should "yield the proper results" in {
    // given
    val firstLayers  = List.range(0, 10) map (i => mock[PaperLayer](s"FirstLayer$i"))
    val secondLayers = List.range(0, 10) map (i => mock[PaperLayer](s"SecondLayer$i"))
    val healedLayers = List.range(0, 5) map (i => mock[PaperLayer](s"HealedLayer$i"))

    for (i <- 0 until 10) given(firstLayers(i) mergeWith any[PaperLayer]) willReturn None
    for (i <- 0 until 5)
      given(firstLayers(i) mergeWith secondLayers(i + 5)) willReturn Some(healedLayers(i))

    // when
    val result = CreasePattern.repair(firstLayers, secondLayers, ValleyFold)

    // then
    result should be((secondLayers dropRight 5) ++ healedLayers ++ (firstLayers drop 5))
  }
}
