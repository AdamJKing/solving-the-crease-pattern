package uk.ac.aber.adk15.paper.newapi

import uk.ac.aber.adk15.geometry.Line

protected class FoldSelection(private val layers: List[NewPaperLayer]) {

  def getAvailableOperations: Set[NewFold] = {
    // if a fold crosses a creased fold, the layer that the creased fold corresponds
    // to must also share that fold.

    // let's look for valley folds
    val legalValleyFolds =
      searchForNonBlockedFolds(_.valleyFolds, layers)

    // let's look for mountain folds
    val legalMountainFolds =
      searchForNonBlockedFolds(_.mountainFolds, layers.reverse)

    legalValleyFolds ++ legalMountainFolds
  }

  private def correspondingCreasedFold(foldLine: Line, creasedFolds: Set[Line]): Boolean = {
    creasedFolds exists (line => {
      line intersectWith foldLine exists (p =>
        // if the intersection is within our range
        layers exists (layer => layer coversPoint p))
    })
  }

  private def testForFoldContinuity(foldLine: Line, layersToTest: List[NewPaperLayer]): Boolean = {
    layersToTest match {
      case first :: Nil =>
        first contains foldLine
      case first :: second :: rest =>
        (first contains foldLine) && {
          val creasedFoldLines = (second.creasedFolds diff first.creasedFolds) map (_.line)
          if (correspondingCreasedFold(foldLine, creasedFoldLines))
            testForFoldContinuity(foldLine, second :: rest)
          else true
        }
      case Nil => false
    }
  }

  private def searchForNonBlockedFolds(foldsFrom: NewPaperLayer => Set[NewFold],
                                       layersToSearch: List[NewPaperLayer]): Set[NewFold] = {
    layersToSearch match {
      case first :: Nil => foldsFrom(first)
      case first :: rest =>
        val firstFolds = foldsFrom(first) filter (fold => {
          // look for those creased folds
          val creasedFoldLines = first.creasedFolds map (_.line)
          if (creasedFoldLines.nonEmpty && correspondingCreasedFold(fold.line, creasedFoldLines)) {
            testForFoldContinuity(fold.line, first :: rest)
          } else true
        })

        // in some cases the top layer does not block
        // creases in the layer below
        if (first.surfaceArea < rest.head.surfaceArea) {
          // ... repeat whole thing with creases on layer underneath
          val foldsFromLayersBeneath = searchForNonBlockedFolds(foldsFrom, rest)

          firstFolds ++ (foldsFromLayersBeneath filterNot (first coversLine _.line))
        } else firstFolds
      case Nil => Set()
    }
  }
}
