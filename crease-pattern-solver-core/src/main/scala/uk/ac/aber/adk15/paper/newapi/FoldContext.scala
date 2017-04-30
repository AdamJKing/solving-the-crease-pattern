package uk.ac.aber.adk15.paper.newapi

import uk.ac.aber.adk15.geometry.Line
import uk.ac.aber.adk15.paper.ValleyFold

import scala.collection.mutable

protected class FoldContext(private val creasePattern: NewCreasePattern, private val fold: NewFold) {

  private var stackA       = mutable.Map[Int, NewPaperLayer]()
  private var stackB       = mutable.Map[Int, NewPaperLayer]()
  private var unattributed = mutable.Map[Int, NewPaperLayer]()

  for ((layer, index) <- creasePattern.layers.zipWithIndex) {
    if (layer.isFoldable) {
      val (layerA, layerB) = layer segmentOnLine fold.line
      stackA += (index -> layerA)
      stackB += (index -> layerB)
    } else {
      unattributed += (index -> layer)
    }
  }

  lazy val (foldableLayers, unaffectedLayers) = {
    if (surfaceArea(stackA.values) < surfaceArea(stackB.values)) {
      (stackA.toMap, stackB.toMap ++ unattributed)
    } else {
      (stackB.toMap, stackA.toMap ++ unattributed)
    }
  }

  val foldAbove: Boolean = fold.foldType == ValleyFold

  def foldLine: Line = fold.line

  //  private def segmentLayers(layers: List[NewPaperLayer]) =
  //    (affectedLayers map (_ segmentOnLine fold.line)).unzip
  //

  private def surfaceArea(layers: Iterable[NewPaperLayer]) = (layers map (_.surfaceArea)).max

  private implicit class NewPaperLayerOps(private val layer: NewPaperLayer) {
    def isFoldable: Boolean = (layer contains fold) || !(layer coversLine fold.line)
  }
}
