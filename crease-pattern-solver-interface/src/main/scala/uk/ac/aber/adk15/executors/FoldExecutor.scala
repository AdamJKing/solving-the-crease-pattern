package uk.ac.aber.adk15.executors

import uk.ac.aber.adk15.paper.CreasePattern
import uk.ac.aber.adk15.paper.CreasePatternPredef.Fold

trait FoldExecutor {
  def findFoldOrder(creasePattern: CreasePattern): List[Fold]
}
