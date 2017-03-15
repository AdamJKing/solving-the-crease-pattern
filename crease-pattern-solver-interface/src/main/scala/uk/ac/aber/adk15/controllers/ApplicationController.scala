package uk.ac.aber.adk15.controllers

import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.model.ConfigurationService
import uk.ac.aber.adk15.view.ConfigurationView

import scalafxml.core.macros.sfxml

@sfxml
class ApplicationController(private val configurationService: ConfigurationService) {

  val logger: Logger = Logger[ApplicationController]

  def start(): Unit = {
    val config = configurationService.configuration
  }

  def configure(): Unit = {
    logger debug "Configuring!"
    ConfigurationView.show()
  }

  def loadCreasePattern(): Unit = {}
}
