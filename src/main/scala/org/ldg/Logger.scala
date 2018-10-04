package org.ldg

trait Logger[E[_]] {
  def trace(message:  String) : E[Unit]
  def trace2(message: String, cause: Throwable) : E[Unit]
  def debug(message: String) : E[Unit]
  def debug2(message: String, cause: Throwable) : E[Unit]
  def info(message: String) : E[Unit]
  def info2(message: String, cause: Throwable) : E[Unit]
  def warn(message: String) : E[Unit]
  def warn2(message: String, cause: Throwable) : E[Unit]
  def error(message: String) : E[Unit]
  def error2(message: String, cause: Throwable) : E[Unit]
}