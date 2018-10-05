package org.ldg.free

import cats.free.Free.liftF
import cats.~>
import org.ldg.Passwords

class PasswordsTaglessFinalIntpr_Free[E[_]](implicit liftK: PasswordsFree ~> E) extends Passwords[E] {
  import PasswordsFreeOp._

  def compareDigest(plainTextPassword: String, passwordDigest: String): E[Boolean] =
    liftK(liftF(CompareDigest(plainTextPassword,passwordDigest)))

  def mkDigest(plainTextPassword: String): E[String] =
    liftK(liftF(MkDigest(plainTextPassword)))
}
