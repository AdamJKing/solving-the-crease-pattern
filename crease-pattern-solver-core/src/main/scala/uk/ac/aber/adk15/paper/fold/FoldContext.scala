package uk.ac.aber.adk15.paper.fold

import uk.ac.aber.adk15.geometry.Line
import uk.ac.aber.adk15.paper.{CreasePattern, PaperLayer}

import scala.collection.mutable

class FoldContext(private val creasePattern: CreasePattern, private val fold: Fold) {

  private var stackA       = mutable.Map[Int, PaperLayer]()
  private var stackB       = mutable.Map[Int, PaperLayer]()
  private var unattributed = mutable.Map[Int, PaperLayer]()

  for ((layer, index) <- creasePattern.layers.zipWithIndex) {
    if (layer.isFoldable) {
      val (layerA, layerB) = layer segmentOnLine fold.line
      layerA foreach (layer => stackA += (index -> layer))
      layerB foreach (layer => stackB += (index -> layer))
    } else {
      unattributed += (index -> layer)
    }
  }

  lazy val (foldableLayers, unaffectedLayers): (Map[Int, PaperLayer], Map[Int, PaperLayer]) = {
    if (surfaceArea(stackA.values) < surfaceArea(stackB.values)) {
      (stackA.toMap, stackB.toMap ++ unattributed)
    } else {
      (stackB.toMap, stackA.toMap ++ unattributed)
    }
  }

  val foldAbove: Boolean = fold.foldType == ValleyFold

  def foldLine: Line = fold.line

  private def surfaceArea(layers: Iterable[PaperLayer]) = {
    if (layers.nonEmpty) (layers map (_.surfaceArea)).max
    else 0.0
  }

  private implicit class PaperLayerOps(private val layer: PaperLayer) {
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
