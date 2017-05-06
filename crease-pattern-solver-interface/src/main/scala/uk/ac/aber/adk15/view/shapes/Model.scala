package uk.ac.aber.adk15.view.shapes

import uk.ac.aber.adk15.geometry.DistanceFromOriginPointOrdering
import uk.ac.aber.adk15.paper._
import uk.ac.aber.adk15.paper.fold.{MountainFold, ValleyFold}

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

    val boundingBox   = (model.layers map (_.boundingBox)) reduce (_ combineWith _)
    val farthestPoint = boundingBox.points.max(DistanceFromOriginPointOrdering)
    val largest       = math.max(farthestPoint.x, farthestPoint.y)

    gc.fill = Color.AntiqueWhite

    def normalise(x: Double, y: Double) =
      (xMin + (x / largest) * xMax, yMin + (y / largest) * yMax)

    model.layers.reverse foreach { layer =>
      val foldsToShade   = (layer.unfoldable map (_.line)).toList
      val distinctPoints = (foldsToShade flatMap (_ map (Seq(_, _)))).distinct

      // fills every possible triangle in the shape
      // even if they overlap
      // this is because while we have the points in the polygon
      // there are inherently unordered, which causes issues when using the
      // `fillPolygon` command
      val groups = distinctPoints.combinations(3)
      groups foreach (group => gc.fillPolygon(group map (p => normalise(p.x, p.y))))

      (layer.unfoldable ++ layer.foldable) foreach (fold => {
        val (a, b) = fold.line.points

        val (x1, y1) = normalise(a.x, a.y)
        val (x2, y2) = normalise(b.x, b.y)

        fold.foldType match {
          case ValleyFold =>
            gc.stroke = Color.Red
            gc setLineDashes 10d

          case MountainFold =>
            gc.stroke = Color.Blue
            gc setLineDashes (15d, 7.5d, 2.5d, 7.5d)

          case _ =>
            gc.stroke = Color.Black
            gc setLineDashes 0d
        }

        gc.strokeLine(x1, y1, x2, y2)
      })
    }
  }
}
