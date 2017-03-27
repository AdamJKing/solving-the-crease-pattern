package uk.ac.aber.adk15.controllers

import com.google.inject.Inject
import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.model.Config

trait ConfigurationController {
  def configureApplication(config: Config): Unit
}

class ConfigurationControllerImpl @Inject()(private val config: Config)
    extends ConfigurationController {

  private val logger = Logger[ConfigurationController]

  override def configureApplication(newConfig: Config): Unit = {
    this.config.maxThreads = newConfig.maxThreads
    logger info s"Configuring application with $newConfig"
  }
}
