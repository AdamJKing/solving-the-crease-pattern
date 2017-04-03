package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._

class PaperLayerSpec extends CommonFlatSpec {

  /**
    * 0,0 -------- 50,0 -------- 100,0
    *  |             |             |
    *  |             |             |
    *  |             |             |
    *  |             |             |
    *  |             |             |
    * 0,50 ------- 50,50 ------- 100,50
    *  |             |             |
    *  |             |             |
    *  |             |             |
    *  |             |             |
    *  |             |             |
    * 0,100 ------ 50,100 ----- 100,100
    */
  private val paperLayer = PaperLayer(
    Seq(
      Point(0, 0) -- Point(50, 0),
      Point(50, 0) -- Point(100, 0),
      Point(100, 0) -- Point(100, 50),
      Point(100, 50) -- Point(100, 100),
      Point(100, 100) -- Point(50, 100),
      Point(50, 100) -- Point(0, 100),
      Point(0, 100) -- Point(0, 50),
      Point(0, 50) -- Point(0, 0),
      Point(50, 0) /\ Point(50, 50),
      Point(50, 50) /\ Point(50, 100),
      Point(0, 50) \/ Point(50, 50),
      Point(50, 50) \/ Point(100, 50)
    ))

  it should "allow for layers to be split over a vertical line" in {
    // when
    val (left, right) = paperLayer segmentOnLine (Point(50, 0), Point(50, 100))

    // then

    withClue("Left was wrong: ")(
      left should be(PaperLayer(Seq(
        Point(0, 0) -- Point(50, 0),
        Point(50, 100) -- Point(0, 100),
        Point(0, 100) -- Point(0, 50),
        Point(0, 50) -- Point(0, 0),
        Point(50, 0) ~~ Point(50, 50),
        Point(50, 50) ~~ Point(50, 100),
        Point(0, 50) \/ Point(50, 50)
      ))))

    withClue("Right was wrong: ")(
      right should be(PaperLayer(Seq(
        Point(50, 0) -- Point(100, 0),
        Point(100, 0) -- Point(100, 50),
        Point(100, 50) -- Point(100, 100),
        Point(100, 100) -- Point(50, 100),
        Point(50, 0) ~~ Point(50, 50),
        Point(50, 50) ~~ Point(50, 100),
        Point(50, 50) \/ Point(100, 50)
      ))))
  }

  it should "allow for layers to be split over a horizontal line" in {
    // when
    val (bottom, top) = paperLayer segmentOnLine (Point(0, 50), Point(100, 50))

    // then

    withClue("Top was wrong: ")(
      top should be(PaperLayer(Seq(
        Point(0, 0) -- Point(50, 0),
        Point(50, 0) -- Point(100, 0),
        Point(100, 0) -- Point(100, 50),
        Point(0, 50) -- Point(0, 0),
        Point(50, 0) /\ Point(50, 50),
        Point(0, 50) ~~ Point(50, 50),
        Point(50, 50) ~~ Point(100, 50)
      ))))

    withClue("Bottom was wrong: ")(
      bottom should be(PaperLayer(Seq(
        Point(100, 50) -- Point(100, 100),
        Point(100, 100) -- Point(50, 100),
        Point(50, 100) -- Point(0, 100),
        Point(0, 100) -- Point(0, 50),
        Point(50, 50) /\ Point(50, 100),
        Point(0, 50) ~~ Point(50, 50),
        Point(50, 50) ~~ Point(100, 50)
      ))))
  }

  it should "allow for layers to be split over an arbitrary line" in {
    // when
    val (lowerRight, upperLeft) = paperLayer segmentOnLine (Point(0, 100), Point(100, 0))

    withClue("Upper Left was wrong: ")(
      upperLeft should be(
        PaperLayer(Seq(
          Point(0, 0) -- Point(50, 0),
          Point(50, 0) -- Point(100, 0),
          Point(0, 50) -- Point(0, 0),
          Point(0, 100) -- Point(0, 50),
          Point(50, 0) /\ Point(50, 50),
          Point(0, 50) \/ Point(50, 50)
        )))
    )

    withClue("Lower Right was wrong: ")(
      lowerRight should be(
        PaperLayer(Seq(
          Point(100, 0) -- Point(100, 50),
          Point(100, 50) -- Point(100, 100),
          Point(100, 100) -- Point(50, 100),
          Point(50, 100) -- Point(0, 100),
          Point(50, 50) /\ Point(50, 100),
          Point(50, 50) \/ Point(100, 50)
        )))
    )
  }

  "Rotating over a given axis" should "should yield expected results" in {
    // given
    val testLayer = PaperLayer(
      Seq(
        Point(0, 0) /\ Point(50, 50),
        Point(0, 50) /\ Point(25, 75)
      ))

    // when
    val rotated = testLayer rotateAround (Point(0, 100), Point(100, 0))

    // then
    rotated should be(
      PaperLayer(
        Seq(
          Point(100, 100) \/ Point(50, 50),
          Point(50, 100) \/ Point(25, 75)
        )))
  }

