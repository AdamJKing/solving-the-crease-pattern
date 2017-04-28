package uk.ac.aber.adk15.paper.newapi

import org.mockito.BDDMockito.given
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify}
import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.geometry.{Line, Point, Polygon}
import uk.ac.aber.adk15.paper.newapi.NewPaperLayer._
import uk.ac.aber.adk15.paper.{CreasedFold, FoldType, MountainFold, ValleyFold}

class NewPaperLayerSpec extends CommonFlatSpec {

  "Segmenting a paper layer into two parts" should "yield the proper results" in {
    // given
    val segmentLine = mock[Line]

    val leftFoldMap     = Map(segmentLine -> MountainFold)
    val rightFoldMap    = Map(segmentLine -> MountainFold)
    val originalFoldMap = mockFoldMap

    val anyPredicate = any[((Line, FoldType)) => Boolean]
    given(originalFoldMap partition anyPredicate) willReturn ((leftFoldMap, rightFoldMap))

    val originalShape = mock[Polygon]
    given(originalShape overlaps segmentLine.a) willReturn true
    given(originalShape overlaps segmentLine.b) willReturn true

    val leftShape = mock[Polygon]
    given(leftShape compareTo segmentLine) willReturn -1

    val rightShape = mock[Polygon]
    given(rightShape compareTo segmentLine) willReturn 1

    given(originalShape slice segmentLine) willReturn ((leftShape, rightShape))

    val paperLayer = NewPaperLayer(Set(originalShape), originalFoldMap)

    // when
    val (leftSide, rightSide) = paperLayer segmentOnLine segmentLine

    // then
    leftSide shouldBe NewPaperLayer(Set(leftShape), leftFoldMap)
    rightSide shouldBe NewPaperLayer(Set(rightShape), rightFoldMap)
  }

  "Rotating over a given axis" should "should yield expected results" in {
    // given
    val rotationLine  = mock[Line]
    val pointToRotate = mock[Point]
    val lineToRotate  = Line(pointToRotate, mock[Point])
    val folds         = Map(lineToRotate -> MountainFold)
    val paperLayer    = NewPaperLayer(Set(Polygon(lineToRotate.a, lineToRotate.b)), folds)

    given(lineToRotate.a reflectedOver rotationLine) willReturn lineToRotate.a
    given(lineToRotate.b reflectedOver rotationLine) willReturn lineToRotate.b

    // when
    val rotatedPaperlayer = paperLayer rotateAround rotationLine

    // then
    verify(pointToRotate, times(2)) reflectedOver rotationLine
    rotatedPaperlayer.valleyFolds should contain(lineToRotate)
    rotatedPaperlayer.mountainFolds should not contain lineToRotate
  }

  "Merging two correct layers" should "yield the expected results" in {
    // given
    val newShape            = mock[Polygon]
    val newPoint            = mock[Point]
    val nonOverlappingShape = mock[Polygon]

    given(newShape overlaps nonOverlappingShape) willReturn false
    given(nonOverlappingShape.points) willReturn Set(newPoint)
    given(newShape isOnEdge newPoint) willReturn false

    val oldFolds    = mockFoldMap
    val newFolds    = mockFoldMap
    val mergedFolds = mockFoldMap

    given(oldFolds ++ newFolds) willReturn mergedFolds

    val oldPaperLayer = new NewPaperLayer(Set(nonOverlappingShape), oldFolds)
    val newPaperLayer = new NewPaperLayer(Set(newShape), newFolds)

    // when
    val mergedPaperLayer = oldPaperLayer mergeWith newPaperLayer

    // then
    verify(newShape) overlaps nonOverlappingShape
    verify(nonOverlappingShape).points
    verify(newShape) isOnEdge newPoint

    mergedPaperLayer shouldBe defined
    mergedPaperLayer should contain(
      NewPaperLayer(Set(nonOverlappingShape, newShape), mergedFolds)
    )
  }

