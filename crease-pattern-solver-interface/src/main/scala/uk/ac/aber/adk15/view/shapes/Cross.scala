package uk.ac.aber.adk15.view.shapes

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

/**
  * Draws a [[Color.Red]] cross (X) into the given [[GraphicsContext]];
  * painting it onto the [[scalafx.scene.canvas.Canvas]].
  *
  * @param xBounds the lowest and highest allowed X values in the form (min, max)
  * @param yBounds the lowest and highest allowed Y values in the form (min, max)
  */
final class Cross(xBounds: (Double, Double), yBounds: (Double, Double))
  extends Drawable(xBounds, yBounds) {

  protected val lineWidth = 6
  protected val colour    = Color.Red

  override def draw(implicit gc: GraphicsContext): Unit = {
    super.draw

    gc.strokeLine(xMin, yMin, xMax, yMax)
    gc.strokeLine(xMin, yMax, xMax, yMin)
  }
}
