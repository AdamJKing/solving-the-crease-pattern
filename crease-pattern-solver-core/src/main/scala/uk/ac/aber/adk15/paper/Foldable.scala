package uk.ac.aber.adk15.paper

trait Foldable {
  def fold(edge: Fold): Foldable
  def creases: Set[Fold]
  def size: Int

  val layers: List[Set[Fold]]
}