  "Merging two correct yet aligned layers" should "yield the expected results" in {
    // given

    /**      Layer A                    Layer B
      * 0,0 ----x--- 50,0         50,0 ----x--- 100,0
      *  |             |            |             |
      *  |             |            |             |
      *  x             x            |             x
      *  |             |            |             |
      *  |             |            |             |
      * 0,50         50,50        50,50         100,50
      *  |             |            |             |
      *  |             |            |             |
      *  x             x            |             x
      *  |             |            |             |
      *  |             |            |             |
      * 0,100 --x--- 50,100       50,100 --x-- 100,100
      */
    val validLayerA = PaperLayer(
      Seq(
        Point(0, 0) -- Point(50, 0),
        Point(0, 100) -- Point(50, 100),
        Point(0, 0) -- Point(0, 50),
        Point(0, 50) -- Point(0, 100),
        Point(50, 0) -- Point(50, 50),
        Point(50, 50) -- Point(50, 100)
      ))

    val validLayerB = PaperLayer(
      Seq(
        Point(50, 0) -- Point(100, 0),
        Point(50, 100) -- Point(100, 100),
        Point(50, 0) -- Point(50, 50),
        Point(50, 50) -- Point(50, 100),
        Point(100, 0) -- Point(100, 50),
        Point(100, 50) -- Point(100, 100)
      ))

    // when
    val merged = validLayerA mergeWith validLayerB

    // then
    merged shouldBe defined
    merged should contain(
      PaperLayer(Seq(
        Point(0.0, 0.0) -- Point(50.0, 0.0),
        Point(0.0, 100.0) -- Point(50.0, 100.0),
        Point(0.0, 0.0) -- Point(0.0, 50.0),
        Point(0.0, 50.0) -- Point(0.0, 100.0),
        Point(50.0, 0.0) -- Point(50.0, 50.0),
        Point(50.0, 50.0) -- Point(50.0, 100.0),
        Point(50.0, 0.0) -- Point(100.0, 0.0),
        Point(50.0, 100.0) -- Point(100.0, 100.0),
        Point(100.0, 0.0) -- Point(100.0, 50.0),
        Point(100.0, 50.0) -- Point(100.0, 100.0)
      )))
  }

  "Merging two incompatible layers" should "fail to merge" in {
    // given

    /**      Layer A                    Layer B
      * 0,0 ----x--- 50,0
      *  |             |
      *  |             |
      *  x             x           0,25 -------- 50,25 --------- 100,25
      *  |             |            |                               |
      *  |             |            |                               |
      * 0,50 ---x--- 50,50         0,50                          100,50
      *  |             |            |                               |
      *  |             |            |                               |
      *  x             x           0,75 -------- 50,75 --------- 100,75
      *  |             |
      *  |             |
      * 0,100 --x--- 50,100
      */
    val validLayerA = PaperLayer(
      Seq(
        Point(0, 0) -- Point(50, 0),
        Point(0, 50) -- Point(50, 50),
        Point(0, 100) -- Point(50, 100),
        Point(0, 0) -- Point(0, 50),
        Point(0, 50) -- Point(0, 100),
        Point(50, 0) -- Point(50, 50),
        Point(50, 50) -- Point(50, 100)
      ))

    val validLayerB = PaperLayer(
      Seq(
        Point(0, 25) -- Point(50, 25),
        Point(50, 25) -- Point(100, 25),
        Point(100, 25) -- Point(100, 50),
        Point(100, 50) -- Point(100, 75),
        Point(100, 75) -- Point(50, 75),
        Point(50, 75) -- Point(0, 75),
        Point(0, 75) -- Point(0, 50),
        Point(0, 50) -- Point(0, 25)
      ))

    // when
    val merged = validLayerA mergeWith validLayerB

    // then
    merged should not be defined
  }

  "Calculating the surface area of a layer" should "yield the correct answers" in {
    // given
    val square = PaperLayer(
      Seq(Point(0, 0) -- Point(0, 10),
          Point(0, 10) -- Point(10, 10),
          Point(10, 10) -- Point(10, 0),
          Point(10, 0) -- Point(0, 0))
    )

    val triangle = PaperLayer(
      Seq(Point(0, 0) -- Point(0, 10), Point(0, 10) -- Point(10, 10), Point(10, 10) -- Point(0, 0))
    )

    // when
    val squareSurfaceArea   = square.surfaceArea()
    val triangleSurfaceArea = triangle.surfaceArea()

    squareSurfaceArea should be(100)
    triangleSurfaceArea should be(50)
  }

  "Merging layers where the points align incorrectly" should "still refuse to merge the layers" in {
    // given
    val diamondA = PaperLayer(
      Seq(
        Point(0, 50) -- Point(50, 0),
        Point(50, 0) -- Point(100, 50),
        Point(100, 50) -- Point(50, 100),
        Point(50, 100) -- Point(0, 50)
      )
    )

    val diamondB = PaperLayer(
      Seq(
        Point(10, 60) -- Point(60, 10),
        Point(60, 10) -- Point(100, 50),
        Point(100, 50) -- Point(50, 100),
        Point(50, 100) -- Point(10, 60)
      )
    )

    // when
    val result = diamondA mergeWith diamondB

    // then
    result should not be defined
  }
}
