package uk.ac.aber.adk15.geometry

import uk.ac.aber.adk15.CommonFlatSpec

/**
  * Tests for [[Line]]
  */
class LineSpec extends CommonFlatSpec {

  "Two lines that have the same position/gradient" should "align with each other" in {
    // given
    val line      = Line(Point(0, 0), Point(100, 100))
    val otherLine = Line(Point(110, 110), Point(200, 200))

    // when
    val aligned = line alignsWith otherLine

    // then
    aligned shouldBe true
  }

  "Two lines that are the same" should "be equal" in {
    // given
    val line         = Line(Point(0, 0), Point(10, 10))
    val otherLine    = Line(Point(0, 0), Point(10, 10))
    val reversedLine = Line(Point(10, 10), Point(0, 0))

    // then
    line should equal(otherLine)
    line should equal(reversedLine)
  }

  "Two lines that are not the same" should "not be equal" in {
    // given
    val line          = Line(Point(0, 0), Point(10, 10))
    val differentLine = Line(Point(0, 0), Point(0, 6))

    // then
    line should not equal differentLine
  }

  "Mapping a line" should "work as expected" in {
    // given
    val (a, b) = (Point(0, 0), Point(10, 10))
    val line   = Line(a, b)

    // then
    line map ((start, end) => {
      start shouldBe a
      end shouldBe b
    })
  }

  "Flat-mapping a line" should "work as expected" in {
    // given
    val (a, b)                 = (Point(0, 0), Point(10, 10))
    val (modifiedA, modifiedB) = (Point(10, 10), Point(20, 20))
    val line                   = Line(a, b)

    // when
    val modifiedLine = line flatMap (p => Point(p.x + 10, p.y + 10))

    // then
    modifiedLine shouldBe Line(modifiedA, modifiedB)
  }
}
