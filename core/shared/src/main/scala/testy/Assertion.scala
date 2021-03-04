package testy

import munit.FunSuite

trait UntypedAssertion {
  def assertWith(value: Any, suite: FunSuite): Unit
}

trait Assertion[T] {
  def assertWith(value: T, suite: FunSuite): Unit
}

case class EqualityAssertion[T](expected: T) extends Assertion[T] {
  override def assertWith(value: T, suite: FunSuite): Unit = suite.assertEquals(value, expected)
}

case class GTEAssertion[T](than: T)(implicit o: Ordering[T]) extends Assertion[T] {
  override def assertWith(value: T, suite: FunSuite): Unit = suite.assert(o.gteq(value, than), s"$value is not >= $than")
}

case class StartsWithAssertion(prefix: String) extends Assertion[String] {
  override def assertWith(value: String, suite: FunSuite): Unit = suite.assert(value.startsWith(prefix), s"$value did not start with $prefix")
}