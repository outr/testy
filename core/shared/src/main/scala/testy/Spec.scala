package testy

import munit.FunSuite

import scala.concurrent.Future
import scala.language.implicitConversions

trait Spec extends FunSuite with SpecExtras {
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

    def when(f: => Unit): Unit = block("when", f)

    def should(f: => Unit): Unit = block("should", f)

    def must(f: => Unit): Unit = block("must", f)

    def in(f: => Unit): Unit = {
      val name = (s :: parts).reverse.mkString(" ")
      test(name)(f)
    }

    def async[A](f: => A)(implicit support: AsyncSupport[A]): Unit = {
      val name = (s :: parts).reverse.mkString(" ")
      test(name)(support(f))
    }
  }

  implicit def t2Assertable[T](t: T): Assertable[T] = Assertable[T](t, this)

  def be[T](expected: T): Assertion[T] = EqualityAssertion[T](expected)

  def startWith(prefix: String): Assertion[String] = StartsWithAssertion(prefix)

  object be {
    def >=[T: Ordering](than: T): Assertion[T] = GTEAssertion[T](than)
  }
}

trait AsyncSupport[A] {
  def apply(async: A): Future[Unit]
}