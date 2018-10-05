package org.ldg.free

import cats.free.Free.liftF
import cats.~>
import org.ldg.Logger

class LoggerTaglessFinalIntpr_Free[E[_]](implicit k: LoggerFree ~> E) extends Logger[E] {
  import LoggerFreeOp._

  def trace(message: String): E[Unit] =
    k(liftF(Trace(message)))

  def trace2(message: String, cause: Throwable): E[Unit] =
    k(liftF(Trace2(message,cause)))

  def debug(message: String): E[Unit] =
    k(liftF(Debug(message)))

  def debug2(message: String, cause: Throwable): E[Unit] =
    k(liftF(Debug2(message,cause)))

  def info(message: String): E[Unit] =
    k(liftF(Info(message)))

  def info2(message: String, cause: Throwable): E[Unit] =
    k(liftF(Info2(message,cause)))

  def warn(message: String): E[Unit] =
    k(liftF(Warn(message)))

  def warn2(message: String, cause: Throwable): E[Unit] =
    k(liftF(Warn2(message,cause)))

  def error(message: String): E[Unit] =
    k(liftF(Error(message)))

  def error2(message: String, cause: Throwable): E[Unit] =
    k(liftF(Error2(message,cause)))
}
