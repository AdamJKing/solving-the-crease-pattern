package uk.ac.aber.adk15.controllers

import java.util.concurrent.ForkJoinPool

import com.google.inject.Inject
import uk.ac.aber.adk15.executors.ant.AntBasedFoldExecutor
import uk.ac.aber.adk15.model.Config
import uk.ac.aber.adk15.paper.CreasePattern
import uk.ac.aber.adk15.paper.fold.Fold

import scala.concurrent.ExecutionContext.fromExecutor
import scala.concurrent.Future

trait ApplicationController {
  import ApplicationController._

  def execute(creasePattern: CreasePattern, config: Config): Future[ExecutionResult]
}

class ApplicationControllerImpl @Inject()(private val antBasedFoldExecutor: AntBasedFoldExecutor)
    extends ApplicationController {
  import ApplicationController._

  override def execute(creasePattern: CreasePattern, config: Config): Future[ExecutionResult] = {
    implicit val executionContext = fromExecutor(new ForkJoinPool(config.maxThreads))

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
