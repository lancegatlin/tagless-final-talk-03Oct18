package org.ldg.free

sealed trait PasswordsFreeOp[R]
object PasswordsFreeOp {
  case class CompareDigest(plainTextPassword: String, passwordDigest: String) extends PasswordsFreeOp[Boolean]
  case class MkDigest(plainTextPassword: String) extends PasswordsFreeOp[String]
}

