package org.ldg.free

import cats.free.Free.liftF
import cats.~>
import org.ldg.SqlDocDao
import org.ldg.SqlDocDao.RecordMetadata

class SqlDocDaoTaglessFinalIntpr_Free[ID,A,E[_]](implicit k: SqlDocDaoFree[ID,A,?] ~> E) extends SqlDocDao[ID,A,E] {
  import SqlDocDaoFreeOp._
  type Op[R] = SqlDocDaoFreeOp[ID,A,R]
  
  def exists(id: ID): E[Boolean] =
    k(liftF[Op,Boolean](Exists(id)))

  def findById(id: ID): E[Option[(ID, A, RecordMetadata)]] =
    k(liftF[Op,Option[(ID, A, RecordMetadata)]](FindById(id)))

  def findByNativeQuery(sql: String): E[Seq[(ID, A, RecordMetadata)]] =
    k(liftF[Op,Seq[(ID, A, RecordMetadata)]](FindByNativeQuery(sql)))

  def findAll(start: Int, batchSize: Int): E[Seq[(ID, A, RecordMetadata)]] =
    k(liftF[Op,Seq[(ID, A, RecordMetadata)]](FindAll(start, batchSize)))

  def insert(id: ID, a: A): E[Boolean] =
    k(liftF[Op,Boolean](Insert(id,a)))
  
  def update(id: ID, value: A): E[Boolean] =
    k(liftF[Op,Boolean](Update(id,value)))
  
  def remove(id: ID): E[Boolean] =
    k(liftF[Op,Boolean](Remove(id)))
}
