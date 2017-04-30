package uk.ac.aber.adk15.paper.newapi

import uk.ac.aber.adk15.CommonFlatSpec

class NewCreasePatternSpec extends CommonFlatSpec {
  "Crease patterns with the same edges" should "equal each other" in {
    // given
    val paperLayer         = mock[NewPaperLayer]
    val creasePattern      = NewCreasePattern(List(paperLayer))
    val otherCreasePattern = NewCreasePattern(List(paperLayer))

    // then
    creasePattern shouldBe otherCreasePattern
  }
}
