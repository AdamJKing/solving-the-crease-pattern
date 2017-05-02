package uk.ac.aber.adk15.paper.fold

import uk.ac.aber.adk15.geometry.Line
import uk.ac.aber.adk15.paper.PaperLayer

class FoldSelection(private val layers: List[PaperLayer]) {

  def getAvailableOperations: Set[Fold] = {
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
        layers exists (layer => (layer coversPoint p) || (layer isOnEdge p)))
    })
  }

  private def testForFoldContinuity(fold: Fold, layersToTest: List[PaperLayer]): Boolean = {
    layersToTest match {
      case first :: Nil =>
        first contains fold
      case first :: second :: rest =>
        (second contains fold) && {
          val creasedFolds = (second.creasedFolds diff first.creasedFolds) map (_.line)
          if (correspondingCreasedFold(fold.line, creasedFolds))
            testForFoldContinuity(fold, second :: rest)
          else true
        }
      case Nil => false
    }
  }

  private def searchForNonBlockedFolds(foldsFrom: PaperLayer => Set[Fold],
                                       layersToSearch: List[PaperLayer]): Set[Fold] = {
    layersToSearch match {
      case first :: Nil => foldsFrom(first)
      case first :: rest =>
        val firstFolds = foldsFrom(first) filter (fold => {
          // look for those creased folds
          val creasedFoldLines = first.creasedFolds map (_.line)
          if (creasedFoldLines.nonEmpty && correspondingCreasedFold(fold.line, creasedFoldLines)) {
            testForFoldContinuity(fold, first :: rest)
          } else true
        })

        // in some cases the top layer does not block
        // creases in the layer below
        val surfaceAreaBelow = (rest map (_.surfaceArea)).max
        if (first.surfaceArea < surfaceAreaBelow) {
          // ... repeat whole thing with creases on layer underneath
          val foldsFromLayersBeneath = searchForNonBlockedFolds(foldsFrom, rest)

          firstFolds ++ (foldsFromLayersBeneath filterNot (foldFromBeneath => {
            val coversLine = first coversLine foldFromBeneath.line
            val conflicts = first exists (fold =>
              foldFromBeneath match {
                case Fold(line, MountainFold) =>
                  (fold.line alignsWith line) && fold.foldType == ValleyFold

                case Fold(line, ValleyFold) =>
                  (fold.line alignsWith line) && fold.foldType == MountainFold
              })

            coversLine || conflicts
          }))
        } else firstFolds
      case Nil => Set()
    }
  }
}
