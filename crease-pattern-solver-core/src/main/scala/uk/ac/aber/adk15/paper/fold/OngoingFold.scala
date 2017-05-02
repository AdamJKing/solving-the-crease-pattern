package uk.ac.aber.adk15.paper.fold

import uk.ac.aber.adk15.paper.{CreasePattern, PaperLayer}

import scala.Function.tupled

class OngoingFold(private val foldContext: FoldContext) {

  def crease: CreasePattern = {
    val indexOfFold = {
      if (foldContext.foldAbove)
        foldContext.foldableLayers.keys.min
      else
        foldContext.foldableLayers.keys.max
    }

    val foldedMap = foldContext.foldableLayers map tupled((index, layer) => {
      val d = math.abs(indexOfFold - index)
      val shiftedIndex =
        if (foldContext.foldAbove) index - (2 * d + 1)
        else index + (2 * d + 1)

      shiftedIndex -> (layer rotateAround foldContext.foldLine)
    })
    val unaffectedMap = foldContext.unaffectedLayers

    new CreasePattern(
      repair(unaffectedMap.toList sortBy (_._1), foldedMap.toList sortBy (_._1))
    )
  }

  private def repair(unaffectedLayers: List[(Int, PaperLayer)],
                     foldedLayers: List[(Int, PaperLayer)]): List[PaperLayer] = {

    if (unaffectedLayers.isEmpty) return foldedLayers map (_._2)
    if (foldedLayers.isEmpty) return unaffectedLayers map (_._2)

    (unaffectedLayers, foldedLayers) match {
      case ((xIndex, xLayer) :: xs, (yIndex, yLayer) :: ys) =>
        if (xIndex == yIndex) {
          xLayer mergeWith yLayer map (_ :: repair(xs, ys)) getOrElse {
            if (foldContext.foldAbove)
              xLayer :: yLayer :: repair(xs, ys)
            else
              yLayer :: xLayer :: repair(xs, ys)
          }
        } else {
          if (xIndex < yIndex) {
            if (foldContext.foldAbove)
              repair(xs, foldedLayers) :+ xLayer
            else
              xLayer :: repair(xs, foldedLayers)
          } else {
            if (foldContext.foldAbove)
              yLayer :: repair(unaffectedLayers, ys)
            else
              repair(unaffectedLayers, ys) :+ yLayer
          }
        }
    }
  }
}
