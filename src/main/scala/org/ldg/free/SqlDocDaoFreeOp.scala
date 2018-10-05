package org.ldg.free

import org.ldg.SqlDocDao.RecordMetadata

sealed trait SqlDocDaoFreeOp[ID,A,R]
object SqlDocDaoFreeOp {
  case class Exists[ID,A](id: ID)extends SqlDocDaoFreeOp[ID,A,Boolean]
  case class FindById[ID,A](id: ID)extends SqlDocDaoFreeOp[ID,A,Option[(ID, A, RecordMetadata)]]
  case class FindByNativeQuery[ID,A](sql: String)extends SqlDocDaoFreeOp[ID,A,Seq[(ID, A, RecordMetadata)]]
  case class FindAll[ID,A](start: Int, batchSize: Int)extends SqlDocDaoFreeOp[ID,A,Seq[(ID, A, RecordMetadata)]]
  case class Insert[ID,A](id: ID, a: A)extends SqlDocDaoFreeOp[ID,A,Boolean]
  case class Update[ID,A](id: ID, value: A)extends SqlDocDaoFreeOp[ID,A,Boolean]
  case class Remove[ID,A](id: ID)extends SqlDocDaoFreeOp[ID,A,Boolean]
}
