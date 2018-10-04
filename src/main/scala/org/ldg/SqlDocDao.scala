package org.ldg

import java.time.Instant

import org.ldg.SqlDocDao.RecordMetadata

trait SqlDocDao[ID,A,E[_]] {
  def exists(id: ID): E[Boolean]

  def findById(id: ID): E[Option[(ID, A, RecordMetadata)]]

  def findByNativeQuery(sql: String): E[Seq[(ID, A, RecordMetadata)]]

  def findAll(start: Int, batchSize: Int): E[Seq[(ID, A, RecordMetadata)]]

  def insert(id: ID, a: A): E[Boolean]

  def update(id: ID, value: A): E[Boolean]

  def remove(id: ID): E[Boolean]
}

object SqlDocDao {
  case class RecordMetadata(
    created: Instant,
    lastUpdated: Instant,
    removed: Option[Instant]
  )
}
