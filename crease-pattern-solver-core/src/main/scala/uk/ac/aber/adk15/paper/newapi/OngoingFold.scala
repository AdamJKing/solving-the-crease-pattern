package uk.ac.aber.adk15.paper.newapi

import scala.Function.tupled

class OngoingFold(private val foldContext: FoldContext) {

  def crease: NewCreasePattern = {
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

    new NewCreasePattern(
      repair(unaffectedMap.toList sortBy (_._1), foldedMap.toList sortBy (_._1))
    )
  }

  private def repair(unaffectedLayers: List[(Int, NewPaperLayer)],
                     foldedLayers: List[(Int, NewPaperLayer)]): List[NewPaperLayer] = {

    if (unaffectedLayers.isEmpty) return foldedLayers map (_._2)
    if (foldedLayers.isEmpty) return unaffectedLayers map (_._2)

    (unaffectedLayers, foldedLayers) match {
      case ((xIndex, xLayer) :: xs, (yIndex, yLayer) :: ys) =>
        if (xIndex == yIndex) {
          xLayer mergeWith yLayer map (_ :: repair(xs, ys)) getOrElse {
            if (foldContext.foldAbove)
              yLayer :: xLayer :: repair(xs, ys)
            else
              xLayer :: yLayer :: repair(xs, ys)
          }
        } else {
          if (xIndex < yIndex) {
            xLayer :: repair(xs, foldedLayers)
          } else {
            yLayer :: repair(ys, unaffectedLayers)
          }
        }
    }
  }
}
