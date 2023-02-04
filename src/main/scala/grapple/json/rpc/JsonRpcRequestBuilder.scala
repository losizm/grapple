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

/** Provides JSON-RPC request builder. */
class JsonRpcRequestBuilder:
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
  def toJsonRpcRequest(): JsonRpcRequest =
    if _method == null then
      throw IllegalStateException("method is not set")
    JsonRpcRequestImpl(_version, _id, _method, _params, _attributes)
