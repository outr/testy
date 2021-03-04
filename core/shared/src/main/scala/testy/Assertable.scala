package testy

import munit.FunSuite

case class Assertable[T](value: T, suite: FunSuite) {
  def should(assertion: Assertion[T]): Unit = assertion.assertWith(value)
}