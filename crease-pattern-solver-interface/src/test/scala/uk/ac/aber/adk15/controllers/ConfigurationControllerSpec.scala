package uk.ac.aber.adk15.controllers

import org.mockito.BDDMockito._
import org.mockito.Mock
import org.scalatest.BeforeAndAfterAll
import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.model.Config

class ConfigurationControllerSpec extends CommonFlatSpec with BeforeAndAfterAll {

  @Mock private var newConfig: Config = _

  private var configurationController: ConfigurationController = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    configurationController = new ConfigurationControllerImpl(mock[Config])
  }

  it should "override the current config values with the new config values" in {
    // given
    given(newConfig.maxThreads) willReturn 8

    // when
    configurationController.configureApplication(newConfig)

    //
  }
}
