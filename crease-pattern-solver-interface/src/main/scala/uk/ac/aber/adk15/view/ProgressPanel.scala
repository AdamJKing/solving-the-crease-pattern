package uk.ac.aber.adk15.view

import com.google.inject.Inject
import uk.ac.aber.adk15.executors.ant._
import uk.ac.aber.adk15.paper.{CreasePattern, MountainFold, Point, ValleyFold}

import scala.collection.concurrent.TrieMap
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.layout.TilePane

class ProgressPanel @Inject()(antTraverser: AntTraverser)
    extends TilePane
    with Observer[AntTraversalEvent] {

  private var canvasses       = new TrieMap[Long, Canvas]()
  private var maxCanvasHeight = 0.0
  private var maxCanvasWidth  = 0.0

  antTraverser.subscribe(this, classOf[AntTraversalEvent])

  prefWidth = 100.0
  prefHeight = 100.0

  override def onSuccess(event: AntTraversalEvent): Unit = {
    val canvas = getCanvas(event.id)
    drawTick(implicitly(canvas.graphicsContext2D))
  }

  override def onFailure(event: AntTraversalEvent): Unit = {
    val canvas = getCanvas(event.id)
    drawCross(implicitly(canvas.graphicsContext2D))
  }

  override def onUpdate(event: AntTraversalEvent): Unit = {
    val canvas = getCanvas(event.id)
    drawModel(event.model)(implicitly(canvas.graphicsContext2D))
  }

  private def getCanvas(id: Long) =
    canvasses getOrElseUpdate (id, createCanvas())

  private def drawTick(implicit graphicsContext: GraphicsContext)  = {}
  private def drawCross(implicit graphicsContext: GraphicsContext) = {}

  private def drawModel(model: CreasePattern)(implicit graphicsContext: GraphicsContext): Unit = {
    def max(op: Point => Double) = (model.folds flatMap (_.toSet) map op).max
    def min(op: Point => Double) = (model.folds flatMap (_.toSet) map op).min

    lazy val (xCeiling, xFloor) = (max(_.x), min(_.x))
    lazy val (yCeiling, yFloor) = (max(_.y), min(_.y))

    model.folds foreach { fold =>
      val (start, end) = (fold.start, fold.end)

      def normalise(x: Double, y: Double) =
        ((x * maxCanvasWidth - 10) / xCeiling + 5, (y * maxCanvasHeight - 10) / yCeiling + 5)

      val (x1, y1) = normalise(start.x, start.y)
      val (x2, y2) = normalise(end.x, end.y)

      fold.foldType match {
        case MountainFold => graphicsContext setLineDashes 10d
        case ValleyFold   => graphicsContext setLineDashes (30d, 15d, 5d, 15d)
        case _            => graphicsContext setLineDashes 0d
      }
    }
  }

  private def createCanvas() = {
    val canvas = new Canvas

    children add canvas

    maxCanvasWidth = this.getWidth / children.size()
    maxCanvasHeight = this.getHeight / children.size()

    children forEach (node => {
      node.prefWidth(maxCanvasWidth)
      node.prefHeight(maxCanvasHeight)
    })

    canvas
  }
}
