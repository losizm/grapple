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

/**
 * Defines JSON exception.
 *
 * @constructor Constructs exception with detail message and underlying cause.
 *
 * @param message detail message
 * @param cause   underlying cause
 */
class JsonException(message: String, cause: Throwable) extends RuntimeException(message, cause):
  /** Constructs exception. */
  def this() = this(null, null)

  /**
   * Constructs exception with detail message.
   *
   * @param message detail message
   */
  def this(message: String) = this(message, null)

  /**
   * Constructs exception with underlying cause.
   *
   * @param cause underlying cause
   */
  def this(cause: Throwable) = this(null, cause)

/**
 * Defines JSON expectation error.
 *
 * @param expected class
 * @param actual class
 */
case class JsonExpectationError(expected: Class[_], actual: Class[_])
  extends JsonException(s"Expected ${expected.getSimpleName} instead of ${actual.getSimpleName}")

/**
 * Defines JSON object error.
 *
 * @param key object key
 * @param cause underlying cause
 */
case class JsonObjectError(key: String, cause: Exception) extends JsonException(s"Error accessing key: ${EncodedString(key)}", cause)

/**
 * Defines JSON array error.
 *
 * @param index array index
 * @param cause underlying cause
 */
case class JsonArrayError(index: Int, cause: Exception) extends JsonException(s"Error accessing index: $index", cause)

/**
 * Defines JSON parser error.
 *
 * @param message detail message
 * @param offset  character offset of parser error
 */
case class JsonParserError(message: String, offset: Long) extends JsonException(message)

/**
 * Defines JSON generator error.
 *
 * @param message detail message
 */
case class JsonGeneratorError(message: String) extends JsonException(message)
