/*
 * Copyright 2021 Carlos Conyers
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
package grapple.json
package rpc

import scala.language.implicitConversions

class JsonRpcRequestLoadSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "load request without params" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": "abc",
      "method": "compute"
    }"""

    val req = JsonRpcRequest.load(text)
    assert(req.version == "2.0")
    assert(req.id.string == "abc")
    assert(req.method == "compute")
    assert(req.params.isEmpty)
  }

  it should "load request with params" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": 123,
      "method": "compute",
      "params": { "a": 1, "b": 2 }
    }"""

    val req = JsonRpcRequest.load(text)
    assert(req.version == "2.0")
    assert(req.id.number == 123)
    assert(req.method == "compute")
    assert(
      req.params.exists {
        case params: JsonObject => params("a").as[Int] == 1 && params("b").as[Int] == 2
        case _                  => throw IllegalArgumentException("Expected JSON object")
      }
    )
  }

  it should "not load request as array" in {
    assertThrows[InvalidRequest](JsonRpcRequest.load("[0, 1, 2]"))
  }

  it should "not load request with JSON exception" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": 123
      "method": "compute",
      "params": { "a": 1, "b": 2 }
    }"""
    assertThrows[JsonException](JsonRpcRequest.load(text))
  }

  it should "not load request without jsonrpc" in {
    val text = """{
      "id": 123,
      "method": "compute",
      "params": { "a": 1, "b": 2 }
    }"""
    assertThrows[InvalidRequest](JsonRpcRequest.load(text))
  }

  it should "not load request with number value for jsonrpc" in {
    val text = """{
      "jsonrpc": 2.0,
      "id": 123,
      "method": "compute",
      "params": { "a": 1, "b": 2 }
    }"""
    assertThrows[InvalidRequest](JsonRpcRequest.load(text))
  }

  it should "not load request without method" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": 123,
      "params": { "a": 1, "b": 2 }
    }"""
    assertThrows[InvalidRequest](JsonRpcRequest.load(text))
  }

  it should "not load request with array value for method" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": 123,
      "method": [],
      "params": { "a": 1, "b": 2 }
    }"""
    assertThrows[InvalidRequest](JsonRpcRequest.load(text))
  }

  it should "not load request with string value for params" in {
    val text = """{
      "jsonrpc": "2.0",
      "id": 123,
      "method": "compute",
      "params": "a"
    }"""
    assertThrows[InvalidRequest](JsonRpcRequest.load(text))
  }
