package testy
import munit.FunSuite

trait SpecExtras {
  inline def a[T]: UntypedAssertion = new UntypedAssertion {
    override def assertWith(value: Any, suite: FunSuite): Unit = value.isInstanceOf[T]
  }
}