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

import javax.json.JsonObject

import little.json.{ Json, JsonInput, JsonOutput }
import little.json.Implicits._

import org.scalatest.FlatSpec

class JsonRpcRequestSpec extends FlatSpec {
  case class Param(name: String, value: Int)

  implicit val paramInput: JsonInput[Param] = {
    case value: JsonObject => Param(
      value.getString("name"),
      value.getInt("value")
    )
  }

  implicit val paramOutput: JsonOutput[Param] =
    param => Json.obj(
      "name"  -> param.name,
      "value" -> param.value
    )

  it should "create JsonRpcRequest" in {
    val req1 = JsonRpcRequest("2.0", "abc", "compute")
    assert(req1.version == "2.0")
    assert(!req1.isNotification)
    assert(req1.id == JsonRpcIdentifier("abc"))
    assert(req1.id.stringValue == "abc")
    assert(req1.method == "compute")
    assert(req1.params.isEmpty)

    val req2 = JsonRpcRequest("2.0", 123, "compute", Param("x", 1))
    assert(req2.version == "2.0")
    assert(!req2.isNotification)
    assert(req2.id == JsonRpcIdentifier(123))
    assert(req2.id.numberValue == 123)
    assert(req2.method == "compute")
    assert(req2.params.map(_.as[Param]).contains(Param("x", 1)))

    val req3 = JsonRpcRequest("2.0", JsonRpcIdentifier.undefined, "compute", Param("x", 1))
    assert(req3.version == "2.0")
    assert(req3.isNotification)
    assert(req3.id == JsonRpcIdentifier.undefined)
    assert(req3.id.isUndefined)
    assert(req3.method == "compute")
    assert(req3.params.map(_.as[Param]).contains(Param("x", 1)))
  }
}
