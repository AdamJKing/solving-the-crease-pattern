package uk.ac.aber.adk15.controllers

import java.io.File
import java.util.concurrent.ForkJoinPool

import com.google.inject.Inject
import uk.ac.aber.adk15.executors.ant.AntBasedFoldExecutor
import uk.ac.aber.adk15.model.Config
import uk.ac.aber.adk15.paper.{CreasePattern, Fold}

import scala.concurrent.ExecutionContext.fromExecutor
import scala.concurrent.Future

trait ApplicationController {
  import ApplicationController._

  def execute(creasePatternFile: File, config: Config): Future[ExecutionResult]
}

class ApplicationControllerImpl @Inject()(private val antBasedFoldExecutor: AntBasedFoldExecutor,
                                          private val creasePatternParser: CreasePatternParser)
    extends ApplicationController {
  import ApplicationController._

  override def execute(creasePatternFile: File, config: Config): Future[ExecutionResult] = {
    implicit val executionContext = fromExecutor(new ForkJoinPool(config.maxThreads))

    val creasePattern   = creasePatternParser parseFile creasePatternFile
    val futureFoldOrder = antBasedFoldExecutor findFoldOrder (creasePattern, config.maxThreads)

    futureFoldOrder map {
      case Some(foldOrder) => SuccessfulExecution(foldOrder, creasePattern)
      case None            => FailedExecution()
    }
  }
}

object ApplicationController {
  abstract class ExecutionResult
  case class SuccessfulExecution(foldOrder: List[Fold], extractedCreasePattern: CreasePattern)
      extends ExecutionResult
  case class FailedExecution() extends ExecutionResult
}
