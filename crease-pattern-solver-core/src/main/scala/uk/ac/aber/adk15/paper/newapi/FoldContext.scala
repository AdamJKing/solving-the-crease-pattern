package uk.ac.aber.adk15.paper.newapi

protected class FoldContext(creasePattern: NewCreasePattern, fold: NewFold) {

  private lazy val affectedLayers = creasePattern.layers filter isFoldable
  private lazy val segmentedLayers = {
    val (left, right) = segmentLayers(affectedLayers)
    if (surfaceArea(left) < surfaceArea(right))
      (left, right)
    else
      (right, left)
  }

  lazy val layersToFold: List[NewPaperLayer]  = segmentedLayers._1
  lazy val layersToLeave: List[NewPaperLayer] = segmentedLayers._2

  lazy val unaffectedLayers: List[NewPaperLayer] = creasePattern.layers diff affectedLayers

  private def isFoldable(layer: NewPaperLayer) =
    (layer contains fold) || !(layer coversLine fold.line)

  private def segmentLayers(layers: List[NewPaperLayer]) =
    (affectedLayers map (_ segmentOnLine fold.line)).unzip

  private def surfaceArea(layers: List[NewPaperLayer]) = (layers map (_.surfaceArea)).max
}
