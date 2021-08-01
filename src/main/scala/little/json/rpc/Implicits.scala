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

import scala.language.implicitConversions

/** Provides extension methods and implicit conversions. */
object Implicits:
  import little.json.Implicits.{ *, given }

  /** Converts `JsonValue` to `JsonRpcError`. */
  given jsonValueToJsonRpcError: JsonInput[JsonRpcError] with
    def apply(json: JsonValue): JsonRpcError =
      if !json.isInstanceOf[JsonObject] then
        throw JsonException("object value expected")
      JsonRpcError(json("code"), json("message"), json.get("data"))

  /** Converts `JsonRpcError` to `JsonValue`. */
  given jsonRpcErrorToJsonValue: JsonOutput[JsonRpcError] with
    def apply(error: JsonRpcError): JsonValue =
      val builder = JsonObjectBuilder()
      builder.add("code", error.code)
      builder.add("message", error.message)
      error.data.foreach(builder.add("data", _))
      builder.build()

  /** Converts `JsonValue` to `JsonRpcIdentifier`. */
  given jsonValueToJsonRpcIdentifier: JsonInput[JsonRpcIdentifier] with
    def apply(json: JsonValue): JsonRpcIdentifier =
      json match
        case id: JsonString => JsonRpcIdentifier(id.value)
        case id: JsonNumber => JsonRpcIdentifier(id.longValue)
        case JsonNull       => JsonRpcIdentifier.nullValue
        case _              => throw JsonException("string, number, or null value expected")

  /** Converts `JsonRpcIdentifier` to `JsonValue`. */
  given jsonRpcIdentifierToJsonValue: JsonOutput[JsonRpcIdentifier] with
    def apply(id: JsonRpcIdentifier): JsonValue =
      if      id.isString then JsonString(id.stringValue)
      else if id.isNumber then JsonNumber(id.numberValue)
      else                     JsonNull

  /** Converts `JsonValue` to `JsonRpcRequest`. */
  given jsonValueToJsonRpcRequest: JsonInput[JsonRpcRequest] with
    def apply(json: JsonValue): JsonRpcRequest =
      if !json.isInstanceOf[JsonObject] then
        throw InvalidRequest("object value expected")

      val builder = JsonRpcRequest.builder()

      json.get("jsonrpc") match
        case Some(s: JsonString) => builder.version(s)
        case Some(_: JsonValue)  => throw InvalidRequest("string value expected for jsonrpc")
        case None                => throw InvalidRequest("jsonrpc required")

      json.get("id") match
        case Some(s: JsonString)  => builder.id(s.value)
        case Some(n: JsonNumber)  => builder.id(getLongIdentifier(n))
        case Some(JsonNull)       => builder.idNull()
        case None                 => builder.idUndefined()
        case Some(_: JsonValue)   => throw InvalidRequest("string or number value expected for id")

      json.get("method") match
        case Some(s: JsonString) => builder.method(s)
        case Some(_: JsonValue)  => throw InvalidRequest("string value expected for method")
        case None                => throw InvalidRequest("method required")

      json.get("params") match
        case Some(s: JsonStructure) => builder.params(s)
        case None                   => builder.params(None)
        case Some(_: JsonValue)     => throw InvalidRequest("array or object value expected for params")

      builder.build()

    private def getLongIdentifier(n: JsonNumber) =
      try n.longValue
      catch case _: ArithmeticException =>
        throw InvalidRequest("integer value expected for id")

  /** Converts `JsonRpcRequest` to `JsonValue`. */
  given jsonRpcRequestToJsonValue: JsonOutput[JsonRpcRequest] with
    def apply(request: JsonRpcRequest): JsonValue =
      val builder = JsonObjectBuilder()
      builder.add("version", request.version)

      if !request.isNotification then
        builder.add("id", request.id)

      builder.add("method", request.method)
      request.params.foreach(builder.add("params", _))

      builder.build()

  /** Converts `JsonValue` to `JsonRpcResponse`. */
  given jsonValueToJsonRpcResponse: JsonInput[JsonRpcResponse] with
    def apply(json: JsonValue): JsonRpcResponse =
      if !json.isInstanceOf[JsonObject] then
        throw JsonException("object value expected")

      val builder = JsonRpcResponse.builder()

      json.get("jsonrpc") match
        case Some(s: JsonString) => builder.version(s)
        case Some(_: JsonValue)  => throw JsonException("string value expected for jsonrpc")
        case None                => throw JsonException("jsonrpc required")

      json.get("id") match
        case Some(s: JsonString)  => builder.id(s.value)
        case Some(n: JsonNumber)  => builder.id(getLongIdentifier(n))
        case Some(JsonNull)       => builder.idNull()
        case Some(_: JsonValue)   => throw JsonException("string or number value expected for id")
        case None                 => throw JsonException("id required")

      json.get("result") match
        case Some(value) => builder.result(value)
        case None        =>
          json.get("error") match
            case Some(o: JsonObject) => builder.error(o)
            case Some(_: JsonValue)  => throw JsonException("object value expected for error")
            case None                => throw JsonException("include must include either result or error")

      builder.build()

    private def getLongIdentifier(n: JsonNumber) =
      try n.longValue
      catch case _: ArithmeticException =>
        throw JsonException("integer value expected for id")

  /** Converts `JsonRpcResponse` to `JsonValue`. */
  given jsonRpcResponseToJsonValue: JsonOutput[JsonRpcResponse] with
    def apply(response: JsonRpcResponse): JsonValue =
      val builder = JsonObjectBuilder()
      builder.add("version", response.version)
      builder.add("id", response.id)

      response.isResult match
        case true  => builder.add("result", response.result)
        case false => builder.add("error", response.error)

      builder.build()

  /** Provides passthrough for `JsonRpcError` or returns `InternalError`. */
  given defaultOnFailure: PartialFunction[Throwable, JsonRpcError] = {
    case err: JsonRpcError => err
    case _                 => InternalError()
  }
