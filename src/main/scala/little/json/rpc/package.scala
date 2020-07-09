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
package object rpc
