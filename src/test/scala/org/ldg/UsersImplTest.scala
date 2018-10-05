package org.ldg

import java.time.Instant
import java.util.UUID

import cats.Id
import org.ldg.SqlDocDao.RecordMetadata
import org.ldg.impl.UsersImpl
import org.ldg.impl.UsersImpl.UserData
import org.scalatest.{FlatSpec, Matchers}
import org.scalamock.scalatest.MockFactory

class UsersImplTest extends FlatSpec with Matchers with MockFactory {
  class Fixture(
   val usersDao: SqlDocDao[UUID,UserData,Id] = mock[SqlDocDao[UUID,UserData,Id]],
   val passwords: Passwords[Id] = stub[Passwords[Id]],
   val logger: Logger[Id] = mock[Logger[Id]]
  ) {
    val users = new UsersImpl[Id](
      usersDao = usersDao,
      passwords = passwords,
      logger = logger
    )
  }

  "UsersImpl.create" should "create a new user when id & username does not already exist" in {
    val fixture = new Fixture
    import fixture._
    val id = UUID.randomUUID()
    val newUserData = UserData(
      username = "test-user",
      passwordDigest = "test-digest"
    )
    (usersDao.findById _).expects(id).returns(None).once
    (usersDao.findByNativeQuery _).expects("`username`='test-user'").returns(Seq.empty).once
    (passwords.mkDigest _).when("test-password").returns("test-digest")
    (usersDao.insert _).expects(id, newUserData).returns(true).once
    (logger.info _).expects(s"Created user $id with username test-user").once

    users.create(id,"test-user","test-password") shouldBe true
  }

  "UsersImpl.create" should "fail if a new user has a username that already exists" in {
    val fixture = new Fixture
    import fixture._
    val id = UUID.randomUUID()
    val fakeId = UUID.randomUUID()
    val existingUserData = (fakeId,UserData(
      username = "test-user",
      passwordDigest = "existing-digest"
    ),RecordMetadata(Instant.now(),Instant.now(),None))
    (usersDao.findById _).expects(id).returns(None).once
    (usersDao.findByNativeQuery _).expects("`username`='test-user'").returns(Seq(existingUserData)).once

    users.create(id,"test-user","test-password") shouldBe false
  }
}