package uk.ac.aber.adk15.executors.ant

import uk.ac.aber.adk15.paper.CreasePattern
import uk.ac.aber.adk15.paper.fold.Fold

case class FoldNode(model: CreasePattern, fold: Option[Fold]) {
  lazy val children: Set[FoldNode] = {
    model.availableFolds map (childFold => FoldNode(model <~~ childFold, Some(childFold)))
  }
}
