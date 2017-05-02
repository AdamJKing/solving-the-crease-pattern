package uk.ac.aber.adk15.controllers

import java.io.File

import com.typesafe.scalalogging.Logger
import org.json4s._
import org.json4s.native.JsonMethods._
import uk.ac.aber.adk15.model.serialisers.PaperLayerSerialiser
import uk.ac.aber.adk15.paper._

trait CreasePatternParser {
  def parseFile(creasePatternFile: File): Option[CreasePattern]
}

class CreasePatternParserImpl extends CreasePatternParser {

  private val logger = Logger[CreasePatternParser]

  override def parseFile(creasePatternFile: File): Option[CreasePattern] = {
    logger info s"Reading crease pattern from file: ${creasePatternFile.getName}."

    implicit val defaultFormats = DefaultFormats + PaperLayerSerialiser

    val maybeCreasePattern = parse(creasePatternFile).extractOpt[CreasePattern]
    maybeCreasePattern.flatMap(creasePattern =>
      if (creasePattern.layers.isEmpty) None else Some(creasePattern))
  }
}
