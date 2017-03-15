package uk.ac.aber.adk15.controllers

import uk.ac.aber.adk15.model.{Config, ConfigurationService}
import uk.ac.aber.adk15.view.ConfigurationView

import scalafx.scene.control.Spinner
import scalafxml.core.macros.sfxml

trait ConfigurationController {
  def configureApplication(): Unit
  def openConfigurationView(): Unit
  def closeConfigurationView(): Unit
}

@sfxml
class ConfigurationControllerImpl(private val maxThreads: Spinner[Int],
                                  private val configurationService: ConfigurationService)
    extends ConfigurationController {

  override def configureApplication(): Unit = {
    closeConfigurationView()
    configurationService.configuration = Config(maxThreads.value.value)
  }

  override def openConfigurationView(): Unit  = ConfigurationView.show()
  override def closeConfigurationView(): Unit = ConfigurationView.hide()
}
