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

import javax.json.{ JsonNumber, JsonString, JsonStructure, JsonValue }

import little.json.{ Json, JsonAdapter }
import little.json.Implicits._

private object JsonRpcRequestAdapter extends JsonAdapter[JsonRpcRequest] {
  def reading(json: JsonValue): JsonRpcRequest = {
    val obj = try json.asObject catch {
      case _: ClassCastException => throw InvalidRequest("request must be object value")
    }

    val builder = JsonRpcRequest.builder()

    obj.get("jsonrpc") match {
      case null          => throw InvalidRequest("Invalid Request", "request must include jsonrpc")
      case s: JsonString => builder.version(s.getString)
      case _: JsonValue  => throw InvalidRequest("Invalid Request", "jsonrpc must be string value")
    }

    obj.get("id") match {
      case null           => builder.idUndefined()
      case JsonValue.NULL => builder.idNull()
      case s: JsonString  => builder.id(s.getString)
      case n: JsonNumber  =>
        n.isIntegral match {
          case true  => builder.id(n.longValueExact)
          case false => throw InvalidRequest("Invalid Request", "id number must be integer value")
        }
      case _: JsonValue   => throw InvalidRequest("Invalid Request", "id must be string or number value")
    }

    json.get("method") match {
      case null          => throw InvalidRequest("Invalid Request", "request must include method")
      case s: JsonString => builder.method(s.getString)
      case _: JsonValue  => throw InvalidRequest("Invalid Request", "method must be string value")
    }

    obj.get("params") match {
      case null             => builder.params(None)
      case s: JsonStructure => builder.params(s)
      case _: JsonValue     => throw InvalidRequest("Invalid Request", "params must be array or object value")
    }

    builder.build()
  }

  def writing(request: JsonRpcRequest): JsonValue = {
    val builder = Json.createObjectBuilder()
    builder.add("version", request.version)

    if (!request.isNotification)
      builder.add("id", request.id)

    builder.add("method", request.method)
    request.params.foreach(builder.add("params", _))
    builder.build()
  }
}
