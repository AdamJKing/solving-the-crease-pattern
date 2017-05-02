package uk.ac.aber.adk15.view.shapes

import uk.ac.aber.adk15.geometry.{DistanceFromOriginPointOrdering, RectangleSizeOrdering}
import uk.ac.aber.adk15.paper._

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

/**
  * Draws a depiction of a [[uk.ac.aber.adk15.paper.CreasePattern]] using [[Color.Black]].
  *
  * In the same way that the pattern considers there to be an inherent order in layers,
  * this class will draw the layers in the same order.
  *
  * @param xBounds the lowest and highest allowed X values in the form (min, max)
  * @param yBounds the lowest and highest allowed Y values in the form (min, max)
  */
final class Model(model: CreasePattern, xBounds: (Double, Double), yBounds: (Double, Double))
    extends Drawable(xBounds, yBounds) {

  protected val lineWidth = 3d
  protected val colour    = Color.Black

  override def draw(implicit gc: GraphicsContext): Unit = {
    super.draw

    val boundingBox   = (model.layers map (_.boundingBox)).max(RectangleSizeOrdering)
    val farthestPoint = boundingBox.points.max(DistanceFromOriginPointOrdering)
    val largest       = math.max(farthestPoint.x, farthestPoint.y)

    gc.fill = Color.AntiqueWhite

    def normalise(x: Double, y: Double) =
      (xMin + (x / largest) * xMax, yMin + (y / largest) * xMax)

    model.layers.reverse foreach { layer =>
      val foldsToShade = (layer.unfoldable map (_.line)).toList
      val pointsMap    = (for (fold <- foldsToShade) yield fold.a -> fold.b).toMap
      val points       = for (point <- pointsMap.keys) yield pointsMap(point)

      gc.fillPolygon(points.toList map (p => normalise(p.x, p.y)))

      layer.unfoldable foreach (fold => {
        val (a, b) = fold.line.points

        val (x1, y1) = normalise(a.x, a.y)
        val (x2, y2) = normalise(b.x, b.y)

//        `type` match {
//          case MountainFold => gc setLineDashes 10d
//          case ValleyFold   => gc setLineDashes (30d, 15d, 5d, 15d)
//          case _            => gc setLineDashes 0d
//        }

        gc.strokeLine(x1, y1, x2, y2)
      })
    }
  }
}
