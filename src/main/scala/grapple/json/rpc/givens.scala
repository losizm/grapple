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

/** Converts `JsonValue` to `JsonRpcError`. */
given jsonRpcErrorJsonInput: JsonInput[JsonRpcError] = JsonRpcErrorJsonInput

/** Converts `JsonRpcError` to `JsonValue`. */
given jsonRpcErrorJsonOutput: JsonOutput[JsonRpcError] = JsonRpcErrorJsonOutput

/** Converts `JsonValue` to `JsonRpcIdentifier`. */
given jsonRpcIdentifierJsonInput: JsonInput[JsonRpcIdentifier] = JsonRpcIdentifierJsonInput

/** Converts `JsonRpcIdentifier` to `JsonValue`. */
given jsonRpcIdentifierJsonOutput: JsonOutput[JsonRpcIdentifier] = JsonRpcIdentifierJsonOutput

/** Converts `JsonValue` to `JsonRpcRequest`. */
given jsonRpcRequestJsonInput: JsonInput[JsonRpcRequest] = JsonRpcRequestJsonInput

/** Converts `JsonRpcRequest` to `JsonValue`. */
given jsonRpcRequestJsonOutput: JsonOutput[JsonRpcRequest] = JsonRpcRequestJsonOutput

/** Converts `JsonValue` to `JsonRpcResponse`. */
given jsonRpcResponseJsonInput: JsonInput[JsonRpcResponse] = JsonRpcResponseJsonInput

/** Converts `JsonRpcResponse` to `JsonValue`. */
given jsonRpcResponseJsonOutput: JsonOutput[JsonRpcResponse] = JsonRpcResponseJsonOutput

/**
 * Provides passthrough for `JsonRpcError` or returns `InternalError`.
 *
 * @see [[JsonRpcResponseBuilder.tryResult]]
 */
given toJsonRpcError: PartialFunction[Throwable, JsonRpcError] =
  case err: JsonRpcError => err
  case _                 => InternalError()

private object JsonRpcErrorJsonInput extends JsonInput[JsonRpcError]:
  def read(json: JsonValue): JsonRpcError =
    json match
      case json: JsonObject =>
        JsonRpcError(
          json.getInt("code"),
          json.getString("message"),
          json.get("data")
        )

      case _ =>
        throw JsonException("object value expected")

private object JsonRpcErrorJsonOutput extends JsonOutput[JsonRpcError]:
  def write(error: JsonRpcError): JsonValue =
    val builder = JsonObjectBuilder()
    builder.add("code", error.code)
    builder.add("message", error.message)
    error.data.foreach(builder.add("data", _))
    builder.toJsonObject()

private object JsonRpcIdentifierJsonInput extends JsonInput[JsonRpcIdentifier]:
  def read(json: JsonValue): JsonRpcIdentifier =
    json match
      case id: JsonString => JsonRpcIdentifier(id.value)
      case id: JsonNumber => JsonRpcIdentifier(id.toLong)
      case JsonNull       => JsonRpcIdentifier.nullified
      case _              => throw JsonException("string, number, or null value expected")

private object JsonRpcIdentifierJsonOutput extends JsonOutput[JsonRpcIdentifier]:
  def write(id: JsonRpcIdentifier): JsonValue =
    if      id.isString then JsonString(id.string)
    else if id.isNumber then JsonNumber(id.number)
    else                     JsonNull

private object JsonRpcRequestJsonInput extends JsonInput[JsonRpcRequest]:
  def read(json: JsonValue): JsonRpcRequest =
    json match
      case json: JsonObject => toRequest(json)
      case _                => throw InvalidRequest("object value expected")

  private def toRequest(json: JsonObject) =
    val builder = JsonRpcRequestBuilder()

    json.get("jsonrpc") match
      case Some(s: JsonString) => builder.version(s.value)
      case Some(_: JsonValue)  => throw InvalidRequest("string value expected for jsonrpc")
      case None                => throw InvalidRequest("jsonrpc required")

    json.get("id") match
      case Some(s: JsonString)  => builder.id(s.value)
      case Some(n: JsonNumber)  => builder.id(getLongIdentifier(n))
      case Some(JsonNull)       => builder.idNull()
      case None                 => builder.idUndefined()
      case Some(_: JsonValue)   => throw InvalidRequest("string or number value expected for id")

    json.get("method") match
      case Some(s: JsonString) => builder.method(s.value)
      case Some(_: JsonValue)  => throw InvalidRequest("string value expected for method")
      case None                => throw InvalidRequest("method required")

    json.get("params") match
      case Some(s: JsonStructure) => builder.params(s)
      case None                   => builder.params(None)
      case Some(_: JsonValue)     => throw InvalidRequest("array or object value expected for params")

    builder.toJsonRpcRequest()

  private def getLongIdentifier(n: JsonNumber) =
    try n.toLong
    catch case _: ArithmeticException =>
      throw InvalidRequest("integer value expected for id")

private object JsonRpcRequestJsonOutput extends JsonOutput[JsonRpcRequest]:
  def write(request: JsonRpcRequest): JsonValue =
    val builder = JsonObjectBuilder()
    builder.add("version", request.version)

    if !request.isNotification then
      builder.add("id", Json.toJson(request.id))

    builder.add("method", request.method)
    request.params.foreach(builder.add("params", _))

    builder.toJsonObject()

private object JsonRpcResponseJsonInput extends JsonInput[JsonRpcResponse]:
  def read(json: JsonValue): JsonRpcResponse =
    json match
      case json: JsonObject => toResponse(json)
      case _                => throw JsonException("object value expected")

  private def toResponse(json: JsonObject) =
    val builder = JsonRpcResponseBuilder()

    json.get("jsonrpc") match
      case Some(s: JsonString) => builder.version(s.value)
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
          case Some(o: JsonObject) => builder.error(o.as[JsonRpcError])
          case Some(_: JsonValue)  => throw JsonException("object value expected for error")
          case None                => throw JsonException("include must include either result or error")

    builder.toJsonRpcResponse()

  private def getLongIdentifier(n: JsonNumber) =
    try n.toLong
    catch case _: ArithmeticException =>
      throw JsonException("integer value expected for id")

private object JsonRpcResponseJsonOutput extends JsonOutput[JsonRpcResponse]:
  def write(response: JsonRpcResponse): JsonValue =
    val builder = JsonObjectBuilder()
    builder.add("version", response.version)
    builder.add("id", Json.toJson(response.id))

    response.isResult match
      case true  => builder.add("result", response.result)
      case false => builder.add("error", Json.toJson(response.error))

    builder.toJsonObject()
