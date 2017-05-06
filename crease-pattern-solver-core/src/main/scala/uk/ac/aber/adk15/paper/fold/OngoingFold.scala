package uk.ac.aber.adk15.paper.fold

import uk.ac.aber.adk15.paper.{CreasePattern, PaperLayer}

import scala.Function.tupled

/**
  * Represents a fold in process. This class is responsible
  * for applying changes to a given model.
  *
  * Code in this class should be purely applicative, any
  * logic associated with identifying affected parts of the model
  * belongs in the [[FoldContext]].
  *
  * @param foldContext the contextual information needed to perform the fold
  */
class OngoingFold(private val foldContext: FoldContext) {

  /**
    * Applies the fold using the given [[FoldContext]],
    * permanently creasing the model.
    *
    * @return a model that has been folded according to the context
    */
  def crease: CreasePattern = {
    // first we determine what our "hinge-point" is
    // ie. last index of the layer affected by the fold
    //
    val indexOfFold = {
      if (foldContext.shouldFoldAbove)
        // if we're folding above, we want the highest point we can fold from
        foldContext.foldableLayers.keys.min
      else
        // alternatively, if folding below we want the lowest point we can fold from
        foldContext.foldableLayers.keys.max
    }

    // at this point we physically rotate and move each layer
    // this is done by mapping the layer onto a new index
    //
    val foldedLayers = foldContext.foldableLayers map tupled((index, layer) => {

      // a simple calculation that rotates a single index around
      // the fold index
      //
      val d = math.abs(indexOfFold - index)
      val shiftedIndex =
        if (foldContext.shouldFoldAbove) index - (2 * d + 1)
        else index + (2 * d + 1)

      // we then associate the new index with the fold
      shiftedIndex -> (layer rotateAround foldContext.foldLine)
    })

    // small convenience function, transform a map into a list ordered by it's key
    val transform = (_: Map[Int, PaperLayer]).toList sortBy (_._1)

    // using our index maps as a guide, both layers are then healed together to form a single stack
    val repairedLayers = repair(transform(foldContext.unaffectedLayers), transform(foldedLayers))

    // finally, create a new crease-pattern using these new repaired layers
    new CreasePattern(repairedLayers)
  }

  /**
    * Heals two distinct layers using a mapping of indexes to the layer itself.
    * These index maps help to associate the relocated layers and decide if two
    * layers inhabit the same space.
    *
    * If two layers are identified to be on the same layer then one of two
    * things will happen;
    *
    *       * the two layers are merged together, and placed in the stack
    *
    *       * if the layers cannot be merged together, they are placed on top of
    *         each other, depending on the fold-type.
    *
    * If there are layers which do not conflict then they are simply placed on top
    * or on bottom depending on the fold type.
    *
    * This process is explained in more detail in the report/documentation.
    *
    * @param unaffectedLayers the layers that have not been modified, and their respective indexes
    * @param foldedLayers the layers that have been modified, and their new indexes
    * @return
    */
  private def repair(unaffectedLayers: List[(Int, PaperLayer)],
                     foldedLayers: List[(Int, PaperLayer)]): List[PaperLayer] = {

    // if there are no layers in either stack, then the healed layers are simply
    // the stack with layers remaining
    //
    if (unaffectedLayers.isEmpty) return foldedLayers map (_._2)
    if (foldedLayers.isEmpty) return unaffectedLayers map (_._2)

    // decompose and compare each layer stack using pattern matching
    (unaffectedLayers, foldedLayers) match {
      // for identification purposes:
      //  * the index is the index of that layer
      //  * the layer is the layer itself
      //  * `x` and `y` refer to the unaffected layer and the folded layer
      //    respectively
      //
      case ((xIndex, xLayer) :: xs, (yIndex, yLayer) :: ys) =>
        // if both layers have the same index
        if (xIndex == yIndex) {
          // attempt to merge those two layers
          xLayer mergeWith yLayer map (_ :: repair(xs, ys)) getOrElse {

            // if the two layers are incompatible
            // then order the stacks appropriately
            // and then continue to recurse down the stack
            //
            if (foldContext.shouldFoldAbove)
              xLayer :: yLayer :: repair(xs, ys)
            else
              yLayer :: xLayer :: repair(xs, ys)

          }
        } else {
          // if the layers have differing indexes
          // then we want to 'skip' all the items on a particular stack
          // until the layers have the same indexes, or we run out of layers
          //
          if (xIndex < yIndex) {
            if (foldContext.shouldFoldAbove)
              repair(xs, foldedLayers) :+ xLayer
            else
              xLayer :: repair(xs, foldedLayers)

          } else {
            if (foldContext.shouldFoldAbove)
              yLayer :: repair(unaffectedLayers, ys)
            else
              repair(unaffectedLayers, ys) :+ yLayer
          }
        }
    }
  }
}
