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

import javax.json.stream.JsonParsingException

import little.json.Json
import little.json.Implicits._

/** Provides utilities for parsing JSON-RPC messages. */
object JsonRpc {
  /**
   * Parses JSON-RPC request.
   *
   * @param text request
   */
  def parseRequest(text: String): JsonRpcRequest =
    try
      Json.parse(text).as[JsonRpcRequest]
    catch {
      case err: JsonParsingException =>
        val location = err.getLocation
        val data = new StringBuilder()
          .append("Invalid JSON at offset=")
          .append(location.getStreamOffset)
          .append(" (line=")
          .append(location.getLineNumber)
          .append(", column=")
          .append(location.getColumnNumber)
          .append(")")
          .toString()
        throw ParseError(data)
    }

  /**
   * Parses JSON-RPC response.
   *
   * @param text response
   */
  def parseResponse(text: String): JsonRpcResponse =
    Json.parse(text).as[JsonRpcResponse]
}
