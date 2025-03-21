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

class JsonBooleanSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "create JsonBoolean" in {
    val b1 = JsonBoolean(true)
    assert(b1.value)
    assert(b1.toString == "true")
    assertThrows[JsonExpectationError](b1.as[String])
    assertThrows[JsonExpectationError](b1.as[Int])
    assertThrows[JsonExpectationError](b1.as[Long])
    assertThrows[JsonExpectationError](b1.as[Double])
    assertThrows[JsonExpectationError](b1.as[BigDecimal])
    assert(b1.as[Boolean])
    assert(b1 == JsonBoolean.True)

    val b2 = JsonBoolean(false)
    assert(!b2.value)
    assert(b2.toString == "false")
    assertThrows[JsonExpectationError](b2.as[String])
    assertThrows[JsonExpectationError](b2.as[Int])
    assertThrows[JsonExpectationError](b2.as[Long])
    assertThrows[JsonExpectationError](b2.as[Double])
    assertThrows[JsonExpectationError](b2.as[BigDecimal])
    assert(!b2.as[Boolean])
    assert(b2 == JsonBoolean.False)
  }

  it should "compare JsonBooleans" in {
    assert(JsonBoolean(true) == JsonBoolean(true))
    assert(JsonBoolean(true) != JsonBoolean(false))
    assert(JsonBoolean(false) == JsonBoolean(false))
    assert(JsonBoolean.True != JsonBoolean.False)
  }

  it should "deconstruct JsonBoolean" in {
    val b1 = JsonBoolean(true)
    assert {
      b1 match
        case JsonBoolean(false) => false
        case JsonBoolean(value) => value
        case _                  => false
    }

    assert {
      b1 match
        case JsonBoolean(false) => false
        case JsonBoolean(true)  => true
        case _                  => false
    }

    val b2 = JsonBoolean(false)
    assert {
      b2 match
        case JsonBoolean(true)  => false
        case JsonBoolean(value) => !value
        case _                  => false
    }

    assert {
      b2 match
        case JsonBoolean(true)  => false
        case JsonBoolean(false) => true
        case _                  => false
    }
  }
