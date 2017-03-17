package uk.ac.aber.adk15.controllers

import uk.ac.aber.adk15.model.ConfigurationConstants.ExecutorType
import uk.ac.aber.adk15.model.{Config, ConfigurationService}
import uk.ac.aber.adk15.view.ConfigurationView

import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ComboBox, Spinner}
import scalafxml.core.macros.sfxml

trait ConfigurationController {
  def configureApplication(): Unit
  def openConfigurationView(): Unit
  def closeConfigurationView(): Unit
}

@sfxml
class ConfigurationControllerImpl(private val maxThreads: Spinner[Int],
                                  private val executorTypeSelector: ComboBox[String],
                                  private val configurationService: ConfigurationService)
    extends ConfigurationController {

  override def configureApplication(): Unit = {
    closeConfigurationView()
    configurationService.configuration =
      Config(ExecutorType withName executorTypeSelector.getValue, maxThreads.getValue)
  }

  override def openConfigurationView(): Unit = {
    populateExecutorTypeSelector(ExecutorType.values.map { _.toString } toSeq)
    ConfigurationView.show()
  }
  override def closeConfigurationView(): Unit = ConfigurationView.hide()

  private def populateExecutorTypeSelector(values: Seq[String]) = {
    executorTypeSelector.items set ObservableBuffer(values)
  }
}
