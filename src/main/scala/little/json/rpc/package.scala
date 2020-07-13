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
package little.json

import javax.json.{ JsonNumber, JsonObject, JsonString, JsonValue }

import Implicits._

/**
 * Defines API for [[https://www.jsonrpc.org/specification JSON-RPC 2.0]].
 *
 * {{{
 * import little.json.{ Json, JsonOutput }
 * import little.json.Implicits._
 * import little.json.rpc._
 *
 * case class Problem(values: Int*)
 * case class Answer(value: Int)
 *
 * // Used when creating "params" in request
 * implicit val problemOutput: JsonOutput[Problem] = {
 *   problem => Json.toJson(problem.values)
 * }
 *
 * // Used when creating "result" in response
 * implicit val answerOutput: JsonOutput[Answer] = {
 *   answer => Json.obj("answer" -> answer.value)
 * }
 *
 * val request = JsonRpcRequest(
 *   version = "2.0",
 *   id = "590d24ae-500a-486c-8d73-8035e78529bd",
 *   method = "sum",
 *   params = Problem(1, 2, 3) // Uses problemOutput
 * )
 *
 * val response = JsonRpcResponse(
 *   version = request.version,
 *   id = request.id,
 *   result = request.method match {
 *     case "sum" =>
 *       // Sets result
 *       request.params
 *         .map(_.as[Array[Int]])
 *         .map(_.sum)
 *         .map(Answer(_))
 *         .map(JsonRpcResult(_)) // Uses answerOutput
 *         .get
 *     case name =>
 *       // Or sets error
 *       JsonRpcResult(MethodNotFound(name))
 *   }
 * )
 * }}}
 */
package object rpc {
  /** Defines adapter for `JsonRpcIdentifier`. */
  implicit val jsonRpcIdentifierAdapter = new JsonAdapter[JsonRpcIdentifier] {
    def reading(json: JsonValue): JsonRpcIdentifier =
      json match {
        case id: JsonString => JsonRpcIdentifier(id.getString)
        case id: JsonNumber => JsonRpcIdentifier(id.longValueExact)
        case _ => throw new IllegalArgumentException("json is not string or number value")
      }

    def writing(id: JsonRpcIdentifier): JsonValue =
      if      (id.isString) Json.toJson(id.stringValue)
      else if (id.isNumber) Json.toJson(id.numberValue)
      else                  JsonValue.NULL
  }

  /** Defines adapter for `JsonRpcError`. */
  implicit val jsonRpcErrorAdapter = new JsonAdapter[JsonRpcError] {
    def reading(json: JsonValue): JsonRpcError =
      json match {
        case error: JsonObject =>
          JsonRpcError(
            error.getInt("code"),
            error.getString("message"),
            Option(error.get("data"))
          )
        case _ => throw new IllegalArgumentException("json is not object value")
      }

    def writing(error: JsonRpcError): JsonValue = {
      val objBuilder = Json.createObjectBuilder()
      objBuilder.add("code", error.code)
      objBuilder.add("message", error.message)
      error.data.foreach(objBuilder.add("data", _))
      objBuilder.build()
    }
  }

  /** Defines adapter for `JsonRpcRequest`. */
  implicit val jsonRpcRequestAdapter: JsonAdapter[JsonRpcRequest] = JsonRpcRequestAdapter

  /** Defines adapter for `JsonRpcResponse`. */
  implicit val jsonRpcResponseAdapter: JsonAdapter[JsonRpcResponse] = JsonRpcResponseAdapter
}
