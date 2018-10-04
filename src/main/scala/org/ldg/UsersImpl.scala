package org.ldg

import java.util.UUID

import cats.Monad
import org.ldg.UsersImpl.UserData
import Users.User
import org.ldg.SqlDocDao.RecordMetadata

class UsersImpl[E[_]] (
  usersDao: SqlDocDao[UUID,UserData,E],
  passwords: Passwords[E],
  logger: Logger[E]
)(implicit
  E:Monad[E]
) extends Users[E]{
  import UsersImpl._
  import Monad.ops._

  def findByUsername(username: String): E[Option[User]] =
    usersDao.findByNativeQuery(s"`username`='$username'").map {
      _.headOption.map(toUser)
    }

  def findById(id: UUID): E[Option[User]] =
    usersDao.findById(id).map(_.map(toUser))

  def findAll(start: Int, batchSize: Int): E[Seq[User]] =
    usersDao.findAll(start, batchSize).map {
      _.map(toUser)
    }

  def findByIdOrUsername(id: UUID, username: String) : E[Option[User]] =
    for {
      optUser <- findById(id)
      result <- (optUser match {
        case s@Some(_) => E.pure(s)
        case None =>
          (for {
            optUser <- findByUsername(username)
            result <- optUser match {
              case s@Some(_) => E.pure(s)
              case None => E.pure(None)

            }
          } yield result):E[Option[User]]
      }):E[Option[User]]
    } yield result

  def create(id: UUID, username: String, plainTextPassword: String): E[Boolean] =
    for {
      optUser <- findByIdOrUsername(id,username)
      createOk <- optUser match {
        case Some(_) => E.pure(false)
        case None =>
          for {
            digest <- passwords.mkDigest(plainTextPassword)
            userData = UserData(
              username = username,
              passwordDigest = digest
            )
            insertResult <- usersDao.insert(id,userData)
            _ <- if(insertResult) {
              logger.info(s"Created user $id with username $username")
            } else {
              logger.error(s"Failed to insert userData=$userData")
            }
          } yield insertResult
      }
    } yield createOk

  def rename(userId: UUID, newUsername: String): E[Boolean] =
    for {
      optUserData <- usersDao.findById(userId)
      result <- optUserData match {
        case Some((_,userData,_)) =>
          for {
            optUserData2 <- findByUsername(newUsername)
            result <- optUserData2 match {
              case Some(_) => E.pure(false)
              case None =>
                usersDao.update(userId,userData.copy(username = newUsername))
            }
            _ <- if(result) {
              logger.info(s"Renamed user $userId to $newUsername")
            } else {
              E.pure(())
            }
          } yield result
        case None => E.pure(false)
      }
    } yield result

  def setPassword(userId: UUID, plainTextPassword: String): E[Boolean] =
    for {
      optUserData <- usersDao.findById(userId)
      result <- optUserData match {
        case Some((_,userData,_)) =>
          for {
            newDigest <- passwords.mkDigest(plainTextPassword)
            result <- usersDao.update(userId,userData.copy(passwordDigest = newDigest))
            _ <- if(result) {
              logger.info(s"Changed password for user $userId")
            } else {
              E.pure(())
            }
          } yield result
        case None => E.pure(false)
      }
    } yield result

  def remove(userId: UUID): E[Boolean] =
    usersDao.remove(userId)
}

object UsersImpl {
  case class UserData(
    username: String,
    passwordDigest: String
  )

  val toUser : ((UUID,UserData,RecordMetadata)) => User = { case (id,userData,metadata) =>
    User(
      id = id,
      username = userData.username,
      passwordDigest = userData.passwordDigest,
      created = metadata.created,
      removed = metadata.removed
    )
  }

}
