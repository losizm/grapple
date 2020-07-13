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

import javax.json.{ JsonNumber, JsonObject, JsonString, JsonStructure, JsonValue }
import javax.json.stream.JsonParsingException

import little.json.Json
import little.json.Implicits._

import scala.util.Try

/** Provides utilities for parsing JSON-RPC messages. */
object JsonRpc {
  /**
   * Parses JSON-RPC request.
   *
   * @param text request
   */
  def parseRequest(text: String): JsonRpcRequest = {
    val json = try parse(text) catch {
      case _: ClassCastException     => throw InvalidRequest("request must be object value")
      case err: JsonParsingException =>
        val location = err.getLocation()
        val message = new StringBuilder()
          .append("Invalid JSON at offset=")
          .append(location.getStreamOffset())
          .append(" (line=")
          .append(location.getLineNumber())
          .append(", column=")
          .append(location.getColumnNumber())
          .append(")")
          .toString()

        throw ParseError(message)
    }

    val builder = JsonRpcRequest.builder()

    json.get("jsonrpc") match {
      case null              => throw InvalidRequest("request must include jsonrpc")
      case value: JsonString => builder.version(value.getString())
      case _: JsonValue      => throw InvalidRequest("jsonrpc must be string value")
    }

    json.get("id") match {
      case null              => builder.idUndefined()
      case JsonValue.NULL    => builder.idNull()
      case value: JsonString => builder.id(value.getString())
      case value: JsonNumber =>
        try builder.id(value.longValueExact())
        catch {
          case _: Exception => throw InvalidRequest("id number must be integer value")
        }
      case _: JsonValue      => throw InvalidRequest("id must be string or number value")
    }

    json.get("method") match {
      case null              => throw InvalidRequest("request must include method")
      case value: JsonString => builder.method(value.getString())
      case _: JsonValue      => throw InvalidRequest("method must be string value")
    }

    json.get("params") match {
      case null                 => builder.params(None)
      case value: JsonStructure => builder.params(value)
      case _: JsonValue         => throw InvalidRequest("params must be array or object value")
    }

    builder.build()
  }

  /**
   * Parses JSON-RPC response.
   *
   * @param text response
   */
  def parseResponse(text: String): JsonRpcResponse = {
    val json = try parse(text) catch {
      case _: ClassCastException => throw new IllegalArgumentException("request must be object value")
    }

    val builder = JsonRpcResponse.builder()

    json.get("jsonrpc") match {
      case null              => throw new IllegalArgumentException("response must include jsonrpc")
      case value: JsonString => builder.version(value.getString())
      case _: JsonValue      => throw new IllegalArgumentException("jsonrpc must be string value")
    }

    json.get("id") match {
      case JsonValue.NULL    => builder.idNull()
      case value: JsonString => builder.id(value.getString())
      case value: JsonNumber =>
        try builder.id(value.longValueExact())
        catch {
          case _: Exception => throw new IllegalArgumentException("id number must be integer value")
        }
      case _: JsonValue      => throw new IllegalArgumentException("id must be string or number value")
    }

    json.get("result") match {
      case null =>
        json.get("error") match {
          case null  => throw new IllegalArgumentException("include must include either result or error")
          case value: JsonObject =>
            val code = value.getInt("code")
            val message = value.getString("message")
            val data = Option(value.get("data"))
            builder.error(code, message, data)
          case _: JsonValue => throw new IllegalArgumentException("error must be array value")
        }
      case value: JsonStructure => builder.result(value)
      case _: JsonValue => throw new IllegalArgumentException("result must be array or object value")
    }

    builder.build()
  }

  private def parse(message: String): JsonObject =
    Json.parse(message).asObject
}
