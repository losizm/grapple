/*
 * Copyright 2021 Carlos Conyers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grapple.json
package rpc

/** Defines JSON-RPC identifier. */
sealed trait JsonRpcIdentifier:
  /** Tests for null. */
  def isNull: Boolean

  /** Tests for string. */
  def isString: Boolean

  /** Tests for number. */
  def isNumber: Boolean

  /**
   * Gets string value.
   *
   * @throws NoSuchElementException if identifier is not a string
   */
  def string: String

  /**
   * Gets number value.
   *
   * @throws NoSuchElementException if identifier is not a number
   */
  def number: Long

/** Provides JSON-RPC identifier factory. */
object JsonRpcIdentifier:
  /** Gets identifier with null value. */
  def nullified: JsonRpcIdentifier = NullIdentifier

  /**
   * Creates identifier with string value.
   *
   * @param id identifier
   */
  def apply(id: String): JsonRpcIdentifier = StringIdentifier(id)

  /**
   * Creates identifier with number value.
   *
   * @param id identifier
   */
  def apply(id: Long): JsonRpcIdentifier = NumberIdentifier(id)

private object NullIdentifier extends JsonRpcIdentifier:
  val isNull   = true
  val isString = false
  val isNumber = false

  def string = throw new NoSuchElementException("no string value")
  def number = throw new NoSuchElementException("no number value")

  override val toString = "null"

private case class StringIdentifier(string: String) extends JsonRpcIdentifier:
  require(string != null)

  val isNull   = false
  val isString = true
  val isNumber = false

  def number = throw new NoSuchElementException("no number value")

  override lazy val toString = EncodedString(string)

private case class NumberIdentifier(number: Long) extends JsonRpcIdentifier:
  val isNull   = false
  val isString = false
  val isNumber = true

  def string = throw new NoSuchElementException("no string value")

  override val toString = s"$number"
