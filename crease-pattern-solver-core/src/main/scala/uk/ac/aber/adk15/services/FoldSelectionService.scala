package uk.ac.aber.adk15.services

import uk.ac.aber.adk15.paper.{CreasedFold, Fold, Foldable}

trait FoldSelectionService {
  def getAvailableOperations(creasePattern: Foldable): Set[Fold]
}
class FoldSelectionServiceImpl extends FoldSelectionService {
  override def getAvailableOperations(model: Foldable): Set[Fold] = {
    def fitsCreasedEdgeRule(layers: List[Set[Fold]]): Boolean = {
      (layers sliding 2) map { x =>
        (x.head filter (_.foldType == CreasedFold)) exists
          (x.last filter (_.foldType == CreasedFold)).contains
      } forall (_ == true)
    }

    model.creases filter (crease => {
      val layers = model.layers filter (_ contains crease)

      if (layers.size > 1)
        fitsCreasedEdgeRule(layers)

      layers.nonEmpty
    })
  }
}
