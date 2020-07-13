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
    new JsonRpcError(code, message, data)

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
 */
final class ParseError private (data: Option[JsonValue])
  extends JsonRpcError(-32700, "Parse error", data)

/** Provides factory for `ParseError`. */
object ParseError {
  /** Creates `ParseError`. */
  def apply(): ParseError =
    apply(None)

  /**
   * Creates `ParseError`.
   *
   * @param data additional information about error
   */
  def apply(data: JsonValue): ParseError =
    apply(Option(data))

  /**
   * Creates `ParseError`.
   *
   * @param data additional information about error
   * @param toJson converts data to JSON
   */
  def apply[T](data: T)(implicit toJson: JsonOutput[T]): ParseError =
    apply(Option(Json.toJson(data)))

  /**
   * Creates `ParseError`.
   *
   * @param data additional information about error
   */
  def apply(data: Option[JsonValue]): ParseError =
    new ParseError(data)

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
 */
final class InvalidRequest private (data: Option[JsonValue])
  extends JsonRpcError(-32600, "Invalid request", data)

/** Provides factory for `InvalidRequest`. */
object InvalidRequest {
  /** Creates `InvalidRequest`. */
  def apply(): InvalidRequest =
    apply(None)

  /**
   * Creates `InvalidRequest`.
   *
   * @param data additional information about error
   */
  def apply(data: JsonValue): InvalidRequest =
    apply(Option(data))

  /**
   * Creates `InvalidRequest`.
   *
   * @param data additional information about error
   * @param toJson converts data to JSON
   */
  def apply[T](data: T)(implicit toJson: JsonOutput[T]): InvalidRequest =
    apply(Option(Json.toJson(data)))

  /**
   * Creates `InvalidRequest`.
   *
   * @param data additional information about error
   */
  def apply(data: Option[JsonValue]): InvalidRequest =
    new InvalidRequest(data)

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
 */
final class MethodNotFound private (data: Option[JsonValue])
  extends JsonRpcError(-32601, "Method not found", data)

/** Provides factory for `MethodNotFound`. */
object MethodNotFound {
  /** Creates `MethodNotFound`.  */
  def apply(): MethodNotFound =
    apply(None)

  /**
   * Creates `MethodNotFound`.
   *
   * @param data additional information about error
   */
  def apply(data: JsonValue): MethodNotFound =
    apply(Option(data))

  /**
   * Creates `MethodNotFound`.
   *
   * @param data additional information about error
   * @param toJson converts data to JSON
   */
  def apply[T](data: T)(implicit toJson: JsonOutput[T]): MethodNotFound =
    apply(Option(Json.toJson(data)))

  /**
   * Creates `MethodNotFound`.
   *
   * @param data additional information about error
   */
  def apply(data: Option[JsonValue]): MethodNotFound =
    new MethodNotFound(data)

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
 */
final class InvalidParams private (data: Option[JsonValue])
  extends JsonRpcError(-32602, "Invalid params", data)

/** Provides factory for `InvalidParams`. */
object InvalidParams {
  /** Creates `InvalidParams`. */
  def apply(): InvalidParams =
    apply(None)

  /**
   * Creates `InvalidParams`.
   *
   * @param data additional information about error
   */
  def apply(data: JsonValue): InvalidParams =
    apply(Option(data))

  /**
   * Creates `InvalidParams`.
   *
   * @param data additional information about error
   * @param toJson converts data to JSON
   */
  def apply[T](data: T)(implicit toJson: JsonOutput[T]): InvalidParams =
    apply(Option(Json.toJson(data)))

  /**
   * Creates `InvalidParams`.
   *
   * @param data additional information about error
   */
  def apply(data: Option[JsonValue]): InvalidParams =
    new InvalidParams(data)

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
 */
final class InternalError private (data: Option[JsonValue])
  extends JsonRpcError(-32603, "Internal error", data)

/** Provides factory for `InternalError`. */
object InternalError {
  /** Creates `InternalError`. */
  def apply(): InternalError =
    apply(None)

  /**
   * Creates `InternalError`.
   *
   * @param data additional information about error
   */
  def apply(data: JsonValue): InternalError =
    apply(Option(data))

  /**
   * Creates `InternalError`.
   *
   * @param data additional information about error
   * @param toJson converts data to JSON
   */
  def apply[T](data: T)(implicit toJson: JsonOutput[T]): InternalError =
    apply(Option(Json.toJson(data)))

  /**
   * Creates `InternalError`.
   *
   * @param data additional information about error
   */
  def apply(data: Option[JsonValue]): InternalError =
    new InternalError(data)

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
