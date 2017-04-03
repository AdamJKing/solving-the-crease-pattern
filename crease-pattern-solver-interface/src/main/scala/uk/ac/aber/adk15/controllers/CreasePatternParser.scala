package uk.ac.aber.adk15.controllers

import java.io.File

import com.typesafe.scalalogging.Logger
import org.json4s._
import org.json4s.native.JsonMethods._
import uk.ac.aber.adk15.model.serialisers.FoldSerialiser
import uk.ac.aber.adk15.paper._

trait CreasePatternParser {
  def parseFile(creasePatternFile: File): CreasePattern
}

class CreasePatternParserImpl extends CreasePatternParser {

  private val logger = Logger[CreasePatternParser]

  override def parseFile(creasePatternFile: File): CreasePattern = {
    logger info s"Reading crease pattern from file: ${creasePatternFile.getName}."

    implicit val defaultFormats      = DefaultFormats + FoldSerialiser
    val creasePattern: CreasePattern = parse(creasePatternFile).extract[CreasePattern] // OrElse {
//      throw new IllegalStateException("Could not load crease pattern")
//    }

    logger debug s"I read a crease pattern looking like $creasePattern"

    creasePattern
  }
}
