package uk.ac.aber.adk15.model.serialisers

import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JNull, JString}
import uk.ac.aber.adk15.paper.{FoldType, MountainFold, PaperBoundary, ValleyFold}

case object FoldSerialiser
    extends CustomSerializer[FoldType](_ =>
      ({
        case JString(foldType) =>
          foldType match {
            case "MountainFold"  => MountainFold
            case "ValleyFold"    => ValleyFold
            case "PaperBoundary" => PaperBoundary
          }
        case JNull => null
      }, {
        case foldType: FoldType => JString(foldType.getClass.getSimpleName replace ("$", ","))
      }))
