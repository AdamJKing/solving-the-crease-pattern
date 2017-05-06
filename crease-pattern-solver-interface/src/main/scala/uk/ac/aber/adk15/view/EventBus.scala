package uk.ac.aber.adk15.view

import java.util.concurrent.atomic.AtomicReference

/**
  * Defines a single, observable event
  */
trait ObservableEvent

/**
  * Defines something that can observe an observable event.
  *
  * @tparam T any kind of [[ObservableEvent]]
  */
trait Observer[T <: ObservableEvent] {
  def onSuccess(event: T): Unit
  def onFailure(event: T): Unit
  def onUpdate(event: T): Unit
}

/**
  * Manages an event queue data-structure.
  *
  * All observers must inject an instance of this class and
  * subscribe to the desired event type.
  *
  * It will then be updated using the following templates;
  *     * success: the event representing a success
  *     * failure: the event representing a failure
  *     * update: a generic update, unassociated with start/end
  *
  * @tparam T the type of event that this event bus concerns itself with
  */
class EventBus[T <: ObservableEvent] {

  // we want our event bus to work asynchronously
  // so event observers may be accessed by multiple threads
  // when the observers are updated
  private val eventObservers = new AtomicReference(Set[Observer[T]]())

  /**
    * Subscribe the given observer to the desired event T
    * @param observer the observer to subscribe to events
    */
  def subscribe(observer: Observer[T]): Unit = {
    var newObservers = eventObservers.get + observer
    var wasSet       = eventObservers.compareAndSet(eventObservers.get(), newObservers)

    // sometimes another thread is trying to subscribe/unsubscribe an observer
    // we simply just need to wait until they're done and reattempt to subscribe
    while (!wasSet) {
      newObservers = eventObservers.get + observer
      wasSet = eventObservers.compareAndSet(eventObservers.get(), newObservers)
    }
  }

  /**
    * Remove the given observer from the list of observers.
    * @param observer the observer to remove
    */
  def unsubscribe(observer: Observer[T]): Unit = {
    var newObservers = eventObservers.get - observer
    var wasSet       = eventObservers.compareAndSet(eventObservers.get(), newObservers)

    // sometimes another thread is trying to subscribe/unsubscribe an observer
    // we simply just need to wait until they're done and reattempt to unsubscribe
    while (!wasSet) {
      newObservers = eventObservers.get - observer
      wasSet = eventObservers.compareAndSet(eventObservers.get(), newObservers)
    }
  }

  def onSuccess(event: T): Unit = publishEvent(_ onSuccess event)
  def onFailure(event: T): Unit = publishEvent(_ onFailure event)
  def onUpdate(event: T): Unit  = publishEvent(_ onUpdate event)

  /**
    * Publish a single event
    * @param update the event to publish to all observers
    */
  private def publishEvent(update: Observer[T] => Unit): Unit =
    eventObservers.get foreach (update(_))
}
