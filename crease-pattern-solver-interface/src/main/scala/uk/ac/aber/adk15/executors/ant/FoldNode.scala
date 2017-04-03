package uk.ac.aber.adk15.executors.ant

import uk.ac.aber.adk15.paper.CreasePatternPredef.Helpers._
import uk.ac.aber.adk15.paper.{CreasePattern, Fold}
import uk.ac.aber.adk15.services.FoldSelectionService

case class FoldNode(model: CreasePattern, fold: Option[Fold])(
    implicit val foldSelectionService: FoldSelectionService) {

  private val newModel = fold.map(f => model <~~ f).getOrElse(model)

  lazy val children: Set[FoldNode] = {
    foldSelectionService getAvailableOperations newModel map (childFold =>
      FoldNode(newModel, Some(childFold)))
  }

  def allFoldsAreComplete(): Boolean = newModel.folds.isEmpty
}
