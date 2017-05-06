package uk.ac.aber.adk15.executors.ant

import com.google.inject.Inject
import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.paper.CreasePattern
import uk.ac.aber.adk15.paper.fold.Fold

import scala.concurrent.{ExecutionContext, Future}

trait AntBasedFoldExecutor {
  def findFoldOrder(creasePattern: CreasePattern, maxAnts: Int)(
      implicit executionContext: ExecutionContext): Future[Option[List[Fold]]]
}

class AntBasedFoldExecutorImpl @Inject()(antTraverser: AntTraverser) extends AntBasedFoldExecutor {

  private val logger = Logger[AntBasedFoldExecutorImpl]

  type PossibleFoldOrder = Future[Option[List[Fold]]]

  override def findFoldOrder(creasePattern: CreasePattern, maxAnts: Int)(
      implicit executionContext: ExecutionContext): PossibleFoldOrder = {

    val operationTreeRoot = FoldNode(creasePattern, None)

    logger info s"Starting ant colony with $maxAnts ants."
    Future.firstCompletedOf {
      List.range(0, maxAnts) map (_ => Future(antTraverser traverseTree operationTreeRoot))
    }
  }
}
