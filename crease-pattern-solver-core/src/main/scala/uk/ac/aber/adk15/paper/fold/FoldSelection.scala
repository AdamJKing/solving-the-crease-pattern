package uk.ac.aber.adk15.paper.fold

import uk.ac.aber.adk15.geometry.Line
import uk.ac.aber.adk15.paper.PaperLayer

import scala.annotation.tailrec

/**
  * This class concerns itself with identifying all possible folds
  * from a given series of layers.
  *
  * For example if a valley fold is covered by the layer above it
  * then that valley fold will not be available.
  *
  * @param layers the layers to search for available folds
  */
class FoldSelection(private val layers: List[PaperLayer]) {

  /**
    * Searches for all legal, foldable folds in the layers.
    *
    * @return all legal or available folds in the crease-pattern's layers
    */
  def getAvailableOperations: Set[Fold] = {

    // let's look for valley folds
    val legalValleyFolds =
      searchForNonBlockedFolds(_.valleyFolds, layers)

    // let's look for mountain folds
    // the process for finding mountain folds is the same as finding valley folds
    // except we examine the layers in reverse
    val legalMountainFolds =
      searchForNonBlockedFolds(_.mountainFolds, layers.reverse)

    legalValleyFolds ++ legalMountainFolds
  }

  /**
    * Checks if there is a 'corresponding creased fold'.
    *
    * A 'corresponding creased fold' is a place where the current layer attaches
    * to a layer above or underneath it. This is important because if a layer has
    * an attached layer then, when the current layer is folded, the layer behind will also
    * be folded.
    *
    * A fold should only be considered legal if all layers affected by the fold also contain
    * that fold.
    *
    * @param foldLine the line of the fold, used to find intersections
    * @param creasedFolds a list of all creased folds in the current layer
    * @return true if folding along the given fold line would affect another layer, false if not
    */
  private def correspondingCreasedFold(foldLine: Line, creasedFolds: Set[Line]): Boolean = {
    // another layer would only be affected by the fold if the creased fold in
    // question intersects with our fold line
    creasedFolds exists (line => {
      line intersectWith foldLine exists (p =>
        // if the intersection is within our range
        // as all non-parallel lines will cross eventually, we want to
        // ensure the intersection is in our model
        layers exists (layer => (layer coversPoint p) || (layer isOnEdge p)))
    })
  }

  /**
    * Fold continuity is when each fold in a given series all share the same fold.
    * Fold continuity only affects layers that are linked together, if a fold can affect
    * a series of layers without affecting layers below it, then that fold is still foldable.
    *
    * @param fold the fold to test for continuity
    * @param layersToTest the layers to check for the fold
    * @return true if the fold validly pierces all layers, false if not
    */
  @tailrec
  private def testForFoldContinuity(fold: Fold, layersToTest: List[PaperLayer]): Boolean = {
    layersToTest match {
      // if there is only one layer, we only check that the layer contains the fold
      case first :: Nil => first contains fold

      // otherwise we want to check that the rest of the layers contain it
      case first :: second :: rest =>
        (second contains fold) && {
          // creased folds come in pairs, so every time we find two that are the same
          // then we should consider them 'validated'
          val creasedFolds = (second.creasedFolds diff first.creasedFolds) map (_.line)

          // check for new creased folds that do not have a completed/validated pair
          if (correspondingCreasedFold(fold.line, creasedFolds))
            testForFoldContinuity(fold, second :: rest)
          else true
        }

      // no layers have no fold continuity because they cannot contain a fold
      case Nil => false
    }
  }

  /**
    * Search the given layers for valid or legal folds.
    *
    * @param foldsFrom a value-function that returns the desired folds from a layer
    * @param layersToSearch the layers to search for folds
    * @return
    */
  private def searchForNonBlockedFolds(foldsFrom: PaperLayer => Set[Fold],
                                       layersToSearch: List[PaperLayer]): Set[Fold] = {
    layersToSearch match {
      // if there is only one layer then there is no need to check for blocked folds
      case first :: Nil => foldsFrom(first)
      case first :: rest =>
        val firstFolds = foldsFrom(first) filter (fold => {
          // look for those creased folds
          // if there are no creased folds there is nothing to check for
          val creasedFoldLines = first.creasedFolds map (_.line)
          if (creasedFoldLines.nonEmpty && correspondingCreasedFold(fold.line, creasedFoldLines)) {
            testForFoldContinuity(fold, first :: rest)
          } else true
        })

        // in some cases the top layers are quite small and as such does
        // not block any creases in the layer below

        // find the largest layer below this one
        val surfaceAreaBelow = (rest map (_.surfaceArea)).max

        // if our layer is not as big as that largest layer, it might not cover all the folds
        if (first.surfaceArea < surfaceAreaBelow) {
          // ... repeat whole thing with creases on layer underneath
          val foldsFromLayersBeneath = searchForNonBlockedFolds(foldsFrom, rest)

          // finally remove any folds that are definitely blocked by the top layer
          // as even a small layer may block certain folds
          firstFolds ++ (foldsFromLayersBeneath filterNot (foldFromBeneath => {
            val coversLine = first coversLine foldFromBeneath.line

            // a 'conflicting fold' is one where the layer does not block the fold
            // but instead holds the same fold of the opposite type.
            // this can happen when the symmetry of the model gets messed up
            val conflicts = first exists (fold =>
              foldFromBeneath match {
                case Fold(line, MountainFold) =>
                  (fold.line alignsWith line) && fold.foldType == ValleyFold

                case Fold(line, _) =>
                  (fold.line alignsWith line) && fold.foldType == MountainFold
              })

            coversLine || conflicts
          }))
        } else firstFolds
      case Nil => Set()
    }
  }
}
