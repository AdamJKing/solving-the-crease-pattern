package uk.ac.aber.adk15.view

import com.google.inject.Inject
import uk.ac.aber.adk15.executors.ant._
import uk.ac.aber.adk15.view.shapes.{Cross, Drawable, Model, Tick}

import scala.collection.concurrent.TrieMap
import scalafx.application.Platform
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.{Pane, TilePane}

/**
  * Represents the portion of the UI that will be updated by the [[EventBus]].
  * Reports on the progress of the application to the user
  *
  * @param eventBus the event bus to recieve events from
  */
class ProgressPane @Inject()(eventBus: EventBus[AntTraversalEvent])
    extends TilePane
    with Observer[AntTraversalEvent] {

  // canvasses need to be animating at the same time
  // so are stored and updated asynchronously
  private val canvasses = new TrieMap[Long, Canvas]()

  // this is the size the canvas will aim to be in the application
  // this is currently hardcoded because it is easier than trying
  // to dynamically gauge what size we should use at boot
  private val idealCanvasSize = 100.0

  // offset is used to create margins for the animations
  private val offset           = idealCanvasSize / 10.0
  private val offsetCanvasSize = (offset, idealCanvasSize - (1.75 * offset))

  // subscribe this panel to events
  eventBus subscribe this

  hgap = 10.0
  vgap = 10.0

  prefTileWidth = idealCanvasSize
  prefTileHeight = idealCanvasSize

  prefColumns = 4
  prefRows = 2

  def refresh(): Unit = {
    children.clear()
    canvasses.clear()
  }

  /**
    * Respond to the success event
    * @param event success event
    */
  override def onSuccess(event: AntTraversalEvent): Unit = {
    update(
      event,
      Set(
        new Model(event.model, offsetCanvasSize, offsetCanvasSize),
        new Tick(offsetCanvasSize, offsetCanvasSize)
      )
    )
  }

  /**
    * Respond to the fail event
    * @param event fail event
    */
  override def onFailure(event: AntTraversalEvent): Unit = {
    update(
      event,
      Set(
        new Model(event.model, offsetCanvasSize, offsetCanvasSize),
        new Cross(offsetCanvasSize, offsetCanvasSize)
      )
    )
  }

  /**
    * respond to a generic update event
    * @param event the generic event
    */
  override def onUpdate(event: AntTraversalEvent): Unit = {
    update(event,
           Set(
             new Model(event.model, offsetCanvasSize, offsetCanvasSize)
           ))
  }

  /**
    * Update the animation of a canvas.
    *
    * @param event the event to animate
    * @param objects the items to draw to the canvas
    */
  private def update(event: AntTraversalEvent, objects: Set[Drawable]) = {
    Platform.runLater(() => {
      val canvas                   = getOrCreateCanvas(event.id)
      implicit val graphicsContext = canvas.graphicsContext2D

      objects foreach (_.draw)
    })
  }

  /**
    * Get the canvas for the corresponding event ID.
    * If one doesn't exist, creates one.
    * @param id the event ID
    * @return the canvas for this ID
    */
  private def getOrCreateCanvas(id: Long) = {
    canvasses getOrElseUpdate (id, createCanvas())
  }

  /**
    * Creates a new canvas.
    * The new canvas is added to the progress pane with a
    * container panel for formatting reasons
    * @return a new canvas
    */
  private def createCanvas() = {
    val canvasSize = idealCanvasSize
    val newCanvas  = new Canvas(canvasSize, canvasSize)

    children = (canvasses.values ++ Seq(newCanvas)) map (canvas => {
      val container = new Pane() {
        minWidth = idealCanvasSize
        minHeight = idealCanvasSize
        maxWidth = idealCanvasSize
        maxHeight = idealCanvasSize
        style = "-fx-background-color: white; -fx-border-width: 2px; -fx-border-color: black;"
      }

      container.children add canvas
      container
    })

    // ask the progress pane to update it's layout based on our new children
    requestLayout()
    newCanvas
  }
}
