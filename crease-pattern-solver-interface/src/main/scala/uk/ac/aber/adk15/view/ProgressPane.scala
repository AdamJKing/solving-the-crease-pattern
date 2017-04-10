package uk.ac.aber.adk15.view

import com.google.inject.Inject
import uk.ac.aber.adk15.executors.ant._
import uk.ac.aber.adk15.view.shapes.{Cross, Drawable, Model, Tick}

import scala.collection.concurrent.TrieMap
import scalafx.application.Platform
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.{Pane, TilePane}

class ProgressPane @Inject()(antTraverser: AntTraverser, eventBus: EventBus[AntTraversalEvent])
    extends TilePane
    with Observer[AntTraversalEvent] {

  private val canvasses = new TrieMap[Long, Canvas]()
  private val idealCanvasSize = 100.0
  private val offset = idealCanvasSize / 10.0

  eventBus subscribe this

  hgap = 10.0
  vgap = 10.0

  prefTileWidth = idealCanvasSize
  prefTileHeight = idealCanvasSize

  prefColumns = 4
  prefRows = 2

  override def onSuccess(event: AntTraversalEvent): Unit = {
    update(event, Set(
      new Model(event.model, (offset, idealCanvasSize - offset), (offset, idealCanvasSize - offset)),
      new Tick((offset, idealCanvasSize - offset), (offset, idealCanvasSize - offset))
      ))
  }

  override def onFailure(event: AntTraversalEvent): Unit = {
    update(event, Set(
      new Model(event.model, (offset, idealCanvasSize - offset), (offset, idealCanvasSize - offset)),
      new Cross((offset, idealCanvasSize - offset), (offset, idealCanvasSize - offset))
    ))
  }

  override def onUpdate(event: AntTraversalEvent): Unit = {
    update(event, Set(
      new Model(event.model, (offset, idealCanvasSize - offset), (offset, idealCanvasSize - offset))
    ))
  }

  private def update(event: AntTraversalEvent, objects: Set[Drawable]) = {
    Platform.runLater(() => {
      val canvas = getOrCreateCanvas(event.id)
      implicit val graphicsContext = canvas.graphicsContext2D

      objects foreach (_.draw)
    })
  }

  private def getOrCreateCanvas(id: Long) = {
    canvasses getOrElseUpdate (id, createCanvas())
  }

  private def createCanvas() = {
    val newCanvas = new Canvas(idealCanvasSize, idealCanvasSize)

    children = (canvasses.values ++ Seq(newCanvas)) map (canvas => {
      val container = new Pane() {
        minWidth = idealCanvasSize
        minHeight = idealCanvasSize
        maxWidth = idealCanvasSize
        maxHeight = idealCanvasSize
        style = "-fx-background-color: white;"
      }

      container.children add canvas
      container
    })

    requestLayout()
    newCanvas
  }
}
