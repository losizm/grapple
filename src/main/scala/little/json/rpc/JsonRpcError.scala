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

import little.json.{ Json, JsonOutput }

/**
 * Represents JSON-RPC error.
 *
 * @constructor Creates JSON-RPC error.
 *
 * @param code error code
 * @param message error message
 * @param data additional information about error
 */
sealed class JsonRpcError protected (_code: Int, _message: String, _data: Option[JsonValue]) extends RuntimeException(_message) {
  /** Gets error code. */
  def code: Int = _code

  /** Gets error message. */
  def message: String = _message

  /** Gets additional information about error. */
  def data: Option[JsonValue] = _data

  /** Tests for parse error. */
  def isParseError: Boolean =
    code == -32700

  /** Tests for invalid request. */
  def isInvalidRequest: Boolean =
    code == -32600

  /** Tests for method not found. */
  def isMethodNotFound: Boolean =
    code == -32601

  /** Tests for invalid params. */
  def isInvalidParams: Boolean =
    code == -32602

  /** Tests for internal error. */
  def isInternalError: Boolean =
    code == -32603
}

/** Provides factory for `JsonRpcError`. */
object JsonRpcError {
  /**
   * Creates `JsonRpcError`.
   *
   * @param code error code
   * @param message error message
   */
  def apply(code: Int, message: String): JsonRpcError =
    apply(code, message, None)

  /**
   * Creates `JsonRpcError`.
   *
   * @param code error code
   * @param message error message
   * @param data additional information about error
   */
  def apply(code: Int, message: String, data: JsonValue): JsonRpcError =
    apply(code, message, Option(data))

  /**
   * Creates `JsonRpcError`.
   *
   * @param code error code
   * @param message error message
   * @param data additional information about error
   * @param toJson converts data to JSON
   */
  def apply[T](code: Int, message: String, data: T)(implicit toJson: JsonOutput[T]): JsonRpcError =
    apply(code, message, Option(Json.toJson(data)))

  /**
   * Creates `JsonRpcError`.
   *
   * @param code error code
   * @param message error message
   * @param data additional information about error
   */
  def apply(code: Int, message: String, data: Option[JsonValue]): JsonRpcError =
    code match {
      case -32700 => ParseError(message, data)
      case -32600 => InvalidRequest(message, data)
      case -32601 => MethodNotFound(message, data)
      case -32602 => InvalidParams(message, data)
      case -32603 => InternalError(message, data)
      case _      => new JsonRpcError(code, message, data)
    }

