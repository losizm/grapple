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

class JsonRpcResultSpec extends FlatSpec {
  case class Result(name: String, value: Int)

  implicit val resultInput: JsonInput[Result] = {
    case value: JsonObject => Result(
      value.getString("name"),
      value.getInt("value")
    )
  }

  implicit val resultOutput: JsonOutput[Result] =
    param => Json.obj(
      "name"  -> param.name,
      "value" -> param.value
    )

  it should "create JsonRpcResult with value" in {
    val result = JsonRpcResult(Result("x", 1))
    assert(result.get.as[Result] == Result("x", 1))
    assert(!result.isError)
    assertThrows[NoSuchElementException](result.error)
  }

  it should "create JsonRpcResult with error" in {
    val result = JsonRpcResult(ParseError("Parse error"))
    assertThrows[NoSuchElementException](result.get)
    assert(result.isError)
    assert(result.error.isParseError)
    assert(result.error.message == "Parse error")
  }
}
