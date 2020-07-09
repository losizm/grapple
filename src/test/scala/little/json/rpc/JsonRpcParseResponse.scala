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

import little.json.Implicits._

import org.scalatest.FlatSpec

class JsonRpcParseResponseSpec extends FlatSpec {
  it should "parse response with result" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": "abc",
      "result": [0, 1, 2]
    }"""

    val res = JsonRpc.toResponse(text)
    assert(res.version == "2.0")
    assert(res.id.stringValue == "abc")
    assert(res.result.get.as[Seq[Int]] == Seq(0, 1, 2))
  }

  it should "parse response with error" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": 123,
      "error": { "code": -32603, "message": "Internal Error" }
    }"""

    val res = JsonRpc.toResponse(text)
    assert(res.version == "2.0")
    assert(res.id.numberValue == 123)
    assert(res.result.error.isInternalError)
    assert(res.result.error.message == "Internal Error")
  }

  it should "not parse response as array" in {
    assertThrows[IllegalArgumentException](JsonRpc.toResponse("[0, 1, 2]"))
  }

  it should "not parse response with invalid JSON" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": 123
      "result": [0, 1, 2]
    }"""
    assertThrows[JsonParsingException](JsonRpc.toResponse(text))
  }

  it should "not parse response without jsonrpc" in {
    val text = """{
      "id": 123,
      "result": [0, 1, 2]
    }"""
    assertThrows[IllegalArgumentException](JsonRpc.toResponse(text))
  }

  it should "not parse response with number value for jsonrpc" in {
    val text = """{
      "jsonrpc": 2.0,
      "id": 123,
      "result": [0, 1, 2]
    }"""
    assertThrows[IllegalArgumentException](JsonRpc.toResponse(text))
  }

  it should "not parse response without result" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": 123
    }"""
    assertThrows[IllegalArgumentException](JsonRpc.toResponse(text))
  }

  it should "not parse response with string value for result" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": 123,
      "result": "xyz"
    }"""
    assertThrows[IllegalArgumentException](JsonRpc.toResponse(text))
  }
}
