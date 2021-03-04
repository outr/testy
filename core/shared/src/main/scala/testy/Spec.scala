package testy

import munit.FunSuite

import scala.language.implicitConversions

trait Spec extends FunSuite {
  private var parts = List.empty[String]

  implicit class Buildable(s: String) {
    private def block(name: String, f: => Unit): Unit = {
      val previous = parts
      parts = name :: s :: parts
      try {
        f
      } finally {
        parts = previous
      }
    }

    def should(f: => Unit): Unit = block("should", f)

    def must(f: => Unit): Unit = block("must", f)

    def in(f: => Unit): Unit = {
      val name = (s :: parts).reverse.mkString(" ")
      test(name)(f)
    }
  }

  implicit def t2Assertable[T](t: T): Assertable[T] = Assertable[T](t, this)

  def be[T](expected: T): Assertion[T] = BeAssertion[T](expected, this)
}