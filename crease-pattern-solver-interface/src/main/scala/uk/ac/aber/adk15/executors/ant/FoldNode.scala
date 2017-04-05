package uk.ac.aber.adk15.executors.ant

import uk.ac.aber.adk15.paper.CreasePatternPredef.Helpers._
import uk.ac.aber.adk15.paper.{CreasePattern, Fold}
import uk.ac.aber.adk15.services.FoldSelectionService

case class FoldNode(model: CreasePattern, fold: Option[Fold])(
    implicit val foldSelectionService: FoldSelectionService) {

  lazy val children: Set[FoldNode] = {
    foldSelectionService getAvailableOperations model map (childFold =>
      FoldNode(model <~~ childFold, Some(childFold)))
  }

  def allFoldsAreComplete(): Boolean = model.folds.isEmpty
}
