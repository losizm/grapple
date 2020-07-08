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

  /** Gets message identifier. */
  def id: JsonRpcIdentifier
}

/** Represents JSON-RPC request. */
sealed trait JsonRpcRequest extends JsonRpcMessage {
  /** Gets method. */
  def method: String

  /** Gets parameters. */
  def params: Option[JsonValue]
}

/** Provides factory for `JsonRpcRequest`. */
object JsonRpcRequest {
  /**
   * Creates `JsonRpcRequest`.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param method method name
   */
  def apply(version: String, id: JsonRpcIdentifier, method: String): JsonRpcRequest =
    apply(version, id, method, None)

  /**
   * Creates `JsonRpcRequest`.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param method method name
   */
  def apply(version: String, id: String, method: String): JsonRpcRequest =
    apply(version, id, method, None)

  /**
   * Creates `JsonRpcRequest`.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param method method name
   */
  def apply(version: String, id: Long, method: String): JsonRpcRequest =
    apply(version, id, method, None)

  /**
   * Creates `JsonRpcRequest`.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param method method name
   * @param params method params
   */
  def apply(version: String, id: JsonRpcIdentifier, method: String, params: JsonValue): JsonRpcRequest =
    apply(version, id, method, Option(params))

  /**
   * Creates `JsonRpcRequest`.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param method method name
   * @param params method params
   */
  def apply(version: String, id: String, method: String, params: JsonValue): JsonRpcRequest =
    apply(version, id, method, Option(params))

  /**
   * Creates `JsonRpcRequest`.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param method method name
   * @param params method params
   */
  def apply(version: String, id: Long, method: String, params: JsonValue): JsonRpcRequest =
    apply(version, id, method, Option(params))

  /**
   * Creates `JsonRpcRequest`.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param method method name
   * @param params method params
   * @param toJson converts params to JSON
   */
  def apply[T](version: String, id: JsonRpcIdentifier, method: String, params: T)
      (implicit toJson: JsonOutput[T]): JsonRpcRequest =
    apply(version, id, method, Option(Json.toJson(params)))

  /**
   * Creates `JsonRpcRequest`.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param method method name
   * @param params method params
   * @param toJson converts params to JSON
   */
  def apply[T](version: String, id: String, method: String, params: T)
      (implicit toJson: JsonOutput[T]): JsonRpcRequest =
    apply(version, id, method, Option(Json.toJson(params)))

  /**
   * Creates `JsonRpcRequest`.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param method method name
   * @param params method params
   * @param toJson converts params to JSON
   */
  def apply[T](version: String, id: Long, method: String, params: T)
      (implicit toJson: JsonOutput[T]): JsonRpcRequest =
    apply(version, id, method, Option(Json.toJson(params)))

  /**
   * Creates `JsonRpcRequest`.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param method method name
   * @param params method params
   */
  def apply(version: String, id: JsonRpcIdentifier, method: String, params: Option[JsonValue]): JsonRpcRequest = {
    if (version == null) throw new NullPointerException()
    if (id == null) throw new NullPointerException()
    if (method == null) throw new NullPointerException()
    if (params == null) throw new NullPointerException()

    JsonRpcRequestImpl(version, id, method, params)
  }

  /**
   * Creates `JsonRpcRequest`.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param method method name
   * @param params method params
   */
  def apply(version: String, id: String, method: String, params: Option[JsonValue]): JsonRpcRequest = {
    if (version == null) throw new NullPointerException()
    if (method == null) throw new NullPointerException()
    if (params == null) throw new NullPointerException()

    JsonRpcRequestImpl(version, JsonRpcIdentifier(id), method, params)
  }

  /**
   * Creates `JsonRpcRequest`.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param method method name
   * @param params method params
   */
  def apply(version: String, id: Long, method: String, params: Option[JsonValue]): JsonRpcRequest = {
    if (version == null) throw new NullPointerException()
    if (method == null) throw new NullPointerException()
    if (params == null) throw new NullPointerException()

    JsonRpcRequestImpl(version, JsonRpcIdentifier(id), method, params)
  }
}

private case class JsonRpcRequestImpl(
  version: String,
  id: JsonRpcIdentifier,
  method: String,
  params: Option[JsonValue]) extends JsonRpcRequest

/** Represents JSON-RPC request. */
sealed trait JsonRpcResponse extends JsonRpcMessage {
  /** Gets result. */
  def result: JsonRpcResult
}

