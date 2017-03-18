package uk.ac.aber.adk15

import org.mockito.{ArgumentCaptor, MockitoAnnotations}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}

import scala.reflect.{ClassTag, _}

trait CommonSpec
    extends FlatSpec
    with Matchers
    with MockitoSugar
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  override def beforeEach(): Unit = MockitoAnnotations.initMocks(this)

  /**
    * Helper function to pretty up the argument captor call.
    * @example val myCaptor = captor[MyCaptiveClass]
    * @tparam T the type to capture
    * @return the argument captor
    */
  def captor[T <: AnyRef: ClassTag]: ArgumentCaptor[T] =
    ArgumentCaptor forClass classTag[T].runtimeClass.asInstanceOf[Class[T]]
}
