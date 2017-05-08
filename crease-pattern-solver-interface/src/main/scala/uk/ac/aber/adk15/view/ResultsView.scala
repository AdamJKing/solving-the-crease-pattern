package uk.ac.aber.adk15.view

import uk.ac.aber.adk15.paper.CreasePattern
import uk.ac.aber.adk15.paper.fold.Fold
import uk.ac.aber.adk15.view.shapes.Model

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.beans.property.DoubleProperty
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

  width = 340
  height = 350
  minWidth = 150.0
  private var CanvasSize = DoubleProperty(300)
  CanvasSize <== width - 40

  scene = new Scene {
    content = {
      // we generate the steps by applying the folds and drawing the model at each stage
      val children = foldOrder.inits map (folds => (originalPattern /: folds)(_ <~~ _))

      val listView = new ListView[CreasePattern](children.toSeq.reverse) {
        cellFactory = _ => new CanvasCell()
        fixedCellSize <== CanvasSize
        prefWidth <== CanvasSize + 40
        minWidth = 150.0
      }

      listView.prefHeight <== height
      listView
    }
  }

  onCloseRequest = handle(Platform.exit())

  /**
    * Specialised cell for the JFX list view
    */
  private class CanvasCell extends ListCell[CreasePattern] {
    private val canvas               = new Canvas { resizable = true }
    private implicit val gc          = canvas.graphicsContext2D
    private var model: CreasePattern = CreasePattern.empty

    style = "-fx-padding: 0px"

    canvas.width <== CanvasSize
    canvas.height <== CanvasSize

    canvas.width.onChange(
      new Model(model, (20, CanvasSize.value - 20), (20, CanvasSize.value - 20)).draw)

    graphic = new Pane() { children = canvas }
    contentDisplay = ContentDisplay.GraphicOnly

    item.onChange { (_, _, model) =>
      this.model = model
      new Model(model, (20, CanvasSize.value - 20), (20, CanvasSize.value - 20)).draw
    }
  }
}
