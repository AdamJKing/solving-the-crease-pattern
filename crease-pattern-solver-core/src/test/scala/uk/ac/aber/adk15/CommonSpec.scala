package uk.ac.aber.adk15

import org.mockito.{ArgumentCaptor, MockitoAnnotations}
import org.scalatest._
import org.scalatest.mockito.MockitoSugar

import scala.reflect.{ClassTag, _}

sealed trait CommonSpec extends BeforeAndAfterEach with MockitoSugar with Matchers { this: Suite =>

  override def beforeEach(): Unit = {
    super.beforeEach()
    MockitoAnnotations.initMocks(this)
  }

  /**
    * Helper function to pretty up the argument captor call.
    * @example val myCaptor = captor[MyCaptiveClass]
    * @tparam T the type to capture
    * @return the argument captor
    */
  def captor[T <: AnyRef: ClassTag]: ArgumentCaptor[T] =
    ArgumentCaptor forClass classTag[T].runtimeClass.asInstanceOf[Class[T]]
}

trait CommonAsyncSpec extends AsyncFlatSpec with CommonSpec
trait CommonFlatSpec  extends FlatSpec with CommonSpec
