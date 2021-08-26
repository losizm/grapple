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
package little.json
package rpc

import scala.util.Try

/** Defines JSON-RPC message. */
sealed trait JsonRpcMessage:
  /** Gets JSON-RPC version. */
  def version: String

  /** Gets identifier. */
  def id: JsonRpcIdentifier

  /**
   * Gets attributes.
   *
   * @note Attributes are arbitrary values associated with message and are not
   * included in transmission.
   */
  def attributes: Map[String, Any]

  /**
   * Gets attribute value.
   *
   * @param name attribute name
   *
   * @throws NoSuchElementException if attribute does not exist
   */
  def attribute[T](name: String): T =
    getAttribute(name).get

  /**
   * Gets optional attribute value.
   *
   * @param name attribute name
   */
  def getAttribute[T](name: String): Option[T] =
    attributes.get(name).map(_.asInstanceOf[T])

  /**
   * Gets attribute value or returns default if attribute does not exist.
   *
   * @param name attribute name
   * @param default default value
   */
  def getAttributeOrElse[T](name: String, default: => T): T =
    getAttribute(name).getOrElse(default)

/**
 * Defines JSON-RPC request.
 *
 * @see [[JsonRpcResponse]]
 */
sealed trait JsonRpcRequest extends JsonRpcMessage:
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

  /**
   * Sets attributes.
   *
   * @return new request
   */
  def setAttributes(attributes: Map[String, Any]): JsonRpcRequest

  /**
   * Puts attribute.
   *
   * @param name attribute name
   * @param value attribute value
   *
   * @return new request
   *
   * @note If attribute already exists with given name, then its value is
   * replaced.
   */
  def putAttribute(name: String, value: Any): JsonRpcRequest

  /**
   * Removes attribute.
   *
   * @param name attribute name
   *
   * @return new request
   */
  def removeAttribute(name: String): JsonRpcRequest

/** Provides JSON-RPC request factory. */
object JsonRpcRequest:
  /** Provides JSON-RPC request builder. */
  class Builder private[JsonRpcRequest]:
    private var _version: String = "2.0"
    private var _id: Option[JsonRpcIdentifier] = None
    private var _method: String = null
    private var _params: Option[JsonValue] = None
    private var _attributes: Map[String, Any] = Map.empty

    /** Sets version. */
    def version(value: String): this.type =
      if value == null then
        throw NullPointerException()
      _version = value
      this

    /** Sets identifier. */
    def id(value: JsonRpcIdentifier): this.type =
      if value == null then
        throw NullPointerException()
      _id = Some(value)
      this

    /** Sets identifier. */
    def id(value: String): this.type =
      id(JsonRpcIdentifier(value))

    /** Sets identifier. */
    def id(value: Long): this.type =
      id(JsonRpcIdentifier(value))

    /** Sets identifier to null value. */
    def idNull(): this.type =
      id(JsonRpcIdentifier.nullified)

    /** Unsets identifier. */
    def idUndefined(): this.type =
      _id = None
      this

    /** Sets method. */
    def method(value: String): this.type =
      if value == null then
        throw NullPointerException()
      _method = value
      this

    /** Sets optional params. */
    def params(value: Option[JsonValue]): this.type =
      if value == null || value.contains(null) then
        throw NullPointerException()

      if !value.forall(_.isInstanceOf[JsonStructure]) then
        throw IllegalArgumentException("params must be JSON structure")

      _params = value
      this

    /** Sets params. */
    def params(value: JsonValue): this.type =
      if value == null then
        throw NullPointerException()

      if !value.isInstanceOf[JsonStructure] then
        throw IllegalArgumentException("params must be JSON structure")

      _params = Some(value)
      this

    /** Sets attributes. */
    def attributes(value: Map[String, Any]): this.type =
      if value == null then
        throw NullPointerException()
      _attributes = value
      this

    /** Sets attributes. */
    def attributes(one: (String, Any), more: (String, Any)*): this.type =
      attributes((one +: more).toMap)

    /** Creates JSON-RPC request with current settings. */
    def build(): JsonRpcRequest =
      if _method == null then
        throw IllegalStateException("method is not set")
      JsonRpcRequestImpl(_version, _id, _method, _params, _attributes)

  /** Gets new request builder. */
  def builder(): Builder = Builder()

  /**
   * Creates JSON-RPC request as notification &ndash; that is, without
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
   * Creates JSON-RPC request.
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

private case class JsonRpcRequestImpl(
  version:    String,
  idOption:   Option[JsonRpcIdentifier],
  method:     String,
  params:     Option[JsonValue],
  attributes: Map[String, Any] = Map.empty
) extends JsonRpcRequest:

  val isNotification = idOption.isEmpty

  def id = idOption.getOrElse(throw new NoSuchElementException("id"))

  def setAttributes(attributes: Map[String, Any]) =
    copy(attributes = attributes)

  def putAttribute(name: String, value: Any) =
    copy(attributes = attributes + (name -> value))

  def removeAttribute(name: String) =
    copy(attributes = attributes - name)

/**
 * Defines JSON-RPC response.
 *
 * @see [[JsonRpcRequest]]
 */
