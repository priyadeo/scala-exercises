package exercises

import shapeless._
import shapeless.ops.function._

import org.scalacheck.{ Prop, Arbitrary }
import org.scalacheck.Gen
import Prop.forAll

import org.scalatest.Spec
import org.scalatest.prop.Checkers

import org.scalacheck.Shapeless._

object Test {
  def success[F, R, L <: HList](method: F, answer: L)(
    implicit
    A:     Arbitrary[L],
    fntop: FnToProduct.Aux[F, L ⇒ R]
  ): Prop = {
    val rightGen = genRightAnswer(answer)
    val rightProp = forAll(rightGen)({ p ⇒
      try {
        fntop(method)(p)
        true
      } catch {
        case _: Throwable ⇒ false
      }
    })

    val wrongGen = genWrongAnswer(answer)
    val wrongProp = forAll(wrongGen)({ p ⇒
      try {
        fntop(method)(p)
        false
      } catch {
        case _: Throwable ⇒ true
      }
    })

    Prop.all(rightProp, wrongProp)
  }

  def genRightAnswer[L <: HList](answer: L): Gen[L] = {
    Gen.const(answer)
  }

  def genWrongAnswer[L <: HList](l: L)(
    implicit
    A: Arbitrary[L]
  ): Gen[L] = {
    A.arbitrary.suchThat(_ != l)
  }
}
