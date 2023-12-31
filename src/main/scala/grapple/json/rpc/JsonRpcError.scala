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

/** Defines JSON-RPC error. */
sealed class JsonRpcError protected (_code: Int, _message: String, _data: Option[JsonValue]) extends JsonException(_message):
  if _message == null || _data == null || _data.contains(null) then
    throw NullPointerException()

  /** Gets error code. */
  def code: Int = _code

  /** Gets error message. */
  def message: String = _message

  /** Gets optional error data. */
  def data: Option[JsonValue] = _data

  /** Tests for parse error. */
  def isParseError: Boolean = code == -32700

  /** Tests for invalid request. */
  def isInvalidRequest: Boolean = code == -32600

  /** Tests for method not found. */
  def isMethodNotFound: Boolean = code == -32601

  /** Tests for invalid params. */
  def isInvalidParams: Boolean = code == -32602

  /** Tests for internal error. */
  def isInternalError: Boolean = code == -32603

  /** Tests for server error. */
  def isServerError: Boolean = code >= -32099 && code <= -32000

  /** Returns string representation of error. */
  override def toString: String = s"$code ($message)"

/** Provides JSON-RPC error factory. */
object JsonRpcError:
  /**
   * Creates JSON-RPC error.
   *
   * @param code error code
   * @param message error message
   */
  def apply(code: Int, message: String): JsonRpcError =
    apply(code, message, None)

  /**
   * Creates JSON-RPC error.
   *
   * @param code error code
   * @param message error message
   * @param data error data
   */
  def apply(code: Int, message: String, data: JsonValue): JsonRpcError =
    apply(code, message, Some(data))

  /**
   * Creates JSON-RPC error.
   *
   * @param code error code
   * @param message error message
   * @param data optional error data
   */
  def apply(code: Int, message: String, data: Option[JsonValue]): JsonRpcError =
    code match
      case -32700 => new ParseError(message, data)
      case -32600 => new InvalidRequest(message, data)
      case -32601 => new MethodNotFound(message, data)
      case -32602 => new InvalidParams(message, data)
      case -32603 => new InternalError(message, data)
      case _      => new JsonRpcError(code, message, data)

  /**
   * Deconstructs `JsonRpcError` to its `code`, `message`, and `data`.
   *
   * @param error JSON-RPC error
   */
  def unapply(error: JsonRpcError): Option[(Int, String, Option[JsonValue])] =
    error match
      case null => None
      case _    => Some(error.code, error.message, error.data)

/** Defines JSON-RPC ParseError (-32700). */
final class ParseError private[rpc] (message: String, data: Option[JsonValue]) extends JsonRpcError(-32700, message, data):
  private[rpc] def this(data: Option[JsonValue]) = this("Parse error", data)

/** Provides `ParseError` factory. */
object ParseError:
  /** Creates `ParseError`. */
  def apply(): ParseError =
    apply(None)

  /**
   * Creates `ParseError` with data.
   *
   * @param data error data
   */
  def apply(data: String): ParseError =
    apply(Option(JsonString(data)))

  /**
   * Creates `ParseError` with data.
   *
   * @param data error data
   */
  def apply(data: JsonValue): ParseError =
    apply(Option(data))

  /**
   * Creates `ParseError` with optional data.
   *
   * @param data optional error data
   */
  def apply(data: Option[JsonValue]): ParseError =
    new ParseError(data)

  /**
   * Deconstructs `ParseError` to its `code`, `message`, and `data`.
   *
   * @param error parse error
   */
  def unapply(error: ParseError): Option[(Int, String, Option[JsonValue])] =
    error match
      case null => None
      case _    => Some(error.code, error.message, error.data)

/** Defines JSON-RPC InvalidRequest (-32600). */
final class InvalidRequest private[rpc] (message: String, data: Option[JsonValue]) extends JsonRpcError(-32600, message, data):
  private[rpc] def this(data: Option[JsonValue]) = this("Invalid Request", data)

