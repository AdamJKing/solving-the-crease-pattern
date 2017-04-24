package uk.ac.aber.adk15.view.shapes

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

  protected val lineWidth = 5d
  protected val colour    = Color.Black

  override def draw(implicit gc: GraphicsContext): Unit = {
    super.draw

    def max(op: Point => Double) =
      (for {
        layer <- model.layers
        fold  <- layer.folds
        point <- fold.toSet
      } yield op(point)).max

    val largest = math.max(max(_.x), max(_.y))

    gc.fill = Color.AntiqueWhite

    def normalise(x: Double, y: Double) =
      (xMin + (x / largest) * xMax, yMin + (y / largest) * xMax)

    model.layers foreach { layer =>
      val foldsToShade = (layer.creasedFolds ++ layer.paperBoundaries).toList

      val externalPoints = foldsToShade.flatMap(_.toSet).map(p => normalise(p.x, p.y)).distinct

      gc.fillPolygon(externalPoints.sortBy(Function.tupled((a, b) => a + b)))

      layer.folds.reverse foreach (fold => {
        val (start, end) = (fold.start, fold.end)

        lazy val (x1, y1) = normalise(start.x, start.y)
        lazy val (x2, y2) = normalise(end.x, end.y)

        fold.foldType match {
          case MountainFold => gc setLineDashes 10d
          case ValleyFold   => gc setLineDashes (30d, 15d, 5d, 15d)
          case _            => gc setLineDashes 0d
        }

        gc.strokeLine(x1, y1, x2, y2)
      })
    }
  }
}
