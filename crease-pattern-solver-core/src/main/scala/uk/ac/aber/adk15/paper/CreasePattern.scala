package uk.ac.aber.adk15.paper

import com.typesafe.scalalogging.Logger

import scala.Function.tupled
import scala.annotation.tailrec

case class CreasePattern(layers: List[PaperLayer]) {
  import CreasePattern._

  private val logger = Logger[CreasePattern]

  def folds: Set[Fold] =
    layers.flatMap(layer => layer.mountainFolds ++ layer.valleyFolds)(collection.breakOut)

  def fold(edge: Fold): CreasePattern = {
    validateLegalFold(edge)

    val (layersToFold, layersToLeave) = layers partition (layer =>
      (layer contains edge) || !(layer coversFold edge))

    val (left, right) = (layersToFold map (_.segmentOnLine(edge.start, edge.end))).unzip

    val maxAreaOfLeft  = (left map (_.surfaceArea)).max
    val maxAreaOfRight = (right map (_.surfaceArea)).max

    val newLayers =
      if (maxAreaOfLeft < maxAreaOfRight) {
        logger debug s"Folding to the right over $edge"

        if (edge.foldType == ValleyFold)
          (left map (_ rotateAround (edge.start, edge.end))) ++ right
        else
          right ++ (left map (_ rotateAround (edge.start, edge.end)))
      } else {
        logger debug s"Folding to the left over $edge"

        if (edge.foldType == ValleyFold)
          (right map (_ rotateAround (edge.start, edge.end))) ++ left
        else
          left ++ (right map (_ rotateAround (edge.start, edge.end)))
      }

    new CreasePattern(repair(layersToLeave, newLayers, edge.foldType))
  }

  private def validateLegalFold(fold: Fold) =
    if (!(folds contains fold)) {
      logger error s"Could not fold $fold, folds available were $folds"
      throw new IllegalFoldException(fold)
    }

  def size: Int = layers.length

  override def equals(obj: Any): Boolean = obj match {
    case other: CreasePattern => other.layers == this.layers
    case _                    => false
  }

  override def hashCode(): Int = {
    17 * 31 * layers.hashCode()
  }

  override def toString = s"{\n${layers mkString ",\n\n\t"}\n}"
}

object CreasePattern {
  def from(paperLayers: PaperLayer*): CreasePattern = {
    new CreasePattern(paperLayers.toList)
  }

  def repair(old: List[PaperLayer],
             `new`: List[PaperLayer],
             foldType: FoldType): List[PaperLayer] = {

    var mergedMap = Map[Int, List[Option[PaperLayer]]]()

    @tailrec
    def findCrossover(crossover: Int): Int = {
      def validCrossover(a: List[PaperLayer], b: List[PaperLayer]) = {
        val maybeLayers = a zip b map tupled((left, right) => left mergeWith right)
        mergedMap += (crossover -> maybeLayers)

        maybeLayers forall (_.isDefined)
      }

      if (foldType == ValleyFold) {
        if (validCrossover(old take crossover, `new` takeRight crossover))
          crossover
        else
          findCrossover(crossover - 1)
      } else {
        if (validCrossover(old takeRight crossover, `new` take crossover))
          crossover
        else
          findCrossover(crossover - 1)
      }
    }

    val crossover = findCrossover(math.min(old.size, `new`.size))

    if (foldType == ValleyFold) {
      val (top, bottom) = (`new` dropRight crossover, old drop crossover)
      top ++ (mergedMap(crossover) map (_.get)) ++ bottom

    } else {
      val (top, bottom) = (old dropRight crossover, `new` drop crossover)
      top ++ (mergedMap(crossover) map (_.get)) ++ bottom
    }
  }
}
