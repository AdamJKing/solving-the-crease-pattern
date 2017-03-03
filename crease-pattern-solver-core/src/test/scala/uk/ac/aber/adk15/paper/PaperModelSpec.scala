package uk.ac.aber.adk15.paper

import org.scalatest.{FlatSpec, Matchers}
import uk.ac.aber.adk15.Point
import uk.ac.aber.adk15.paper.PaperModelPredef._

import scalax.collection.immutable.Graph

class PaperModelSpec extends FlatSpec with Matchers {

  "The paper model" should "be capable of being folded" in {

    // GIVEN
    val paperModel = PaperModel(
      Graph(
        PaperEdge(Point(0, 0), Point(100, 0), PaperBoundary),
        PaperEdge(Point(100, 0), Point(100, 100), PaperBoundary),
        PaperEdge(Point(100, 100), Point(0, 100), PaperBoundary),
        PaperEdge(Point(0, 100), Point(0, 0), PaperBoundary),
        PaperEdge(Point(0, 100), Point(100, 0), MountainFold)
      ))

    // WHEN
    val foldedPaperModel = paperModel <~~ PaperEdge(Point(0, 100), Point(100, 0), MountainFold)

    // THEN
    foldedPaperModel should be(
      PaperModel(Graph(
        PaperEdge(Point(100, 100), Point(100, 0), PaperBoundary),
        PaperEdge(Point(100, 0), Point(0, 100), CreasedFold),
        PaperEdge(Point(0, 100), Point(100, 100), PaperBoundary)
      )))
  }

  "Graphs with undirected edges" should "equal each other" in {
    val a = PaperModel(
      Graph(
        PaperEdge(Point(100, 0), Point(0, 100), PaperBoundary)
      ))

    val b = PaperModel(
      Graph(
        PaperEdge(Point(0, 100), Point(100, 0), PaperBoundary)
      ))

    a should equal(b)
  }
}
