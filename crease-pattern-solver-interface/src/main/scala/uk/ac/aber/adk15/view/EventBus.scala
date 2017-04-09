package uk.ac.aber.adk15.view

import java.util.concurrent.atomic.AtomicReference

trait ObservableEvent
trait Observer[T <: ObservableEvent] {
  def onSuccess(event: T): Unit
  def onFailure(event: T): Unit
  def onUpdate(event: T): Unit
}

final class EventBus[T <: ObservableEvent] {

  private val eventObservers = new AtomicReference(Set[Observer[T]]())

  def subscribe(observer: Observer[T]): Unit = {
    var newObservers = eventObservers.get + observer
    var wasSet       = eventObservers.compareAndSet(eventObservers.get(), newObservers)

    while (!wasSet) {
      newObservers = eventObservers.get + observer
      wasSet = eventObservers.compareAndSet(eventObservers.get(), newObservers)
    }
  }

  def unsubscribe(observer: Observer[T]): Unit = {
    var newObservers = eventObservers.get - observer
    var wasSet       = eventObservers.compareAndSet(eventObservers.get(), newObservers)

    while (!wasSet) {
      newObservers = eventObservers.get - observer
      wasSet = eventObservers.compareAndSet(eventObservers.get(), newObservers)
    }
  }

  def onSuccess(event: T): Unit = publishEvent(_ onSuccess event)
  def onFailure(event: T): Unit = publishEvent(_ onFailure event)
  def onUpdate(event: T): Unit  = publishEvent(_ onUpdate event)

  private def publishEvent(update: Observer[T] => Unit): Unit =
    eventObservers.get foreach (update(_))
}
