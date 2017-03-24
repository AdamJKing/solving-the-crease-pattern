package uk.ac.aber.adk15.executors

import uk.ac.aber.adk15.paper.{Fold, Foldable}

import scala.concurrent.{ExecutionContext, Future}

trait FoldExecutor {
  def findFoldOrder(creasePattern: Foldable)(
      implicit executionContext: ExecutionContext): Future[Option[List[Fold]]]
}
