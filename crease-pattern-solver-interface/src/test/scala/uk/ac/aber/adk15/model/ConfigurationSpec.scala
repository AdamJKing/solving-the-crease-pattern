package uk.ac.aber.adk15.model

import uk.ac.aber.adk15.CommonFlatSpec

class ConfigurationSpec extends CommonFlatSpec {

  "The max threads config" should "be set to min if max threads set below min" in {
    // given
    val invalidMaxThreads = -20

    // then
    assertThrows[IllegalArgumentException](Config(invalidMaxThreads))
  }

  "The max threads config" should "be set to max if max threads set above max" in {
    // given
    val invalidMaxThreads = 300

    // then
    assertThrows[IllegalArgumentException](Config(invalidMaxThreads))
  }

  "The max threads config" should "be set to the desired value if that value is valid" in {
    // given
    val validMaxThreads = 8

    // when
    val config = Config(validMaxThreads)

    // then
    config.maxThreads shouldBe validMaxThreads
  }
}
