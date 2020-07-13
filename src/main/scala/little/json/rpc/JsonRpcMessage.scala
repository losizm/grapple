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

/** Represents JSON-RPC message. */
sealed trait JsonRpcMessage {
  /** Gets JSON-RPC version. */
  def version: String

  /** Gets identifier. */
  def id: JsonRpcIdentifier

}

/** Represents JSON-RPC request. */
sealed trait JsonRpcRequest extends JsonRpcMessage {
  /** Gets method. */
  def method: String

  /** Gets parameters. */
  def params: Option[JsonValue]

  /**
   * Tests for notification.
   *
   * @note A request is a notification if its identifier is undefined.
   */
  def isNotification: Boolean
}

/** Provides factory for `JsonRpcRequest`. */
object JsonRpcRequest {
  /** Provides builder for `JsonRpcRequest`. */
  class Builder private[JsonRpcRequest] {
    private var _version: String = "2.0"
    private var _id: Option[JsonRpcIdentifier] = None
    private var _method: String = null
    private var _params: Option[JsonValue] = None

    /** Sets version. */
    def version(value: String): this.type = {
      if (value == null) throw new NullPointerException()
      _version = value
      this
    }

    /** Sets identifier. */
    def id(value: JsonRpcIdentifier): this.type = {
      if (value == null) throw new NullPointerException()
      _id = Some(value)
      this
    }

    /** Sets identifier. */
    def id(value: String): this.type =
      id(JsonRpcIdentifier(value))

    /** Sets identifier. */
    def id(value: Long): this.type =
      id(JsonRpcIdentifier(value))

    /** Sets identifier to null value. */
    def idNull(): this.type =
      id(JsonRpcIdentifier.nullValue)

    /** Unsets identifier. */
    def idUndefined(): this.type = {
      _id = None
      this
    }

    /** Sets method. */
    def method(value: String): this.type = {
      if (value == null) throw new NullPointerException()
      _method = value
      this
    }

    /** Sets optional params. */
    def params(value: Option[JsonValue]): this.type = {
      if (value == null)
        throw new NullPointerException()

      if (!value.forall(_.isInstanceOf[JsonStructure]))
        throw new IllegalArgumentException("params must be either JSON array or object")

      _params = value
      this
    }

    /** Sets params. */
    def params(value: JsonValue): this.type =
      params(Some(value))

    /**
     * Sets params.
     *
     * @param value params
     * @param toJson converts params to JSON value
     */
    def params[T](value: T)(implicit toJson: JsonOutput[T]): this.type =
      params(Json.toJson(value))

    /** Creates `JsonRpcRequest` with current settings. */
    def build(): JsonRpcRequest = {
      if (_method == null) throw new IllegalStateException("method is not set")

      JsonRpcRequestImpl(_version, _id, _method, _params)
    }
  }

  /** Gets new request builder. */
  def builder(): Builder = new Builder

  /**
   * Creates `JsonRpcRequest` as notification &ndash; that is, without
   * identifier.
   *
   * @param version JSON-RPC version
   * @param method method name
   * @param params optional method params
   */
  def apply(version: String, method: String, params: Option[JsonValue]): JsonRpcRequest =
    builder()
      .version(version)
      .method(method)
      .params(params)
      .build()

  /**
   * Creates `JsonRpcRequest`.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param method method name
   * @param params optional method params
   */
  def apply(version: String, id: JsonRpcIdentifier, method: String, params: Option[JsonValue]): JsonRpcRequest =
    builder()
      .version(version)
      .id(id)
      .method(method)
      .params(params)
      .build()
}

private case class JsonRpcRequestImpl(
  version: String,
  idOption: Option[JsonRpcIdentifier],
  method: String,
  params: Option[JsonValue]) extends JsonRpcRequest {

  val isNotification = idOption.isEmpty

  def id = idOption.getOrElse(throw new NoSuchElementException("id"))
}

