package uk.ac.aber.adk15.services

import uk.ac.aber.adk15.paper.{Fold, Foldable}

trait FoldSelectionService {
  def getAvailableOperations(creasePattern: Foldable): Set[Fold]
}
class FoldSelectionServiceImpl extends FoldSelectionService {
  override def getAvailableOperations(model: Foldable): Set[Fold] =
    model.creases filter (crease => model.layers forall (_ contains crease))
}
