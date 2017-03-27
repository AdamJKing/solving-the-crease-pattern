package uk.ac.aber.adk15.controllers

import java.io.File
import java.util.concurrent.ForkJoinPool

import com.google.inject.Inject
import uk.ac.aber.adk15.executors.ant.AntBasedFoldExecutor
import uk.ac.aber.adk15.model.Config
import uk.ac.aber.adk15.paper.Fold

import scala.concurrent.{ExecutionContext, Future}

trait ApplicationController {
  def execute(creasePatternFile: File): Future[Option[List[Fold]]]
}

class ApplicationControllerImpl @Inject()(private val config: Config,
                                          private val antBasedFoldExecutor: AntBasedFoldExecutor,
                                          private val creasePatternParser: CreasePatternParser)
    extends ApplicationController {

  def execute(creasePatternFile: File): Future[Option[List[Fold]]] = {
    implicit val executionContext =
      ExecutionContext.fromExecutor(new ForkJoinPool(config.maxThreads))

    antBasedFoldExecutor findFoldOrder {
      creasePatternParser parseFile creasePatternFile
    }
  }
}