/** Provides factor for `JsonRpcResponse`. */
object JsonRpcResponse {
  /**
   * Creates `JsonRpcResponse`.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param result result
   */
  def apply(version: String, id: JsonRpcIdentifier, result: JsonRpcResult): JsonRpcResponse = {
    if (version == null) throw new NullPointerException()
    if (id == null) throw new NullPointerException()
    if (result == null) throw new NullPointerException()

    JsonRpcResponseImpl(version, id, result)
  }

  /**
   * Creates `JsonRpcResponse`.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param result result
   */
  def apply(version: String, id: String, result: JsonRpcResult): JsonRpcResponse = {
    if (version == null) throw new NullPointerException()
    if (result == null) throw new NullPointerException()

    JsonRpcResponseImpl(version, JsonRpcIdentifier(id), result)
  }

  /**
   * Creates `JsonRpcResponse`.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param result result
   */
  def apply(version: String, id: Long, result: JsonRpcResult): JsonRpcResponse = {
    if (version == null) throw new NullPointerException()
    if (result == null) throw new NullPointerException()

    JsonRpcResponseImpl(version, JsonRpcIdentifier(id), result)
  }

  /**
   * Creates `JsonRpcResponse` with result.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param result result
   */
  def apply(version: String, id: JsonRpcIdentifier, result: JsonValue): JsonRpcResponse = {
    if (version == null) throw new NullPointerException()
    if (id == null) throw new NullPointerException()
    if (result == null) throw new NullPointerException()

    require(result.isInstanceOf[JsonStructure],
      "result must be JSON array or object value")

    JsonRpcResponseImpl(version, id, JsonRpcResult(result))
  }

  /**
   * Creates `JsonRpcResponse` with result.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param result result
   */
  def apply(version: String, id: String, result: JsonValue): JsonRpcResponse = {
    if (version == null) throw new NullPointerException()
    if (result == null) throw new NullPointerException()

    JsonRpcResponseImpl(version, JsonRpcIdentifier(id), JsonRpcResult(result))
  }

  /**
   * Creates `JsonRpcResponse` with result.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param result result
   */
  def apply(version: String, id: Long, result: JsonValue): JsonRpcResponse = {
    if (version == null) throw new NullPointerException()
    if (result == null) throw new NullPointerException()

    JsonRpcResponseImpl(version, JsonRpcIdentifier(id), JsonRpcResult(result))
  }

  /**
   * Creates `JsonRpcResponse` with result.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param result result
   * @param toJson converts params to JSON
   */
  def apply[T](version: String, id: JsonRpcIdentifier, result: T)
      (implicit toJson: JsonOutput[T]): JsonRpcResponse =
    apply(version, id, Json.toJson(result))

  /**
   * Creates `JsonRpcResponse` with result.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param result result
   * @param toJson converts params to JSON
   */
  def apply[T](version: String, id: String, result: T)
      (implicit toJson: JsonOutput[T]): JsonRpcResponse =
    apply(version, id, Json.toJson(result))

  /**
   * Creates `JsonRpcResponse` with result.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param result result
   * @param toJson converts params to JSON
   */
  def apply[T](version: String, id: Long, result: T)
      (implicit toJson: JsonOutput[T]): JsonRpcResponse =
    apply(version, id, Json.toJson(result))

  /**
   * Creates `JsonRpcResponse` with error.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param error error
   */
  def apply(version: String, id: JsonRpcIdentifier, error: JsonRpcError): JsonRpcResponse = {
    if (version == null) throw new NullPointerException()
    if (id == null) throw new NullPointerException()
    if (error == null) throw new NullPointerException()

    JsonRpcResponseImpl(version, id, JsonRpcResult(error))
  }

  /**
   * Creates `JsonRpcResponse` with error.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param error error
   */
  def apply(version: String, id: String, error: JsonRpcError): JsonRpcResponse = {
    if (version == null) throw new NullPointerException()
    if (error == null) throw new NullPointerException()

    JsonRpcResponseImpl(version, JsonRpcIdentifier(id), JsonRpcResult(error))
  }

  /**
   * Creates `JsonRpcResponse` with error.
   *
   * @param version JSON-RPC version
   * @param id message identifier
   * @param error error
   */
  def apply(version: String, id: Long, error: JsonRpcError): JsonRpcResponse = {
    if (version == null) throw new NullPointerException()
    if (error == null) throw new NullPointerException()

    JsonRpcResponseImpl(version, JsonRpcIdentifier(id), JsonRpcResult(error))
  }
}

private case class JsonRpcResponseImpl(version: String, id: JsonRpcIdentifier, result: JsonRpcResult) extends JsonRpcResponse
