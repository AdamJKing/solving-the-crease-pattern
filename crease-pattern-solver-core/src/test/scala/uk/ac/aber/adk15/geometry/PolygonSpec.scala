package uk.ac.aber.adk15.geometry

import uk.ac.aber.adk15.CommonFlatSpec

/**
  * Tests for [[Polygon]]
  */
class PolygonSpec extends CommonFlatSpec {

  private val RectangularPolygon = Rectangle(Point(0, 20), Point(50, 0))
  private val IrregularPolygon   = createIrregularPolygon

  "When slicing a Rectangle" should "correctly cut the rectangle along the given line" in {
    // given
    val line = Line(Point(0, 20), Point(50, 0))

    // when
    val (leftSide, rightSide) = RectangularPolygon slice line

    // then
    leftSide shouldBe Polygon(Point(0, 20), Point(0, 0), Point(50, 0))
    rightSide shouldBe Polygon(Point(0, 20), Point(50, 0), Point(50, 20))
  }

  "When slicing an irregular polygon" should "correctly cut the polgyon along the given line" in {
    // given
    val line = Line(Point(6, 4), Point(3, 1))

    // when
    val (leftSide, rightSide) = IrregularPolygon slice line

    // then
    leftSide shouldBe Polygon(Point(6, 4), Point(4, 7), Point(2, 5), Point(1, 3), Point(3, 1))
    rightSide shouldBe Polygon(Point(6, 4), Point(3, 1), Point(6, 0), Point(8, 3))
  }

  "Comparing a shape to a line" should "correctly identify which side of the line it is on" in {
    // given
    val leftwardsLine  = Line(Point(3, 0), Point(0, 2))
    val rightwardsLine = Line(Point(12, 0), Point(0, 12))
    val centralLine    = Line(Point(8, 0), Point(0, 6))

    val (left, centre, right) = (-1, 0, 1)

    // then
    (IrregularPolygon compareTo leftwardsLine) shouldBe right
    (IrregularPolygon compareTo rightwardsLine) shouldBe left
    (IrregularPolygon compareTo centralLine) shouldBe centre
  }

  "Checking if a shape overlaps a line" should "correctly gauge if the line is overlapped" in {
    // given
    val overlappedPoint = Point(4, 4)
    val uncoveredPoint  = Point(3, 0)

    // then
    (IrregularPolygon overlaps overlappedPoint) shouldBe true
    (IrregularPolygon overlaps uncoveredPoint) shouldBe false
  }

  private def createIrregularPolygon = {
    Polygon(Point(4, 7),
            Point(6, 4),
            Point(8, 3),
            Point(6, 0),
            Point(3, 1),
            Point(1, 3),
            Point(2, 5))
  }
}
