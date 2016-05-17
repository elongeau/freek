import scala.language.implicitConversions

import cats.~>


/** a few implicit conversions */
package object freek {

  implicit def uplist[C[_] <: CoproductK[_], A, D[_] <: CoproductK[_]](freek: Freek[C, A])(
    implicit subCop: SubCop[C, D]
  ): Freek[D, A] = {

    val nat = new (C ~> D) {
      def apply[A](fa: C[A]): D[A] = subCop(fa)
    }

    new Freek(freek.free.mapSuspension(nat))
  }

  implicit class ToFreek[F[_], A](val fa: F[A]) extends AnyVal {
    @inline def freek0: Freek[ConsK[F, CNilK, ?], A] = Freek(fa)
    @inline def freek[C[_] <: CoproductK[_]](implicit sub: SubCop[ConsK[F, CNilK, ?], C]): Freek[C, A] = freek0.expand[C]
  }

  implicit def toInterpreter[F[_], R[_]](nat: F ~> R): Interpreter[ConsK[F, CNilK, ?], R] = Interpreter(nat)

}