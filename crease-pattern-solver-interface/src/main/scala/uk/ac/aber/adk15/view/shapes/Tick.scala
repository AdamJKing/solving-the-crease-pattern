package uk.ac.aber.adk15.view.shapes

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

/**
  * Draws a [[Color.Green]] tick into the given [[GraphicsContext]];
  * painting it onto the [[scalafx.scene.canvas.Canvas]].
  *
  * @param xBounds the lowest and highest allowed X values in the form (min, max)
  * @param yBounds the lowest and highest allowed Y values in the form (min, max)
  */
final class Tick(xBounds: (Double, Double), yBounds: (Double, Double))
    extends Drawable(xBounds, yBounds) {

  protected val lineWidth = 8
  protected val colour    = Color.Green

  override def draw(implicit gc: GraphicsContext): Unit = {
    super.draw

    val points = Seq((xMin, yMax / 2), (xMax / 2, yMax), (xMax, yMin))
    (points sliding 2) foreach {
      case Seq(a: (Double, Double), b: (Double, Double)) =>
        gc.strokeLine(a._1, a._2, b._1, b._2)
    }
  }
}
