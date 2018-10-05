package org.ldg.free

import java.time.Instant
import java.util.UUID

import cats._
import cats.free.Free
import org.ldg.SqlDocDao.RecordMetadata
import org.ldg.impl.UsersImpl.UserData
import org.ldg.impl.free._
import org.ldg._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable


class UsersImplTaglessFinalIntpr_FreeTest extends FlatSpec with Matchers with MockFactory {
  import UsersImplTaglessFinalIntpr_FreeTest._

  // Build interpreter for 3 composed DSLs
  val usersImplOpIntpr : UsersImplFreeOp ~> Id =
    SqlDocDaoFreeOpIntpr or (PasswordsFreeOpIntpr or LoggerFreeOpIntpr)

  "UsersImplTaglessFinalIntpr_Free" should "interpret to Free monad of its composed DSLs" in {
    val id = UUID.randomUUID()

    // A sample tagless-final program; E is generic here
    def program[E[_]:Monad](id: UUID)(users: Users[E],logger: Logger[E]) : E[Boolean] = {
      import cats.Monad.ops._
      for {
        createOk <- users.create(id, "test-user", "test-password")
        _ <- if (createOk) {
          logger.info("success!")
        } else {
          logger.error("failure!")
        }
      } yield createOk
    }

    // Render the program in the Free monad
    val freeResult : UsersImplFree[Boolean] =
      program(id)(
        UsersImplTaglessFinalIntpr_Free,
        new LoggerTaglessFinalIntpr_Free
      )

    // Run the program stored in the FreeMonad
    val result = freeResult.foldMap(usersImplOpIntpr)

    result shouldBe true
    SqlDocDaoFreeOpIntpr.m.size shouldBe 1
    val expectedUserData = SqlDocDaoFreeOpIntpr.m.head
    expectedUserData._1 shouldBe id
    expectedUserData._2._1 shouldBe id
    expectedUserData._2._2 shouldBe UserData(
      username = "test-user",
      passwordDigest = "test-password"
    )
    expectedUserData._2._3.removed shouldBe None

    val expectedLogs = LoggerFreeOpIntpr.output.toList
    expectedLogs.size shouldBe 2
    expectedLogs should contain inOrderOnly (
      s"Created user $id with username test-user",
      "success!"
    )
  }

}

object UsersImplTaglessFinalIntpr_FreeTest {
  // Test interpreters for Logger, Passwords and SqlDocDao

  object LoggerFreeOpIntpr extends (LoggerFreeOp ~> Id) {
    val output = new mutable.ListBuffer[String]()
    def apply[A](fa: LoggerFreeOp[A]): Id[A] = {
      import LoggerFreeOp._
      fa match {
        case Trace(message) =>
          output += message;()
        case Trace2(message, cause) => output += message;()
        case Debug(message) => output += message;()
        case Debug2(message, cause) => output += message;()
        case Info(message) => output += message;()
        case Info2(message, cause) => output += message;()
        case Warn(message) => output += message;()
        case Warn2(message, cause) => output += message;()
        case Error(message) => output += message;()
        case Error2(message, cause) => output += message;()
      }
    }
  }
  object PasswordsFreeOpIntpr extends (PasswordsFreeOp ~> Id) {
    def apply[A](fa: PasswordsFreeOp[A]): Id[A] = {
      import PasswordsFreeOp._
      fa match {
        case CompareDigest(plainTextPassword, passwordDigest) =>
          plainTextPassword == passwordDigest
        case MkDigest(plainTextPassword) =>
          plainTextPassword
      }
    }
  }
  object SqlDocDaoFreeOpIntpr extends (SqlDocDaoFreeOp[UUID,UserData,?] ~> Id) {
    val m = new mutable.HashMap[UUID,(UUID,UserData,RecordMetadata)]()
    def apply[A](fa: SqlDocDaoFreeOp[UUID, UserData, A]): Id[A] = {
      import SqlDocDaoFreeOp._
      fa match {
        case Exists(id)=>
          m.contains(id)
        case FindById(id)=>
          m.get(id)
        case FindByNativeQuery(sql)=>
          val r = "`username`='([^']+)'".r
          sql match {
            case r(username) =>
              m.values.iterator.filter(_._2.username == username).toList
            case _ => ???
          }
        case FindAll(start, batchSize)=>
          m.values.iterator.drop(start).take(batchSize).toList
        case Insert(id, a)=>
          m.put(id,(id,a,RecordMetadata(
            created = Instant.now,
            lastUpdated = Instant.now,
            removed = None
          )))
          true
        case Update(id, newA) =>
          m.get(id) match {
            case Some((_,_,existingRecordMetadata)) =>
              m.put(id,(id,newA,existingRecordMetadata.copy(
                lastUpdated = Instant.now
              )))
              true
            case None =>
              false
          }
        case Remove(id)=>
          m.get(id) match {
            case Some((_,a,existingRecordMetadata)) =>
              m.put(id,(id,a,RecordMetadata(
                created = existingRecordMetadata.created,
                lastUpdated = Instant.now,
                removed = Some(Instant.now)
              )))
              true
            case None =>
              false
          }

      }
    }
  }
}