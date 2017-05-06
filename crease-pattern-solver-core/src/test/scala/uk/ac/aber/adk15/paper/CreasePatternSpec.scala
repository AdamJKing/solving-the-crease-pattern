package uk.ac.aber.adk15.paper

import uk.ac.aber.adk15.CommonFlatSpec

/**
  * Tests for [[CreasePattern]], currently most functionality of the crease-pattern
  * is covered by other tests.
  *
  * Some elements of a crease-pattern are not currently testable, something would change
  * with further development of the application.
  *
  */
class CreasePatternSpec extends CommonFlatSpec {
  "Crease patterns with the same edges" should "equal each other" in {
    // given
    val paperLayer         = mock[PaperLayer]
    val creasePattern      = CreasePattern(List(paperLayer))
    val otherCreasePattern = CreasePattern(List(paperLayer))

    // then
    creasePattern shouldBe otherCreasePattern
  }

  "A crease-pattern with zero layers" should "be impossible to create" in {
    assertThrows[IllegalArgumentException](new CreasePattern(List()))
  }
}
