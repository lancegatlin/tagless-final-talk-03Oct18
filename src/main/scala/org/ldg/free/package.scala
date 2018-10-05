package org.ldg

import cats.free.Free

package object free {
  type SqlDocDaoFree[ID,A,R] = Free[SqlDocDaoFreeOp[ID,A,?],R]
  type PasswordsFree[R] = Free[PasswordsFreeOp,R]
  type LoggerFree[R] = Free[LoggerFreeOp,R]
}
