package uk.ac.aber.adk15.controllers

import java.io.File

import com.github.tototoshi.csv.CSVReader
import com.typesafe.scalalogging.Logger
import uk.ac.aber.adk15.paper._

trait CreasePatternParser {
  def parseFile(creasePatternFile: File): CreasePattern
}

class CreasePatternParserImpl extends CreasePatternParser {

  private val logger = Logger[CreasePatternParser]

  override def parseFile(creasePatternFile: File): CreasePattern = {
    val reader = CSVReader open creasePatternFile

    logger info "Reading crease pattern from file."

    val creasePattern = new CreasePattern(List((reader allWithHeaders () map (line => {
      val A = new Point(line("X1").toDouble, line("Y1").toDouble)
      val B = new Point(line("X2").toDouble, line("Y2").toDouble)
      val foldType = line("Type") match {
        case "Mountain" => MountainFold
        case "Valley"   => ValleyFold
        case "Boundary" => PaperBoundary
      }

      Fold(A, B, foldType)
    })).toSet))

    logger debug s"I read a crease pattern looking like $creasePattern"

    creasePattern
  }
}
