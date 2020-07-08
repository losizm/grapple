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

import javax.json.{ JsonStructure, JsonValue }

import little.json.{ Json, JsonOutput }

/** Stores result of JSON-RPC response. */
sealed trait JsonRpcResult {
  /** Tests for result. */
  def isResult: Boolean

  /** Tests for error. */
  def isError: Boolean

  /**
   * Gets result.
   *
   * @throws NoSuchElementException if no result
   */
  def result: JsonValue

  /**
   * Gets error.
   *
   * @throws NoSuchElementException if no error
   */
  def error: JsonRpcError
}

/** Provides factory for `JsonRpcResult`. */
object JsonRpcResult {
  /**
   * Creates error result.
   *
   * @param error error
   */
  def apply(error: JsonRpcError): JsonRpcResult = {
    if (error == null)
      throw new NullPointerException()
    JsonRpcResultImpl(Left(error))
  }

  /**
   * Creates successful result.
   *
   * @param result successful result
   * @param toJson converts result to JSON
   */
  def apply[T](result: T)(implicit toJson: JsonOutput[T]): JsonRpcResult =
    apply(Json.toJson(result))

  /**
   * Creates successful result.
   *
   * @param result successful result
   */
  def apply(result: JsonValue): JsonRpcResult = {
    if (result == null)
      throw new NullPointerException()

    require(result.isInstanceOf[JsonStructure],
      "result must be JSON array or object value")

    JsonRpcResultImpl(Right(result))
  }
}

private case class JsonRpcResultImpl(either: Either[JsonRpcError, JsonValue]) extends JsonRpcResult {
  val isResult = either.isRight
  val isError = either.isLeft

  def result = either.getOrElse(throw new NoSuchElementException("no result"))
  def error = either.swap.getOrElse(throw new NoSuchElementException("no error"))
}
