package uk.ac.aber.adk15.view

import uk.ac.aber.adk15.paper.CreasePattern
import uk.ac.aber.adk15.paper.fold.Fold
import uk.ac.aber.adk15.view.shapes.Model

import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{ContentDisplay, ListCell, ListView}
import scalafx.scene.layout.Pane
import scalafx.stage.Stage

/**
  * Displays the discovered fold-order to the user
  *
  * @param foldOrder the discovered fold-order
  * @param originalPattern the pattern to apply the fold-order to
  */
class ResultsView(foldOrder: Seq[Fold], originalPattern: CreasePattern) extends Stage {

  private val CanvasSize = 200.0

  resizable = false

  scene = new Scene {
    content = {
      // we generate the steps by applying the folds and drawing the model at each stage
      val children = foldOrder.inits map (folds => (originalPattern /: folds)(_ <~~ _))

      new ListView[CreasePattern](children.toSeq.reverse) {
        cellFactory = _ => new CanvasCell()
        fixedCellSize = 200.0
      }
    }
  }

  /**
    * Specialised cell for the JFX list view
    */
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
