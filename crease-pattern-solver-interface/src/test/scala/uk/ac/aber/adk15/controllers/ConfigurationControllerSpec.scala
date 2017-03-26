package uk.ac.aber.adk15.controllers

import org.mockito.Mock
import org.mockito.Mockito.verify
import org.scalatest.BeforeAndAfterAll
import uk.ac.aber.adk15.CommonFlatSpec
import uk.ac.aber.adk15.model.ConfigConstants.DefaultConfig
import uk.ac.aber.adk15.model.{Config, ConfigurationService}

class ConfigurationControllerSpec extends CommonFlatSpec with BeforeAndAfterAll {

  @Mock private var configurationService: ConfigurationService = _

  private var configurationController: ConfigurationController = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    configurationController = new ConfigurationControllerImpl(configurationService)
  }

  "Configuring the application" should "update the configuration using the config service" in {
    // when
    configurationController configureApplication DefaultConfig

    // then
    val configCapture = captor[Config]
    verify(configurationService) configuration_= (configCapture capture)
    configCapture.getValue should be(DefaultConfig)
  }

}
