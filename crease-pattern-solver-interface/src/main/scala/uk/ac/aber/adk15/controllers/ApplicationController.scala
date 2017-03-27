package uk.ac.aber.adk15.controllers

import java.util.concurrent.ForkJoinPool

import com.google.inject.Inject
import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.controllers.ui.ApplicationViewController
import uk.ac.aber.adk15.executors.ant.AntBasedFoldExecutor
import uk.ac.aber.adk15.model.ConfigurationService
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._
import uk.ac.aber.adk15.paper.{CreasePattern, Fold, Foldable, Point}

import scala.concurrent.{ExecutionContext, Future}

trait ApplicationController {
  def execute(): Future[Option[List[Fold]]]
}

class ApplicationControllerImpl @Inject()(private val configurationService: ConfigurationService,
                                          private val antBasedFoldExecutor: AntBasedFoldExecutor)
    extends ApplicationController {

  private val logger = Logger[ApplicationViewController]

  val FlatCreasePattern: Foldable = CreasePattern from (
    Point(0, 0) -- Point(100, 0),
    Point(100, 0) -- Point(100, 100),
    Point(100, 100) -- Point(0, 100),
    Point(0, 100) -- Point(0, 0),
    Point(0, 100) /\ Point(100, 0)
  )

  def execute(): Future[Option[List[Fold]]] = {
    val config = configurationService.configuration

    implicit val executionContext =
      ExecutionContext.fromExecutor(new ForkJoinPool(config.maxThreads))

    antBasedFoldExecutor findFoldOrder FlatCreasePattern
  }
}