  "Merging two incompatible layers" should "fail to merge" in {
    // given
    val newShape         = mock[Polygon]
    val newPoint         = mock[Point]
    val overlappingShape = mock[Polygon]

    given(newShape overlaps overlappingShape) willReturn true
    given(overlappingShape.points) willReturn Set(newPoint)
    given(newShape isOnEdge newPoint) willReturn true

    val oldPaperLayer = new NewPaperLayer(Set(overlappingShape), mockFoldMap)
    val newPaperLayer = new NewPaperLayer(Set(newShape), mockFoldMap)

    // when
    val mergedPaperLayer = oldPaperLayer mergeWith newPaperLayer

    // then
    verify(newShape) overlaps overlappingShape
    verify(overlappingShape).points
    verify(newShape) isOnEdge newPoint

    mergedPaperLayer should not be defined
  }

  "Merging two layers where the points overlap" should "fail to merge" in {
    // given
    val newShape         = mock[Polygon]
    val newPoint         = mock[Point]
    val overlappingShape = mock[Polygon]

    given(newShape overlaps overlappingShape) willReturn false
    given(overlappingShape.points) willReturn Set(newPoint)
    given(newShape isOnEdge newPoint) willReturn true

    val oldFolds    = mockFoldMap
    val newFolds    = mockFoldMap
    val mergedFolds = mockFoldMap

    given(oldFolds ++ newFolds) willReturn mergedFolds

    val oldPaperLayer = new NewPaperLayer(Set(overlappingShape), oldFolds)
    val newPaperLayer = new NewPaperLayer(Set(newShape), newFolds)

    // when
    val mergedPaperLayer = oldPaperLayer mergeWith newPaperLayer

    // then
    verify(newShape) overlaps overlappingShape
    verify(overlappingShape).points
    verify(newShape) isOnEdge newPoint

    mergedPaperLayer should not be defined
  }

  "Calculating the surface area of a layer" should "yield the correct answers" in {
    // given
    val smallShape  = mock[Polygon]
    val largerShape = mock[Polygon]

    given(smallShape.surfaceArea) willReturn 10
    given(largerShape.surfaceArea) willReturn 100

    val paperLayer = NewPaperLayer(Set(smallShape, largerShape), mockFoldMap)

    // when
    val paperLayerSurfaceArea = paperLayer.surfaceArea

    // then
    paperLayerSurfaceArea should be(110)
  }

  "The paper layer" should "accurately report which folds are or aren't covered by it" in {
    // given
    val overlappingShape = mock[Polygon]
    val paperLayer       = NewPaperLayer(Set(overlappingShape), mockFoldMap)
    val coveredLine      = Line(mock[Point], mock[Point])
    val uncoveredLine    = Line(mock[Point], mock[Point])

    given(overlappingShape overlaps coveredLine.a) willReturn true
    given(overlappingShape overlaps coveredLine.b) willReturn true

    given(overlappingShape overlaps uncoveredLine.a) willReturn false
    given(overlappingShape overlaps uncoveredLine.b) willReturn false

    // when
    val overlapsCoveredLine   = paperLayer coversLine coveredLine
    val overlapsUncoveredLine = paperLayer coversLine uncoveredLine

    // then
    overlapsCoveredLine should be(true)
    overlapsUncoveredLine should be(false)
  }

  "Creasing all folds in the fold map" should "leave correct creases creased" in {
    // given
    val similarLine    = mock[Line]
    val line           = mock[Line]
    val dissimilarLine = mock[Line]

    val folds = Map(
      line           -> MountainFold,
      similarLine    -> MountainFold,
      dissimilarLine -> ValleyFold
    )

    given(line alignsWith similarLine) willReturn true
    given(line alignsWith line) willReturn true
    given(similarLine alignsWith line) willReturn true
    given(similarLine alignsWith similarLine) willReturn true

    // when
    val creasedFolds = folds creaseFoldsAlong line

    // then
    creasedFolds(line) shouldBe CreasedFold
    creasedFolds(similarLine) shouldBe CreasedFold
    creasedFolds(dissimilarLine) shouldBe folds(dissimilarLine)
  }

  private def mockFoldMap = mock[Map[Line, FoldType]]
}
