package uk.ac.aber.adk15.view

import uk.ac.aber.adk15.paper.CreasePatternPredef.Helpers._
import uk.ac.aber.adk15.paper.{CreasePattern, Fold}
import uk.ac.aber.adk15.view.shapes.Model

import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{ContentDisplay, ListCell, ListView}
import scalafx.scene.layout.Pane
import scalafx.stage.Stage

class ResultsView(foldOrder: Seq[Fold], originalPattern: CreasePattern) extends Stage {

  private val CanvasSize = 200.0

  resizable = false

  scene = new Scene {
    content = {
      val children = foldOrder.inits map (folds => (originalPattern /: folds)(_ <~~ _))

      new ListView[CreasePattern](children.toSeq.reverse) {
        cellFactory = _ => new CanvasCell()
        fixedCellSize = 200.0
      }
    }
  }

  private class CanvasCell extends ListCell[CreasePattern] {
    private val canvas      = new Canvas
    private implicit val gc = canvas.graphicsContext2D

    style = "-fx-padding: 0px"
    canvas.width = CanvasSize
    canvas.height = CanvasSize

    graphic = new Pane() { children = canvas }
    contentDisplay = ContentDisplay.GraphicOnly

    item.onChange { (_, _, model) =>
      new Model(model, (20, CanvasSize - 20), (20, CanvasSize - 20)).draw
    }
  }
}
