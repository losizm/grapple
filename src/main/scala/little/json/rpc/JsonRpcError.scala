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

import javax.json.JsonValue

/**
 * Represents JSON-RPC error.
 *
 * @constructor Creates JSON-RPC error.
 *
 * @param code error code
 * @param message error message
 * @param data additional information about error
 */
sealed class JsonRpcError protected (code: Int, message: String, data: Option[JsonValue] = None) extends RuntimeException(message) {
  /** Gets error code. */
  def getCode(): Int = code

  /** Gets additional information about error. */
  def getData(): Option[JsonValue] = data

  /** Tests for parse error. */
  def isParseError(): Boolean =
    code == -32700

  /** Tests for invalid request. */
  def isInvalidRequest(): Boolean =
    code == -32600

  /** Tests for method not found. */
  def isMethodNotFound(): Boolean =
    code == -32601

  /** Tests for invalid params. */
  def isInvalidParams(): Boolean =
    code == -32602

  /** Tests for internal error. */
  def isInternalError(): Boolean =
    code == -32603
}

/** Provides factory for `JsonRpcError`. */
object JsonRpcError {
  /**
   * Creates `JsonRpcError`.
   *
   * @param code error code
   * @param message error message
   * @param data additional information about error
   */
  def apply(code: Int, message: String, data: Option[JsonValue] = None): JsonRpcError =
    code match {
      case -32700 => new ParseError(message, data)
      case -32600 => new InvalidRequest(message, data)
      case -32601 => new MethodNotFound(message, data)
      case -32602 => new InvalidParams(message, data)
      case -32603 => new InternalError(message, data)
      case _      => new JsonRpcError(code, message, data)
    }
}

/**
 * Represents JSON-RPC parse error (code = -32700).
 *
 * @constructor Creates JSON-RPC parse error.
 *
 * @param message error message
 * @param data additional information about error
 */
final class ParseError(message: String, data: Option[JsonValue] = None)
  extends JsonRpcError(-32700, message, data)

/** Provides factory for `ParseError`. */
object ParseError {
  /**
   * Creates `ParseError`.
   *
   * @param message error message
   * @param data additional information about error
   */
  def apply(message: String, data: Option[JsonValue] = None): ParseError =
    new ParseError(message, data)
}

/**
 * Represents JSON-RPC invalid request (code = -32600).
 *
 * @constructor Creates JSON-RPC invalid request.
 *
 * @param message error message
 * @param data additional information about error
 */
final class InvalidRequest(message: String, data: Option[JsonValue] = None)
  extends JsonRpcError(-32600, message, data)

/** Provides factory for `InvalidRequest`. */
object InvalidRequest {
  /**
   * Creates `InvalidRequest`.
   *
   * @param message error message
   * @param data additional information about error
   */
  def apply(message: String, data: Option[JsonValue] = None): InvalidRequest =
    new InvalidRequest(message, data)
}

/**
 * Represents JSON-RPC method not found (code = -32601).
 *
 * @constructor Creates JSON-RPC method not found.
 *
 * @param message error message
 * @param data additional information about error
 */
final class MethodNotFound(message: String, data: Option[JsonValue] = None)
  extends JsonRpcError(-32601, message, data)

/** Provides factory for `MethodNotFound`. */
object MethodNotFound {
  /**
   * Creates `MethodNotFound`.
   *
   * @param message error message
   * @param data additional information about error
   */
  def apply(message: String, data: Option[JsonValue] = None): MethodNotFound =
    new MethodNotFound(message, data)
}

/**
 * Represents JSON-RPC invalid params (code = -32602).
 *
 * @constructor Creates JSON-RPC invalid params.
 *
 * @param message error message
 * @param data additional information about error
 */
final class InvalidParams(message: String, data: Option[JsonValue] = None)
  extends JsonRpcError(-32602, message, data)

/** Provides factory for `InvalidParams`. */
object InvalidParams {
  /**
   * Creates `InvalidParams`.
   *
   * @param message error message
   * @param data additional information about error
   */
  def apply(message: String, data: Option[JsonValue] = None): InvalidParams =
    new InvalidParams(message, data)
}

/**
 * Represents JSON-RPC internal error (code = -32603).
 *
 * @constructor Creates JSON-RPC internal error.
 *
 * @param message error message
 * @param data additional information about error
 */
final class InternalError(message: String, data: Option[JsonValue] = None)
  extends JsonRpcError(-32603, message, data)

/** Provides factory for `InternalError`. */
object InternalError {
  /**
   * Creates `InternalError`.
   *
   * @param message error message
   * @param data additional information about error
   */
  def apply(message: String, data: Option[JsonValue] = None): InternalError =
    new InternalError(message, data)
}
