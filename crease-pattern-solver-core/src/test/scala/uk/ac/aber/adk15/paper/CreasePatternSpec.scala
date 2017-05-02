package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.CommonFlatSpec

class CreasePatternSpec extends CommonFlatSpec {
  "Crease patterns with the same edges" should "equal each other" in {
    // given
    val paperLayer         = mock[PaperLayer]
    val creasePattern      = CreasePattern(List(paperLayer))
    val otherCreasePattern = CreasePattern(List(paperLayer))

    // then
    creasePattern shouldBe otherCreasePattern
  }
}
