package uk.ac.aber.adk15.paper.fold

import uk.ac.aber.adk15.geometry.Line
import uk.ac.aber.adk15.paper.{CreasePattern, PaperLayer}

import scala.collection.mutable

/**
  * Contains contextual information about an [[OngoingFold]], such as
  * layers should or should not be modified as well as the fold-type.
  *
  * @param creasePattern the crease pattern being folded
  * @param fold the fold being folded
  */
class FoldContext(private val creasePattern: CreasePattern, private val fold: Fold) {

  // The layers in the model can be split into the following:
  //
  // StackA is one half of the layers split by the fold
  private var stackA = mutable.Map[Int, PaperLayer]()
  //
  // StackB is the other half of the layers split by the fold
  private var stackB = mutable.Map[Int, PaperLayer]()
  //
  // finally some layers will be 'unattributed', which means
  // they stay the same regardless of the fold
  private var unattributed = mutable.Map[Int, PaperLayer]()

  // we attach the index as an identifier to the layer
  // this helps us when we want to reposition them later
  //
  for ((layer, index) <- creasePattern.layers.zipWithIndex) {
    // select layers using the fold-ability rule
    if (layer.isFoldable) {
      val (layerA, layerB) = layer segmentOnLine fold.line

      // sometimes segmenting a layer will not produce anything
      // so we treat the new layers as [[Option]]s
      layerA foreach (layer => stackA += (index -> layer))
      layerB foreach (layer => stackB += (index -> layer))
    } else {

      // if a layer is not foldable, it is considered 'unattributed'
      unattributed += (index -> layer)
    }
  }

  // in this section we decide which half of the layers should be folded
  // and which half they should be folded on top of
  lazy val (foldableLayers, unaffectedLayers): (Map[Int, PaperLayer], Map[Int, PaperLayer]) = {

    // we use surface area as a measure to decide which half to fold
    // in reality this distinction is entirely arbitrary
    // but we must choose a side.
    //
    // in this instance we always prefer to fold the smaller side,
    // as a smaller surface area implies a simpler fold
    // (though this is not always true).
    //
    if (surfaceArea(stackA.values) < surfaceArea(stackB.values)) {
      // both stacks are already maps
      // we use .toMap to convert a mutable map to an immutable one
      (stackA.toMap, stackB.toMap ++ unattributed)
    } else {
      (stackB.toMap, stackA.toMap ++ unattributed)
    }
  }

  // indicates if we are working with a [[ValleyFold]] or a [[MountainFold]]
  val shouldFoldAbove: Boolean = fold.foldType == ValleyFold

  /**
    * @return the line of the fold
    */
  def foldLine: Line = fold.line

  /**
    * Return the maximum surface area covered by a stack of layers.
    * Note, this does not take the average nor the sum, it takes the surface
    * area of the largest layer.
    *
    * @param layers the layers to find the maximum surface area of
    * @return the maximum surface area, 0.0 for empty layers
    */
  private def surfaceArea(layers: Iterable[PaperLayer]) = {
    if (layers.nonEmpty) (layers map (_.surfaceArea)).max
    else 0.0
  }

  /**
    * Enhancement class for checking the fold-ability of a layer.
    *
    * This type of class is affectionately known as a pimp-my-class pattern
    * by the Scala community.
    *
    * @param layer the layer to enhance
    */
  private implicit class PaperLayerOps(private val layer: PaperLayer) {

    /**
      * Determines if a given layer is foldable.
      * A layer is foldable if it either contains the fold or does nothing
      * to block that fold.
      *
      * Note: This should not be conflated with fold ''legality''.
      *       ie. this function is for determining if a layer would be '''affected'''
      *       by a fold and '''not''' if this fold is legal for this layer
      *
      * @return if the layer is foldable or not
      */
    def isFoldable: Boolean = {
      val containsFold   = layer contains fold
      val oneFoldOnEdge  = fold.line map ((a, b) => (layer isOnEdge a) || (layer isOnEdge b))
      val bothFoldOnEdge = fold.line map ((a, b) => (layer isOnEdge a) && (layer isOnEdge b))
      val foldBlocked    = layer coversLine fold.line

      if (containsFold) true
      else if (!foldBlocked) bothFoldOnEdge ^ oneFoldOnEdge
      else false
    }
  }
}
