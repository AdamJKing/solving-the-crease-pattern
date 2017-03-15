package uk.ac.aber.adk15

import uk.ac.aber.adk15.paper.CreasePattern
import uk.ac.aber.adk15.paper.CreasePatternPredef.Fold
import uk.ac.aber.adk15.view.ProgressDisplayPane

trait Executor { this: ProgressDisplayPane =>

  def findFoldOrder(creasePattern: CreasePattern): List[Fold]
}
