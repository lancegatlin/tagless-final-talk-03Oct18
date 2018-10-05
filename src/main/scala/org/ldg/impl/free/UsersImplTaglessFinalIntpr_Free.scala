package org.ldg.impl.free

import java.util.UUID

import org.ldg.impl.UsersImpl
import org.ldg.free._
import org.ldg.impl.UsersImpl.UserData

/**
  * A tagless-final interpreter that outputs Free monad with the operations
  * of it's composed DSLs
  */
object UsersImplTaglessFinalIntpr_Free extends UsersImpl[UsersImplFree](
  usersDao = new SqlDocDaoTaglessFinalIntpr_Free[UUID, UserData, UsersImplFree],
  passwords = new PasswordsTaglessFinalIntpr_Free[UsersImplFree],
  logger = new LoggerTaglessFinalIntpr_Free[UsersImplFree]
)