/*
 * Copyright 2023 Carlos Conyers
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
  /**
   * Creates JSON-RPC request as notification &ndash; that is, without
   * identifier.
   *
   * @param version JSON-RPC version
   * @param method method name
   * @param params optional method params
   */
  def apply(version: String, method: String, params: Option[JsonValue]): JsonRpcRequest =
    JsonRpcRequestBuilder()
      .version(version)
      .method(method)
      .params(params)
      .toJsonRpcRequest()

  /**
   * Creates JSON-RPC request.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param method method name
   * @param params optional method params
   */
  def apply(version: String, id: JsonRpcIdentifier, method: String, params: Option[JsonValue]): JsonRpcRequest =
    JsonRpcRequestBuilder()
      .version(version)
      .id(id)
      .method(method)
      .params(params)
      .toJsonRpcRequest()

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
  /**
   * Creates JSON-RPC response with result.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param result result
   */
  def apply(version: String, id: JsonRpcIdentifier, result: JsonValue): JsonRpcResponse =
    JsonRpcResponseBuilder()
      .version(version)
      .id(id)
      .result(result)
      .toJsonRpcResponse()

  /**
   * Creates JSON-RPC response with error.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param error error
   */
  def apply(version: String, id: JsonRpcIdentifier, error: JsonRpcError): JsonRpcResponse =
    JsonRpcResponseBuilder()
      .version(version)
      .id(id)
      .error(error)
      .toJsonRpcResponse()

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
