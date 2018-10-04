package org.ldg

import java.util.UUID

import cats.Id
import org.ldg.UsersImpl.UserData
import org.scalatest.{FlatSpec, Matchers}
import org.scalamock.scalatest.MockFactory

class UsersImplTest extends FlatSpec with Matchers with MockFactory {
  class Fixture(
   val usersDao: SqlDocDao[UUID,UserData,Id] = stub[SqlDocDao[UUID,UserData,Id]],
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
    (usersDao.findById _).when(id).returns(None)
    (usersDao.findByNativeQuery _).when(*).returns(Seq.empty)
    (passwords.mkDigest _).when("test-password").returns("test-digest")
    (usersDao.insert _).when(id,newUserData).returns(true)
    (logger.info _).expects(s"Created user $id with username test-user").once()

    users.create(id,"test-user","test-password") shouldBe true
  }
}