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

import java.util.Arrays

class JsonBytesSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "get UTF-8 encoded bytes for JSON object" in {
    val json  = Json.parse("""{"a": "test", "b": 1, "c": true, "d": {"foo": "One", "bar": 2}, "e": ["baz", "quux"], "f": null}""")
    val bytes = Json.toBytes(json)
    assert(Arrays.equals(bytes, json.toString.getBytes("utf-8")))
  }

  it should "get UTF-8 encoded bytes for JSON array" in {
    val json = Json.parse("""["test", 1, true, {"foo": "One", "bar": 2}, ["baz", "quux"], null]""")
    val bytes = Json.toBytes(json)
    assert(Arrays.equals(bytes, json.toString.getBytes("utf-8")))
  }

  it should "get UTF-8 encoded bytes for other JSON values" in {
    assert(Json.toBytes(JsonString("Hello, world!")) sameElements "\"Hello, world!\"".getBytes("UTF-8"))
    assert(Json.toBytes(JsonNumber(123.456)) sameElements "123.456".getBytes("UTF-8"))
    assert(Json.toBytes(JsonTrue) sameElements "true".getBytes("UTF-8"))
    assert(Json.toBytes(JsonFalse) sameElements "false".getBytes("UTF-8"))
    assert(Json.toBytes(JsonNull) sameElements "null".getBytes("UTF-8"))
  }
