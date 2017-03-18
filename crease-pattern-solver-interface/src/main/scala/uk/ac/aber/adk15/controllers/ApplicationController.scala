package uk.ac.aber.adk15.controllers

import com.google.inject.Inject
import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.controllers.ui.ApplicationViewController
import uk.ac.aber.adk15.executors.FoldExecutorFactory
import uk.ac.aber.adk15.model.ConfigurationService
import uk.ac.aber.adk15.paper.CreasePatternPredef.Constants.ModelConstants.BlankPaper
import uk.ac.aber.adk15.paper.CreasePatternPredef.Fold

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.{Failure, Success}

trait ApplicationController {
  def start(): Unit
}

class ApplicationControllerImpl @Inject()(private val configurationService: ConfigurationService,
                                          private val foldExecutorFactory: FoldExecutorFactory)
    extends ApplicationController {

  private val logger = Logger[ApplicationViewController]

  def start(): Unit = {
    val config       = configurationService.configuration
    val foldExecutor = foldExecutorFactory createFactoryFrom config

    val foldOrder = Promise[List[Fold]]
    foldOrder success { foldExecutor findFoldOrder BlankPaper }

    foldOrder.future onComplete {
      case Success(result) => logger info s"Process complete with foldOrder=$result"
      case Failure(ex)     => throw ex
    }
  }
}
