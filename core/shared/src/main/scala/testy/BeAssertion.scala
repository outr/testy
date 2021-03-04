package testy

import munit.FunSuite

case class BeAssertion[T](expected: T, suite: FunSuite) extends Assertion[T] {
  override def assertWith(value: T): Unit = suite.assertEquals(value, expected)
}