sealed trait JsonRpcResponse extends JsonRpcMessage:
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

  /**
   * Sets attributes.
   *
   * @return new response
   */
  def setAttributes(attributes: Map[String, Any]): JsonRpcResponse

  /**
   * Puts attribute.
   *
   * @param name attribute name
   * @param value attribute value
   *
   * @return new response
   *
   * @note If attribute already exists with given name, then its value is
   * replaced.
   */
  def putAttribute(name: String, value: Any): JsonRpcResponse

  /**
   * Removes attribute.
   *
   * @param name attribute name
   *
   * @return new response
   */
  def removeAttribute(name: String): JsonRpcResponse

/** Provides JSON-RPC response factory. */
object JsonRpcResponse:
  /** Provides JSON-RPC response builder. */
  final class Builder private[JsonRpcResponse]:
    private var _version: String = "2.0"
    private var _id: JsonRpcIdentifier = null
    private var _content: Either[JsonRpcError, JsonValue] = null
    private var _attributes: Map[String, Any] = Map.empty

    /** Sets version. */
    def version(value: String): this.type =
      if value == null then
        throw NullPointerException()
      _version = value
      this

    /** Sets identifier. */
    def id(value: JsonRpcIdentifier): this.type =
      if value == null then
        throw NullPointerException()
      _id = value
      this

    /** Sets identifier. */
    def id(value: String): this.type =
      id(JsonRpcIdentifier(value))

    /** Sets identifier. */
    def id(value: Long): this.type =
      id(JsonRpcIdentifier(value))

    /** Sets identifier to null value. */
    def idNull(): this.type =
      id(JsonRpcIdentifier.nullified)

    /**
     * Tries to set result or sets error on failure.
     *
     * @note If `value` throws exception that does not match `onFailure`, then
     * the exception is raised.
     */
    def tryResult(value: => JsonValue)(using onFailure: PartialFunction[Throwable, JsonRpcError]): this.type =
      Try(value)
        .map(result(_))
        .recover { case err if onFailure.isDefinedAt(err) => error(onFailure(err)) }
        .get
      this

    /** Sets either result or error. */
    def resultOrError(value: JsonValue | JsonRpcError): this.type =
      value match
        case json: JsonValue   => result(json)
        case err: JsonRpcError => error(err)

    /** Sets result. */
    def result(value: JsonValue): this.type =
      if value == null then
        throw NullPointerException()
      _content = Right(value)
      this

    /** Sets error. */
    def error(value: JsonRpcError): this.type =
      if value == null then
        throw NullPointerException()
      _content = Left(value)
      this

    /**
     * Sets error.
     *
     * @param code error code
     * @param message error message
     * @param data optional additional data
     */
    def error(code: Int, message: String, data: Option[JsonValue]): this.type =
      error(JsonRpcError(code, message, data))

    /**
     * Sets error.
     *
     * @param code error code
     * @param message error message
     * @param data additional data
     */
    def error(code: Int, message: String, data: JsonValue): this.type =
      error(JsonRpcError(code, message, data))

    /** Sets attributes. */
    def attributes(value: Map[String, Any]): this.type =
      if value == null then
        throw NullPointerException()
      _attributes = value
      this

    /** Sets attributes. */
    def attributes(one: (String, Any), more: (String, Any)*): this.type =
      attributes((one +: more).toMap)

    /** Creates JSON-RPC response with current settings. */
    def build(): JsonRpcResponse =
      if _id == null then
        throw IllegalStateException("id is not set")
      if _content == null then
        throw IllegalStateException("neither result nor error is set")
      JsonRpcResponseImpl(_version, _id, _content, _attributes)

  /** Gets new response builder. */
  def builder(): Builder = Builder()

  /**
   * Creates JSON-RPC response with result.
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
   * Creates JSON-RPC response with error.
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

private case class JsonRpcResponseImpl(
  version:    String,
  id:         JsonRpcIdentifier,
  content:    Either[JsonRpcError, JsonValue],
  attributes: Map[String, Any] = Map.empty
) extends JsonRpcResponse:

  def isResult = content.isRight
  def isError  = content.isLeft

  def result = content.getOrElse(throw new NoSuchElementException("result"))
  def error  = content.swap.getOrElse(throw new NoSuchElementException("error"))

  def setAttributes(attributes: Map[String, Any]) =
    copy(attributes = attributes)

  def putAttribute(name: String, value: Any) =
    copy(attributes = attributes + (name -> value))

  def removeAttribute(name: String) =
    copy(attributes = attributes - name)