/** Provides `InvalidRequest` factory. */
object InvalidRequest:
  /** Creates `InvalidRequest`. */
  def apply(): InvalidRequest =
    apply(None)

  /**
   * Creates `InvalidRequest` with data.
   *
   * @param data error data
   */
  def apply(data: String): InvalidRequest =
    apply(Option(JsonString(data)))

  /**
   * Creates `InvalidRequest` with data.
   *
   * @param data error data
   */
  def apply(data: JsonValue): InvalidRequest =
    apply(Option(data))

  /**
   * Creates `InvalidRequest` with optional data.
   *
   * @param data optional error data
   */
  def apply(data: Option[JsonValue]): InvalidRequest =
    new InvalidRequest(data)

  /**
   * Deconstructs `InvalidRequest` error to its `code`, `message`, and `data`.
   *
   * @param error invalid request
   */
  def unapply(error: InvalidRequest): Option[(Int, String, Option[JsonValue])] =
    error match
      case null => None
      case _    => Some(error.code, error.message, error.data)

/** Defines JSON-RPC MethodNotFound (-32601). */
final class MethodNotFound private[rpc] (message: String, data: Option[JsonValue]) extends JsonRpcError(-32601, message, data):
  private[rpc] def this(data: Option[JsonValue]) = this("Method not found", data)

/** Provides `MethodNotFound` factory. */
object MethodNotFound:
  /** Creates `MethodNotFound`.  */
  def apply(): MethodNotFound =
    apply(None)

  /**
   * Creates `MethodNotFound` with data.
   *
   * @param data error data
   */
  def apply(data: String): MethodNotFound =
    apply(Option(JsonString(data)))

  /**
   * Creates `MethodNotFound` with data.
   *
   * @param data error data
   */
  def apply(data: JsonValue): MethodNotFound =
    apply(Option(data))

  /**
   * Creates `MethodNotFound` with optional data.
   *
   * @param data optional error data
   */
  def apply(data: Option[JsonValue]): MethodNotFound =
    new MethodNotFound(data)

  /**
   * Deconstructs `MethodNotFound` error to its `code`, `message`, and `data`.
   *
   * @param error method not found
   */
  def unapply(error: MethodNotFound): Option[(Int, String, Option[JsonValue])] =
    error match
      case null => None
      case _    => Some(error.code, error.message, error.data)

/** Defines JSON-RPC InvalidParams (-32602). */
final class InvalidParams private[rpc] (message: String, data: Option[JsonValue]) extends JsonRpcError(-32602, message, data):
  private[rpc] def this(data: Option[JsonValue]) = this("Invalid params", data)

/** Provides `InvalidParams` factory. */
object InvalidParams:
  /** Creates `InvalidParams`. */
  def apply(): InvalidParams =
    apply(None)

  /**
   * Creates `InvalidParams` with data.
   *
   * @param data error data
   */
  def apply(data: String): InvalidParams =
    apply(Option(JsonString(data)))

  /**
   * Creates `InvalidParams` with data.
   *
   * @param data error data
   */
  def apply(data: JsonValue): InvalidParams =
    apply(Option(data))

  /**
   * Creates `InvalidParams` with optional data.
   *
   * @param data optional error data
   */
  def apply(data: Option[JsonValue]): InvalidParams =
    new InvalidParams(data)

  /**
   * Deconstructs `InvalidParams` error to its `code`, `message`, and `data`.
   *
   * @param error invalid params
   */
  def unapply(error: InvalidParams): Option[(Int, String, Option[JsonValue])] =
    error match
      case null => None
      case _    => Some(error.code, error.message, error.data)

/** Defines JSON-RPC InternalError (-32603). */
final class InternalError private[rpc] (message: String, data: Option[JsonValue]) extends JsonRpcError(-32603, message, data):
  private[rpc] def this(data: Option[JsonValue]) = this("Internal error", data)

/** Provides `InternalError` factory. */
object InternalError:
  /** Creates `InternalError`. */
  def apply(): InternalError =
    apply(None)

  /**
   * Creates `InternalError` with data.
   *
   * @param data error data
   */
  def apply(data: String): InternalError =
    apply(Option(JsonString(data)))

  /**
   * Creates `InternalError` with data.
   *
   * @param data additional information about error
   */
  def apply(data: JsonValue): InternalError =
    apply(Option(data))

  /**
   * Creates `InternalError` with optional data.
   *
   * @param data optional error data
   */
  def apply(data: Option[JsonValue]): InternalError =
    new InternalError(data)

  /**
   * Deconstructs `InternalError` error to its `code`, `message`, and `data`.
   *
   * @param error internal error
   */
  def unapply(error: InternalError): Option[(Int, String, Option[JsonValue])] =
    error match
      case null => None
      case _    => Some(error.code, error.message, error.data)
