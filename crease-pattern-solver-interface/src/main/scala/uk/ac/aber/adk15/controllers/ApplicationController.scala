package uk.ac.aber.adk15.controllers

import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.executors.FoldExecutorFactory
import uk.ac.aber.adk15.model.ConfigurationService
import uk.ac.aber.adk15.paper.CreasePatternPredef.Constants.ModelConstants.BlankPaper
import uk.ac.aber.adk15.view.ConfigurationView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scalafxml.core.macros.sfxml

@sfxml
class ApplicationController(private val configurationService: ConfigurationService,
                            private val foldExecutorFactory: FoldExecutorFactory) {

  private val logger: Logger = Logger[ApplicationController]

  def start(): Unit = {
    val config       = configurationService.configuration
    val foldExecutor = foldExecutorFactory createFactoryFrom config

    Future { foldExecutor findFoldOrder BlankPaper } onComplete {
      case Success(foldOrder) => logger info s"Process complete with foldOrder=$foldOrder"
      case Failure(ex)        => throw ex
    }
  }

  def configure(): Unit = {
    ConfigurationView.show()
  }

  def loadCreasePattern(): Unit = {}
}
