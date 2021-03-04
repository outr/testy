package testy

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

trait SpecExtras {
  def a[T]: UntypedAssertion = macro SpecExtras.isA[T]
}

object SpecExtras {
  def isA[T](context: blackbox.Context)(implicit t: context.WeakTypeTag[T]): context.Expr[UntypedAssertion] = {
    import context.universe._

    val className = t.tpe.toString
    context.Expr[UntypedAssertion](
      q"""
        import _root_.testy._
        import _root_.munit.FunSuite

        new UntypedAssertion {
          def assertWith(value: Any, suite: FunSuite): Unit = assert(value.isInstanceOf[$t], value + " is not an instance of " + $className)
        }
       """)
  }
}