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
import javax.json.stream.JsonParsingException

import little.json.JsonInput
import little.json.Implicits._

class JsonRpcParseResponseSpec extends org.scalatest.flatspec.AnyFlatSpec {
  case class Answer(value: Int)

  implicit val resultInput: JsonInput[Answer] = {
    case result: JsonObject => Answer(result.getInt("answer"))
  }

  it should "parse response with object result" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": "abc",
      "result": { "answer": 3 }
    }"""

    val res = JsonRpc.parseResponse(text)
    assert(res.version == "2.0")
    assert(res.id.stringValue == "abc")
    assert(res.result.as[Answer] == Answer(3))
  }

  it should "parse response with array result" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": "abc",
      "result": [0, 1, 2]
    }"""

    val res = JsonRpc.parseResponse(text)
    assert(res.version == "2.0")
    assert(res.id.stringValue == "abc")
    assert(res.result.as[Seq[Int]] == Seq(0, 1, 2))
  }

  it should "parse response with number result" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": "abc",
      "result": 3
    }"""

    val res = JsonRpc.parseResponse(text)
    assert(res.version == "2.0")
    assert(res.id.stringValue == "abc")
    assert(res.result.as[Int] == 3)
  }

  it should "parse response with string result" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": 123,
      "result": "success"
    }"""

    val res = JsonRpc.parseResponse(text)
    assert(res.version == "2.0")
    assert(res.id.numberValue == 123)
    assert(res.result.as[String] == "success")
  }

  it should "parse response with boolean result" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": 123,
      "result": true
    }"""

    val res = JsonRpc.parseResponse(text)
    assert(res.version == "2.0")
    assert(res.id.numberValue == 123)
    assert(res.result.as[Boolean])
  }

  it should "parse response with error" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": 123,
      "error": { "code": -32603, "message": "Internal Error" }
    }"""

    val res = JsonRpc.parseResponse(text)
    assert(res.version == "2.0")
    assert(res.id.numberValue == 123)
    assert(res.error.isInternalError)
    assert(res.error.message == "Internal Error")
  }

  it should "not parse response as array" in {
    assertThrows[IllegalArgumentException](JsonRpc.parseResponse("[0, 1, 2]"))
  }

  it should "not parse response with invalid JSON" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": 123
      "result": [0, 1, 2]
    }"""
    assertThrows[JsonParsingException](JsonRpc.parseResponse(text))
  }

  it should "not parse response without jsonrpc" in {
    val text = """{
      "id": 123,
      "result": [0, 1, 2]
    }"""
    assertThrows[IllegalArgumentException](JsonRpc.parseResponse(text))
  }

  it should "not parse response with number value for jsonrpc" in {
    val text = """{
      "jsonrpc": 2.0,
      "id": 123,
      "result": [0, 1, 2]
    }"""
    assertThrows[IllegalArgumentException](JsonRpc.parseResponse(text))
  }

  it should "not parse response without result" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": 123
    }"""
    assertThrows[IllegalArgumentException](JsonRpc.parseResponse(text))
  }
}
