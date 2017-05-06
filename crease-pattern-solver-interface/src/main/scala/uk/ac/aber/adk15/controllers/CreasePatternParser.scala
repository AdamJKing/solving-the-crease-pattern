package uk.ac.aber.adk15.controllers

import java.io.File

import com.typesafe.scalalogging.Logger
import org.json4s._
import org.json4s.native.JsonMethods._
import uk.ac.aber.adk15.model.serialisers.PaperLayerSerialiser
import uk.ac.aber.adk15.paper._

/**
  * Parses a crease-pattern file (JSON)
  */
trait CreasePatternParser {

  /**
    * Parse the given crease-pattern file for a [[CreasePattern]]
    * @param creasePatternFile the file to parse
    * @return if successful, the parsed crease-pattern
    */
  def parseFile(creasePatternFile: File): Option[CreasePattern]
}

/**
  * An implementation of [[CreasePatternParser]]
  */
class CreasePatternParserImpl extends CreasePatternParser {

  private val logger = Logger[CreasePatternParser]

  /**
    * @inheritdoc
    * @param creasePatternFile the file to parse
    * @return if successful, the parsed crease-pattern
    */
  override def parseFile(creasePatternFile: File): Option[CreasePattern] = {
    logger info s"Reading crease pattern from file: ${creasePatternFile.getName}."

    implicit val defaultFormats = DefaultFormats + PaperLayerSerialiser

    val maybeCreasePattern = parse(creasePatternFile).extractOpt[CreasePattern]
    maybeCreasePattern.flatMap(
      creasePattern =>
        // we want to check that the crease-pattern we parsed is valid
        if (creasePattern.layers.isEmpty) {
          logger info "Malformed crease-pattern, has no layers!"
          None
        } else Some(creasePattern))
  }
}
