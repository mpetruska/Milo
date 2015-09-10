package cats.extensions.std

import cats.Applicative

import scala.util.{Success, Try}

trait TryInstances {

  implicit def tryInstances: Applicative[Try] =
    new Applicative[Try] {

      override def pure[A](x: A): Try[A] = Success(x)

      override def ap[A, B](fa: Try[A])(f: Try[(A) => B]): Try[B] = fa.flatMap(a => f.map(ff => ff(a)))

    }

}
