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

import javax.json.{ JsonNumber, JsonObject, JsonString, JsonValue }

import little.json.{ Json, JsonAdapter }
import little.json.Implicits._

private object JsonRpcResponseAdapter extends JsonAdapter[JsonRpcResponse] {
  def reading(json: JsonValue): JsonRpcResponse = {
    val obj = try json.asObject catch {
      case _: ClassCastException => throw new IllegalArgumentException("response must be object value")
    }

    val builder = JsonRpcResponse.builder()

    obj.get("jsonrpc") match {
      case null          => throw new IllegalArgumentException("response must include jsonrpc")
      case s: JsonString => builder.version(s.getString)
      case _: JsonValue  => throw new IllegalArgumentException("jsonrpc must be string value")
    }

    obj.get("id") match {
      case null           => new IllegalArgumentException("id required for response")
      case JsonValue.NULL => builder.idNull()
      case s: JsonString  => builder.id(s.getString)
      case n: JsonNumber  =>
        n.isIntegral match {
          case true  => builder.id(n.longValueExact())
          case false => throw new IllegalArgumentException("id number must be integer value")
        }
      case _: JsonValue   => throw new IllegalArgumentException("id must be string or number value")
    }

    obj.get("result") match {
      case null =>
        obj.get("error") match {
          case null          => throw new IllegalArgumentException("include must include either result or error")
          case error: JsonObject =>
            val code    = error.getInt("code")
            val message = error.getString("message")
            val data    = Option(error.get("data"))
            builder.error(code, message, data)
          case _: JsonValue  => throw new IllegalArgumentException("error must be object value")
        }
      case value: JsonValue => builder.result(value)
    }

    builder.build()
  }

  def writing(response: JsonRpcResponse): JsonValue = {
    val builder = Json.createObjectBuilder()
    builder.add("version", response.version)
    builder.add("id", response.id)

    response.isResult match {
      case true  => builder.add("result", response.result)
      case false => builder.add("error", response.error)
    }

    builder.build()
  }
}
