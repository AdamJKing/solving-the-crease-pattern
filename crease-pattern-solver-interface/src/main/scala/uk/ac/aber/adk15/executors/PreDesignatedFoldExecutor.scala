package uk.ac.aber.adk15.executors
import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.Point
import uk.ac.aber.adk15.paper.CreasePattern
import uk.ac.aber.adk15.paper.CreasePatternPredef.Helpers._
import uk.ac.aber.adk15.paper.CreasePatternPredef.{Fold, Layer}
import uk.ac.aber.adk15.paper.PaperEdgeHelpers._

class PreDesignatedFoldExecutor extends FoldExecutor {

  private val logger = Logger[PreDesignatedFoldExecutor]

  override def findFoldOrder(ignored: CreasePattern): List[Fold] = {
    val creasePattern = CreasePattern(
      Layer(
        Point(0, 0) -- Point(50, 0),
        Point(50, 0) -- Point(100, 0),
        Point(0, 0) -- Point(0, 50),
        Point(0, 50) -- Point(0, 100),
        Point(0, 100) -- Point(100, 100),
        Point(100, 0) -- Point(100, 100),
        Point(0, 0) \/ Point(25, 25),
        Point(25, 25) \/ Point(50, 50),
        Point(50, 50) \/ Point(100, 100),
        Point(0, 100) \/ Point(50, 50),
        Point(50, 50) /\ Point(100, 0),
        Point(0, 50) /\ Point(25, 25),
        Point(25, 25) \/ Point(50, 0)
      )
    )

    val creases = List(Point(50, 50) \/ Point(100, 100),
                       Point(0, 50) /\ Point(25, 25),
                       Point(0, 100) \/ Point(50, 50))

    logger info "starting..."

    creases.foldLeft(creasePattern)((pattern, crease) => {
      val newPattern = pattern <~~ crease
      logger info s"folding $crease"
      newPattern
    })

    logger info "found final crease pattern"
    creases
  }
}
