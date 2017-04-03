package uk.ac.aber.adk15.services

import uk.ac.aber.adk15.paper._

trait FoldSelectionService {
  def getAvailableOperations(creasePattern: CreasePattern): Set[Fold]
}
class FoldSelectionServiceImpl extends FoldSelectionService {
  override def getAvailableOperations(model: CreasePattern): Set[Fold] = {
    val possibleCreases =
      (model.layers flatMap (layer => layer.valleyFolds() ++ layer.mountainFolds())).toSet

    possibleCreases filter { crease =>
      val layersToCheck = {
        if (crease.foldType == ValleyFold)
          model.layers takeWhile (_ contains crease)
        else
          model.layers.reverse takeWhile (_ contains crease)
      }

      layersToCheck.size == 1 || {
        (layersToCheck sliding 2) forall { layer =>
          val (top, bottom) = (layer.head, layer.last)

          top.creasedFolds() exists (creased => bottom.creasedFolds() contains creased)
        }
      }
    }
  }
}
