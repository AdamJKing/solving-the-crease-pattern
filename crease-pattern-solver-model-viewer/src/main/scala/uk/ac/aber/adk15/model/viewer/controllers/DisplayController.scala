package uk.ac.aber.adk15.model.viewer.controllers[Fold]

@sfxml
class DisplayController(private val modelView: Pane) {

  private val logger = Logger[DisplayController]

  val canvas = new Canvas {
    width = modelView.getPrefWidth
    height = modelView.getPrefHeight
    logger debug s"Creating a canvas of height ${height.toDouble} and width ${width.toDouble}"
  }

  modelView.children add canvas
  private val graphicsContext = canvas.graphicsContext2D

  graphicsContext.stroke = Black
  graphicsContext.lineWidth = 2.5d

  private val myGraph = CreasePattern(
    Set[Fold](
      PaperEdge(Point(0, 0), Point(100, 0), PaperBoundary),
      PaperEdge(Point(0, 100), Point(0, 0), PaperBoundary),
      PaperEdge(Point(0, 100), Point(100, 0), MountainFold),
      PaperEdge(Point(0, 100), Point(100, 100), PaperBoundary),
      PaperEdge(Point(100, 0), Point(100, 100), PaperBoundary)
    ))

  private lazy val xCeiling = myGraph.edges
    .flatMap(p => Traversable(p.start.x, p.end.x))
    .max

  private lazy val yCeiling = myGraph.edges
    .flatMap(p => Traversable(p.start.y, p.end.y))
    .max

  logger debug s"xCeiling is $xCeiling, yCeiling is $yCeiling"

  myGraph.edges.foreach((p: PaperEdge) => {
    def normalise(x: Double, y: Double) =
      ((x * (canvas.getWidth - 10d) / xCeiling) + 5d,
       (y * (canvas.getHeight - 10d) / yCeiling) + 5d)

    val (x1, y1) = normalise(p.start.x, p.start.y)
    val (x2, y2) = normalise(p.end.x, p.end.y)

    p.foldType match {
      case MountainFold => graphicsContext setLineDashes 10d
      case ValleyFold   => graphicsContext setLineDashes (30d, 15d, 5d, 15d)
      case _            => graphicsContext setLineDashes 0d
    }

    logger debug s"Drawing a line of {$x1, $y1}, {$x2, $y2}"
    graphicsContext strokeLine (x1, y1, x2, y2)
  })
}
