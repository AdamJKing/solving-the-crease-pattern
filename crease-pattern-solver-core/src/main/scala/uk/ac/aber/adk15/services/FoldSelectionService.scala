package uk.ac.aber.adk15.services

import uk.ac.aber.adk15.paper.CreasePatternPredef.Fold
import uk.ac.aber.adk15.paper.Foldable

trait FoldSelectionService {
  def getAvailableOperations(creasePattern: Foldable): Set[Fold]
}
class FoldSelectionServiceImpl extends FoldSelectionService {
  override def getAvailableOperations(creasePattern: Foldable): Set[Fold] =
    creasePattern.creases match {
      case layer :: Nil => layer
      case _            => creasePattern.creases reduce { _ intersect _ }
    }
}
