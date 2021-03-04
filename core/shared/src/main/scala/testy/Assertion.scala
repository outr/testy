package testy

trait Assertion[T] {
  def assertWith(value: T): Unit
}