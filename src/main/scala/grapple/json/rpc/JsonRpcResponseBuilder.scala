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

import scala.util.Try

/** Provides JSON-RPC response builder. */
final class JsonRpcResponseBuilder:
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
  def toJsonRpcResponse(): JsonRpcResponse =
    if _id == null then
      throw IllegalStateException("id is not set")
    if _content == null then
      throw IllegalStateException("neither result nor error is set")
    JsonRpcResponseImpl(_version, _id, _content, _attributes)
