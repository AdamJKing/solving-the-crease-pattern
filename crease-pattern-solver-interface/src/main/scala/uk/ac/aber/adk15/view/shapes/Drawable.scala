package uk.ac.aber.adk15.view.shapes

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

/**
  * Represents a single item that can be drawn by the [[GraphicsContext]]
  *
  * @param xBounds the bounds of the stage being drawn to in the format (min, max)
  * @param yBounds the bounds of the stage being drawn to in the format (min, max)
  */
abstract class Drawable(xBounds: (Double, Double), yBounds: (Double, Double)) {

  protected[this] val (xMin, xMax) = xBounds
  protected[this] val (yMin, yMax) = yBounds

  protected[this] val lineWidth: Double
  protected[this] val colour: Color

  /**
    * Clears the stage and draws the object.
    *
    * @param gc the graphics context to draw to
    */
  def draw(implicit gc: GraphicsContext): Unit = {
    gc.clearRect(0, 0, gc.getCanvas.getWidth, gc.getCanvas.getHeight)

    gc.lineWidth = lineWidth
    gc.stroke = colour
  }
}
