package uk.ac.aber.adk15.controllers

import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.model.ConfigurationConstants.ExecutorType
import uk.ac.aber.adk15.model.ConfigurationConstants.ExecutorType.ExecutorType
import uk.ac.aber.adk15.model.{Config, ConfigurationService}
import uk.ac.aber.adk15.view.ConfigurationView

import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ComboBox, Spinner}
import scalafxml.core.macros.sfxml

trait ConfigurationController {
  def configureApplication(): Unit
  def cancelConfiguration(): Unit
}

@sfxml
class ConfigurationControllerImpl(private val maxThreads: Spinner[Int],
                                  private val executorTypeSelector: ComboBox[ExecutorType],
                                  private val configurationService: ConfigurationService)
    extends ConfigurationController {

  private val logger = Logger[ConfigurationController]

  executorTypeSelector.items set ObservableBuffer(ExecutorType.values toSeq)
  executorTypeSelector.getSelectionModel.selectFirst()

  override def configureApplication(): Unit = {
    val config = Config(executorTypeSelector.getValue, maxThreads.getValue)
    configurationService.configuration = config
    logger info s"Configuring application with $config"

    ConfigurationView.hide()
  }

  override def cancelConfiguration(): Unit = {
    logger info "Cancelling configuration; nothing changed."
    ConfigurationView.hide()
  }
}
