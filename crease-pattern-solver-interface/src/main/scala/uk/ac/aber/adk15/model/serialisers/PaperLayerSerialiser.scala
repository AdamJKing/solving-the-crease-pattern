package uk.ac.aber.adk15.model.serialisers

import org.json4s.Extraction.decompose
import org.json4s.JsonAST.{JArray, JObject, JSet}
import org.json4s.{CustomSerializer, DefaultFormats}
import uk.ac.aber.adk15.paper.PaperLayer
import uk.ac.aber.adk15.paper.fold.Fold

case object PaperLayerSerialiser
    extends CustomSerializer[PaperLayer](_ =>
      ({
        case JObject(List(("folds", JArray(objs)))) =>
          implicit val defaultFormats = DefaultFormats + FoldTypeSerialiser
          PaperLayer(objs map (obj => obj.extract[Fold]): _*)
      }, {
        case paperLayer: PaperLayer =>
          implicit val defaultFormats = DefaultFormats + FoldTypeSerialiser
          JSet(paperLayer.unfoldable ++ paperLayer.foldable map decompose)
      }))
