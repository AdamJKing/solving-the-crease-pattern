package uk.ac.aber.adk15.controllers

import com.google.inject.Inject
import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.model.{Config, ConfigurationService}

trait ConfigurationController {
  def configureApplication(config: Config): Unit
}

class ConfigurationControllerImpl @Inject()(private val configurationService: ConfigurationService)
    extends ConfigurationController {

  private val logger = Logger[ConfigurationController]

  override def configureApplication(config: Config): Unit = {
    configurationService.configuration = config
    logger info s"Configuring application with $config"
  }
}
