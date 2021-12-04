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

class JsonRpcIdentifierSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "inspect JsonRpcIdentifier with string value" in {
    val id = JsonRpcIdentifier("abc")
    assert(!id.isNull)
    assert(id.isString)
    assert(!id.isNumber)
    assert(id.stringValue == "abc")
    assertThrows[NoSuchElementException](id.numberValue)
    assert(Json.toJson(id) == JsonString("abc"))
    assert(id == JsonString("abc").as[JsonRpcIdentifier])
  }

  it should "inspect JsonRpcIdentifier with number value" in {
    val id = JsonRpcIdentifier(123)
    assert(!id.isNull)
    assert(!id.isString)
    assert(id.isNumber)
    assertThrows[NoSuchElementException](id.stringValue)
    assert(id.numberValue == 123)
    assert(Json.toJson(id) == JsonNumber(123))
    assert(id == JsonNumber(123).as[JsonRpcIdentifier])
  }

  it should "inspect JsonRpcIdentifier with null value" in {
    val id = JsonRpcIdentifier.nullified
    assert(id.isNull)
    assert(!id.isString)
    assert(!id.isNumber)
    assertThrows[NoSuchElementException](id.stringValue)
    assertThrows[NoSuchElementException](id.numberValue)
    assert(Json.toJson(id) == JsonNull)
    assert(id == JsonNull.as[JsonRpcIdentifier])
  }
