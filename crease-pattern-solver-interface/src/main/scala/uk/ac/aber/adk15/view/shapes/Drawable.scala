package uk.ac.aber.adk15.view.shapes

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

abstract class Drawable(xBounds: (Double, Double), yBounds: (Double, Double))  {

  protected[this] val (xMin, xMax) = xBounds
  protected[this] val (yMin, yMax) = yBounds

  protected[this] val lineWidth: Double
  protected[this] val colour: Color

  def draw(implicit gc: GraphicsContext): Unit = {
    gc.clearRect(xMin, yMin, xMax, yMax)

    gc.lineWidth = lineWidth
    gc.stroke = colour
  }
}
