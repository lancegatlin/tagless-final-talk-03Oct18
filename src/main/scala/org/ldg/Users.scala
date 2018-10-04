package org.ldg

import java.time.Instant
import java.util.UUID

import org.ldg.Users.User

trait Users[E[_]] {

  def findByUsername(username: String) : E[Option[User]]

  def findById(id: UUID) : E[Option[User]]

  def findAll(start: Int, batchSize: Int) : E[Seq[User]]

  def create(id: UUID,username: String,plainTextPassword: String) : E[Boolean]

  def rename(userId: UUID, newUsername: String) : E[Boolean]

  def setPassword(userId: UUID, plainTextPassword: String) : E[Boolean]

  def remove(userId: UUID) : E[Boolean]
}

object Users {
  case class User(
    id: UUID,
    username: String,
    passwordDigest: String,
    created: Instant,
    removed: Option[Instant]
  )
}