/** Represents JSON-RPC request. */
sealed trait JsonRpcResponse extends JsonRpcMessage {
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

/** Provides factory for `JsonRpcResponse`. */
object JsonRpcResponse {
  /** Provides builder for `JsonRpcResponse`. */
  final class Builder private[JsonRpcResponse] {
    private var _version: String = "2.0"
    private var _id: JsonRpcIdentifier = null
    private var _content: Either[JsonRpcError, JsonValue] = null

    /** Sets version. */
    def version(value: String): this.type = {
      if (value == null) throw new NullPointerException()
      _version = value
      this
    }

    /** Sets identifier. */
    def id(value: JsonRpcIdentifier): this.type = {
      if (value == null) throw new NullPointerException()
      _id = value
      this
    }

    /** Sets identifier. */
    def id(value: String): this.type =
      id(JsonRpcIdentifier(value))

    /** Sets identifier. */
    def id(value: Long): this.type =
      id(JsonRpcIdentifier(value))

    /** Sets identifier to null value. */
    def idNull(): this.type =
      id(JsonRpcIdentifier.nullValue)

    /** Sets result. */
    def result(value: JsonValue): this.type = {
      if (value == null)
        throw new NullPointerException()

      if (!value.isInstanceOf[JsonStructure])
        throw new IllegalArgumentException("result must be either JSON array or object")

      _content = Right(value)
      this
    }

    /**
     * Sets result.
     *
     * @param result result
     * @param toJson converts results to JSON value
     */
    def result[T](value: T)(implicit toJson: JsonOutput[T]): this.type =
      result(Json.toJson(value))

    /** Sets error. */
    def error(value: JsonRpcError): this.type = {
      if (value == null)
        throw new NullPointerException()

      _content = Left(value)
      this
    }

    /**
     * Sets error.
     *
     * @param code error code
     * @param message error message
     * @param data optional additional data
     * @param toJson converts data to JSON value
     */
    def error(code: Int, message: String, data: Option[JsonValue]): this.type =
      error(JsonRpcError(code, message, data))

    /**
     * Sets error.
     *
     * @param code error code
     * @param message error message
     * @param data additional data
     * @param toJson converts data to JSON value
     */
    def error(code: Int, message: String, data: JsonValue): this.type =
      error(JsonRpcError(code, message, data))

    /**
     * Sets error.
     *
     * @param code error code
     * @param message error message
     * @param data additional data
     * @param toJson converts data to JSON value
     */
    def error[T](code: Int, message: String, data: T)
        (implicit toJson: JsonOutput[T]): this.type =
      error(JsonRpcError(code, message, data))

    /** Creates `JsonRpcResponse` with current settings. */
    def build(): JsonRpcResponse = {
      if (_id == null) throw new IllegalStateException("id is not set")
      if (_content == null) throw new IllegalStateException("neither result nor error is set")

      new JsonRpcResponseImpl(_version, _id, _content)
    }
  }

  /** Gets new response builder. */
  def builder(): Builder = new Builder

  /**
   * Creates `JsonRpcResponse` with result.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param result result
   */
  def apply(version: String, id: JsonRpcIdentifier, result: JsonValue): JsonRpcResponse =
    builder()
      .version(version)
      .id(id)
      .result(result)
      .build()

  /**
   * Creates `JsonRpcResponse` with error.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param error error
   */
  def apply(version: String, id: JsonRpcIdentifier, error: JsonRpcError): JsonRpcResponse =
    builder()
      .version(version)
      .id(id)
      .error(error)
      .build()
}

private case class JsonRpcResponseImpl(
  version: String,
  id: JsonRpcIdentifier,
  content: Either[JsonRpcError, JsonValue]) extends JsonRpcResponse {

  def isResult = content.isRight
  def isError  = content.isLeft

  def result = content.getOrElse(throw new NoSuchElementException("result"))
  def error  = content.swap.getOrElse(throw new NoSuchElementException("error"))
}