  /**
   * Destructures `JsonRpcError` to its `code`, `message`, and `data`.
   *
   * @param error JSON-RPC error
   */
  def unapply(error: JsonRpcError): Option[(Int, String, Option[JsonValue])] =
    error match {
      case null => None
      case _    => Some(error.code, error.message, error.data)
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
final class ParseError private (message: String, data: Option[JsonValue])
  extends JsonRpcError(-32700, message, data)

/** Provides factory for `ParseError`. */
object ParseError {
  /**
   * Creates `ParseError`.
   *
   * @param message error message
   */
  def apply(message: String): ParseError =
    apply(message, None)

  /**
   * Creates `ParseError`.
   *
   * @param message error message
   * @param data additional information about error
   */
  def apply(message: String, data: JsonValue): ParseError =
    apply(message, Option(data))

  /**
   * Creates `ParseError`.
   *
   * @param message error message
   * @param data additional information about error
   * @param toJson converts data to JSON
   */
  def apply[T](message: String, data: T)(implicit toJson: JsonOutput[T]): ParseError =
    apply(message, Option(Json.toJson(data)))

  /**
   * Creates `ParseError`.
   *
   * @param message error message
   * @param data additional information about error
   */
  def apply(message: String, data: Option[JsonValue]): ParseError =
    new ParseError(message, data)

  /**
   * Destructures `ParseError` to its `code`, `message`, and `data`.
   *
   * @param error parse error
   */
  def unapply(error: ParseError): Option[(Int, String, Option[JsonValue])] =
    error match {
      case null => None
      case _    => Some(error.code, error.message, error.data)
    }
}

/**
 * Represents JSON-RPC invalid request (code = -32600).
 *
 * @constructor Creates JSON-RPC invalid request.
 *
 * @param message error message
 * @param data additional information about error
 */
final class InvalidRequest private (message: String, data: Option[JsonValue])
  extends JsonRpcError(-32600, message, data)

/** Provides factory for `InvalidRequest`. */
object InvalidRequest {
  /**
   * Creates `InvalidRequest`.
   *
   * @param message error message
   */
  def apply(message: String): InvalidRequest =
    apply(message, None)

  /**
   * Creates `InvalidRequest`.
   *
   * @param message error message
   * @param data additional information about error
   */
  def apply(message: String, data: JsonValue): InvalidRequest =
    apply(message, Option(data))

  /**
   * Creates `InvalidRequest`.
   *
   * @param message error message
   * @param data additional information about error
   * @param toJson converts data to JSON
   */
  def apply[T](message: String, data: T)(implicit toJson: JsonOutput[T]): InvalidRequest =
    apply(message, Option(Json.toJson(data)))

  /**
   * Creates `InvalidRequest`.
   *
   * @param message error message
   * @param data additional information about error
   */
  def apply(message: String, data: Option[JsonValue]): InvalidRequest =
    new InvalidRequest(message, data)

  /**
   * Destructures `InvalidRequest` error to its `code`, `message`, and `data`.
   *
   * @param error invalid request
   */
  def unapply(error: InvalidRequest): Option[(Int, String, Option[JsonValue])] =
    error match {
      case null => None
      case _    => Some(error.code, error.message, error.data)
    }
}

/**
 * Represents JSON-RPC method not found (code = -32601).
 *
 * @constructor Creates JSON-RPC method not found.
 *
 * @param message error message
 * @param data additional information about error
 */
final class MethodNotFound private (message: String, data: Option[JsonValue])
  extends JsonRpcError(-32601, message, data)

/** Provides factory for `MethodNotFound`. */
object MethodNotFound {
  /**
   * Creates `MethodNotFound`.
   *
   * @param message error message
   */
  def apply(message: String): MethodNotFound =
    apply(message, None)

  /**
   * Creates `MethodNotFound`.
   *
   * @param message error message
   * @param data additional information about error
   */
  def apply(message: String, data: JsonValue): MethodNotFound =
    apply(message, Option(data))

  /**
   * Creates `MethodNotFound`.
   *
   * @param message error message
   * @param data additional information about error
   * @param toJson converts data to JSON
   */
  def apply[T](message: String, data: T)(implicit toJson: JsonOutput[T]): MethodNotFound =
    apply(message, Option(Json.toJson(data)))

  /**
   * Creates `MethodNotFound`.
   *
   * @param message error message
   * @param data additional information about error
   */
  def apply(message: String, data: Option[JsonValue]): MethodNotFound =
    new MethodNotFound(message, data)

  /**
   * Destructures `MethodNotFound` error to its `code`, `message`, and `data`.
   *
   * @param error method not found
   */
  def unapply(error: MethodNotFound): Option[(Int, String, Option[JsonValue])] =
    error match {
      case null => None
      case _    => Some(error.code, error.message, error.data)
    }
}

/**
 * Represents JSON-RPC invalid params (code = -32602).
 *
 * @constructor Creates JSON-RPC invalid params.
 *
 * @param message error message
 * @param data additional information about error
 */
final class InvalidParams private (message: String, data: Option[JsonValue])
  extends JsonRpcError(-32602, message, data)

/** Provides factory for `InvalidParams`. */
object InvalidParams {
  /**
   * Creates `InvalidParams`.
   *
   * @param message error message
   */
  def apply(message: String): InvalidParams =
    apply(message, None)

  /**
   * Creates `InvalidParams`.
   *
   * @param message error message
   * @param data additional information about error
   */
  def apply(message: String, data: JsonValue): InvalidParams =
    apply(message, Option(data))

  /**
   * Creates `InvalidParams`.
   *
   * @param message error message
   * @param data additional information about error
   * @param toJson converts data to JSON
   */
  def apply[T](message: String, data: T)(implicit toJson: JsonOutput[T]): InvalidParams =
    apply(message, Option(Json.toJson(data)))

  /**
   * Creates `InvalidParams`.
   *
   * @param message error message
   * @param data additional information about error
   */
  def apply(message: String, data: Option[JsonValue]): InvalidParams =
    new InvalidParams(message, data)

  /**
   * Destructures `InvalidParams` error to its `code`, `message`, and `data`.
   *
   * @param error invalid params
   */
  def unapply(error: InvalidParams): Option[(Int, String, Option[JsonValue])] =
    error match {
      case null => None
      case _    => Some(error.code, error.message, error.data)
    }
}

/**
 * Represents JSON-RPC internal error (code = -32603).
 *
 * @constructor Creates JSON-RPC internal error.
 *
 * @param message error message
 * @param data additional information about error
 */
final class InternalError private (message: String, data: Option[JsonValue])
  extends JsonRpcError(-32603, message, data)

/** Provides factory for `InternalError`. */
object InternalError {
  /**
   * Creates `InternalError`.
   *
   * @param message error message
   */
  def apply(message: String): InternalError =
    apply(message, None)

  /**
   * Creates `InternalError`.
   *
   * @param message error message
   * @param data additional information about error
   */
  def apply(message: String, data: JsonValue): InternalError =
    apply(message, Option(data))

  /**
   * Creates `InternalError`.
   *
   * @param message error message
   * @param data additional information about error
   * @param toJson converts data to JSON
   */
  def apply[T](message: String, data: T)(implicit toJson: JsonOutput[T]): InternalError =
    apply(message, Option(Json.toJson(data)))

  /**
   * Creates `InternalError`.
   *
   * @param message error message
   * @param data additional information about error
   */
  def apply(message: String, data: Option[JsonValue]): InternalError =
    new InternalError(message, data)

  /**
   * Destructures `InternalError` error to its `code`, `message`, and `data`.
   *
   * @param error internal error
   */
  def unapply(error: InternalError): Option[(Int, String, Option[JsonValue])] =
    error match {
      case null => None
      case _    => Some(error.code, error.message, error.data)
    }
}
