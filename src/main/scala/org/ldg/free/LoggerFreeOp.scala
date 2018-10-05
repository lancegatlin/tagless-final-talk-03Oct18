package org.ldg.free

sealed trait LoggerFreeOp[R]
object LoggerFreeOp {
  case class Trace(message:  String)  extends LoggerFreeOp[Unit]
  case class Trace2(message: String, cause: Throwable)  extends LoggerFreeOp[Unit]
  case class Debug(message: String)  extends LoggerFreeOp[Unit]
  case class Debug2(message: String, cause: Throwable)  extends LoggerFreeOp[Unit]
  case class Info(message: String)  extends LoggerFreeOp[Unit]
  case class Info2(message: String, cause: Throwable)  extends LoggerFreeOp[Unit]
  case class Warn(message: String)  extends LoggerFreeOp[Unit]
  case class Warn2(message: String, cause: Throwable)  extends LoggerFreeOp[Unit]
  case class Error(message: String)  extends LoggerFreeOp[Unit]
  case class Error2(message: String, cause: Throwable)  extends LoggerFreeOp[Unit]
}
