package org.ldg.impl

import java.util.UUID

import cats.data.EitherK
import cats.free.Free
import cats.~>
import org.ldg.free._
import org.ldg.impl.UsersImpl.UserData

package object free {
  type UsersImplFreeOp[A] = EitherK[
    SqlDocDaoFreeOp[UUID,UserData,?],
    EitherK[
      PasswordsFreeOp,
      LoggerFreeOp,
      ?
    ],
    A
  ]
  type UsersImplFree[A] = Free[UsersImplFreeOp,A]

  implicit val liftLoggerFreeOp = new (LoggerFreeOp ~> UsersImplFreeOp) {
    def apply[A](fa: LoggerFreeOp[A]): UsersImplFreeOp[A] =
      EitherK.rightc(EitherK.rightc(fa))
  }
  implicit val liftPasswordsFreeOp = new (PasswordsFreeOp ~> UsersImplFreeOp) {
    def apply[A](fa: PasswordsFreeOp[A]): UsersImplFreeOp[A] =
      EitherK.rightc(EitherK.leftc(fa))
  }
  implicit val liftSqlDocDaoFreeOp = new (SqlDocDaoFreeOp[UUID,UserData,?] ~> UsersImplFreeOp) {
    def apply[A](fa: SqlDocDaoFreeOp[UUID, UserData, A]): UsersImplFreeOp[A] =
      EitherK.leftc(fa)
  }

  implicit val liftLoggerFree = new (LoggerFree ~> UsersImplFree) {
    def apply[A](fa: LoggerFree[A]): UsersImplFree[A] =
      fa.mapK(liftLoggerFreeOp)
  }
  implicit val liftPasswordsFree = new (PasswordsFree ~> UsersImplFree) {
    def apply[A](fa: PasswordsFree[A]): UsersImplFree[A] =
      fa.mapK(liftPasswordsFreeOp)
  }
  implicit val liftSqlDocDaoFree = new (SqlDocDaoFree[UUID,UserData,?] ~> UsersImplFree) {
    def apply[A](fa: SqlDocDaoFree[UUID,UserData,A]): UsersImplFree[A] =
      fa.mapK(liftSqlDocDaoFreeOp)
  }
}
