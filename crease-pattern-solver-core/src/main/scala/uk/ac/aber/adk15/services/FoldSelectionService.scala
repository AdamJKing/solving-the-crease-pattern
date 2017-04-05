package uk.ac.aber.adk15.services

import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.paper.{CreasePattern, Fold, PaperLayer}

trait FoldSelectionService {
  def getAvailableOperations(creasePattern: CreasePattern): Set[Fold]
}
class FoldSelectionServiceImpl extends FoldSelectionService {

  private val logger = Logger[FoldSelectionService]

  override def getAvailableOperations(model: CreasePattern): Set[Fold] = {
    // if a fold crosses a creased fold, the layer that the creased fold corresponds
    // to must also share that fold.

    // let's look for valley folds
    val legalValleyFolds =
      searchForNonBlockedFolds(_.valleyFolds, model.layers)

    // let's look for mountain folds
    val mountainFolds =
      searchForNonBlockedFolds(_.mountainFolds, model.layers.reverse)

    (legalValleyFolds ++ mountainFolds)(collection.breakOut)
  }

  private def correspondingCreasedFold(fold: Fold, creasedFolds: List[Fold]): Boolean = {
    val (x1, y1) = (fold.start.x, fold.start.y)
    val (x2, y2) = (fold.end.x, fold.end.y)

    logger debug s"Checking out fold=$fold"

    creasedFolds exists (line => {
      val (x3, y3) = (line.start.x, line.start.y)
      val (x4, y4) = (line.end.x, line.end.y)

      // if the lines are the same
      if (line == fold) true
      else {
        val a = (x1 - x2) * (y3 - y4)
        val b = (y1 - y2) * (x3 - x4)

        // if the lines are parallel
        if (a - b == 0) return false

        // calculate the determinants
        val px = (((x1 * y2 - y1 * x2) * (x3 - x4)) - (x1 - x2) * (x3 * y4 - y3 * x4)) / (((x1 - x2) * (y3 - y4)) - (y1 - y2) * (x3 - x4))
        val py = (((x1 * y2 - y1 * x2) * (y3 - y4)) - (y1 - y2) * (x3 * y4 - y3 * x4)) / (((x1 - x2) * (x3 - y4)) - (y1 - y2) * (x3 - x4))

        // if the intersection is within our range

        val sortedValuesX = ((creasedFolds flatMap (_.toSet)) map (_.x)).sorted
        val sortedValuesY = ((creasedFolds flatMap (_.toSet)) map (_.y)).sorted

        val (xMax, xMin) = (sortedValuesX.last - 1, sortedValuesX.head + 1)
        val (yMax, yMin) = (sortedValuesY.last - 1, sortedValuesY.head + 1)

        if (px <= xMax && px >= xMin && py <= yMax && py >= yMin) return true

        false
      }
    })
  }

  private def testForFoldContinuity(fold: Fold, layers: List[PaperLayer]): Boolean = {
    layers match {
      case first :: Nil => first contains fold
      case first :: second :: rest =>
        (first contains fold) && {
          if (correspondingCreasedFold(fold, second.creasedFolds diff first.creasedFolds))
            testForFoldContinuity(fold, second :: rest)
          else true
        }
      case Nil => false
    }
  }

  private def searchForNonBlockedFolds(foldsFrom: PaperLayer => List[Fold],
                                       layers: List[PaperLayer]): List[Fold] = {
    layers match {
      case first :: Nil => foldsFrom(first)
      case first :: rest =>
        val firstFolds = foldsFrom(first) filter (fold => {
          // look for those creased folds
          if (correspondingCreasedFold(fold, first.creasedFolds)) {
            testForFoldContinuity(fold, rest)

          } else true
        })

        // in some cases the top layer does not block
        // creases in the layer below
        if (first.surfaceArea < rest.head.surfaceArea) {
          // ... repeat whole thing with creases on layer underneath
          val foldsFromLayersBeneath = searchForNonBlockedFolds(foldsFrom, rest)

          firstFolds ++ (foldsFromLayersBeneath filterNot (first coversFold _))
        } else firstFolds
      case Nil => List()
    }
  }
}
