/*
 * Copyright 2020 Carlos Conyers
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
package little.json.rpc

/** Stores optional identifier. */
sealed trait JsonRpcIdentifier {
  /**
   * Tests for undefined value.
   *
   * @note An identifier is undefined if it is not present. Not same as
   * [[isNullified]].
   */
  def isUndefined: Boolean

  /**
   * Tests for nullified value.
   *
   * @note An identifier is nullified if it is defined with a null value. Not
   * same as [[isUndefined]].
   */
  def isNullified: Boolean

  /** Tests for string value. */
  def isString: Boolean

  /** Tests for number value. */
  def isNumber: Boolean

  /**
   * Gets value as string.
   *
   * @throws NoSuchElementException if not string value
   */
  def stringValue: String

  /**
   * Gets value as number.
   *
   * @throws NoSuchElementException if not number value
   */
  def numberValue: Long
}

/** Provides factory for `JsonRpcIdentifier`. */
object JsonRpcIdentifier {
  /** Gets undefined identifier. */
  def undefined: JsonRpcIdentifier = UndefinedIdentifier

  /** Gets nullified identifier. */
  def nullified: JsonRpcIdentifier = NullifiedIdentifier

  /**
   * Creates identifier with supplied string value.
   *
   * @param id string value of identifier
   */
  def apply(id: String): JsonRpcIdentifier =
    StringIdentifier(id)

  /**
   * Creates identifier with supplied number value.
   *
   * @param id number value of identifier
   */
  def apply(id: Long): JsonRpcIdentifier =
    NumberIdentifier(id)
}

/** Represents undefined identifier. */
private object UndefinedIdentifier extends JsonRpcIdentifier {
  val isUndefined = true
  val isNullified = false
  val isString = false
  val isNumber = false
  def stringValue = throw new NoSuchElementException("no string value")
  def numberValue = throw new NoSuchElementException("no number value")
}

/** Defines identifier with null value. */
private object NullifiedIdentifier extends JsonRpcIdentifier {
  val isUndefined = false
  val isNullified = true
  val isString = false
  val isNumber = false
  def stringValue = throw new NoSuchElementException("no string value")
  def numberValue = throw new NoSuchElementException("no number value")
}

/** Defines identifier with string value. */
private case class StringIdentifier(stringValue: String) extends JsonRpcIdentifier {
  require(stringValue != null)

  val isUndefined = false
  val isNullified = false
  val isString = true
  val isNumber = false
  def numberValue = throw new NoSuchElementException("no number value")
}

/** Defines identifier with string value. */
private case class NumberIdentifier(numberValue: Long) extends JsonRpcIdentifier {
  val isUndefined = false
  val isNullified = false
  val isString = false
  val isNumber = true
  def stringValue = throw new NoSuchElementException("no string value")
}
