package uk.ac.aber.adk15

/**
  * Created by adam on 25/02/17.
  */
object PaperEdgePredef {
  final implicit class PaperEdgeAssoc[N](val start: N) extends AnyVal {
    @inline def /\(end: N) = new PaperEdge[N](start, end, MountainFold())
    @inline def \/(end: N) = new PaperEdge[N](start, end, ValleyFold())
    @inline def <>(end: N) = new PaperEdge[N](start, end, HardEdge())
  }
}
