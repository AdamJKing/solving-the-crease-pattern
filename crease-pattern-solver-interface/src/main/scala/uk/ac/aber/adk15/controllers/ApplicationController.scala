package uk.ac.aber.adk15.controllers

import java.util.concurrent.Executors

import com.google.inject.Inject
import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.controllers.ui.ApplicationViewController
import uk.ac.aber.adk15.executors.ant.AntBasedFoldExecutor
import uk.ac.aber.adk15.model.ConfigurationService
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._
import uk.ac.aber.adk15.paper.{CreasePattern, Foldable, Point}
import uk.ac.aber.adk15.services.FoldSelectionService

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait ApplicationController {
  def start(): Unit
}

class ApplicationControllerImpl @Inject()(private val configurationService: ConfigurationService,
                                          private val foldSelectionService: FoldSelectionService)
    extends ApplicationController {

  private val logger = Logger[ApplicationViewController]

  val FlatCreasePattern: Foldable = CreasePattern from (
    Point(0, 0) -- Point(100, 0),
    Point(100, 0) -- Point(100, 100),
    Point(100, 100) -- Point(0, 100),
    Point(0, 100) -- Point(0, 0),
    Point(0, 100) /\ Point(100, 0)
  )

  def start(): Unit = {
    val config       = configurationService.configuration
    val foldExecutor = new AntBasedFoldExecutor(foldSelectionService, config)

    implicit val executionContext =
      ExecutionContext.fromExecutor(Executors.newFixedThreadPool(config.maxThreads))

    foldExecutor findFoldOrder FlatCreasePattern onComplete {
      case Success(Some(result)) => logger info s"Process complete with foldOrder=$result"
      case Success(None)         =>
      case Failure(ex)           => throw ex
    }
  }
}
