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

class JsonStringSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "create JsonString" in {
    val s = JsonString("Hello, world!")
    assert(s.value == "Hello, world!")
    assert(s.toString == "\"Hello, world!\"")
    assert(s.as[String] == "Hello, world!")
  }

  it should "compare JsonStrings" in {
    assert(JsonString("abc") == JsonString("abc"))
    assert(JsonString("abc") != JsonString("xyz"))
  }

  it should "destructure JsonString" in {
    val s = JsonString("abc")

    assert {
      s match
        case JsonString("xyz") => false
        case JsonString(value) => value == "abc"
        case _                 => false
    }

    assert {
      s match
        case JsonString("xyz") => false
        case JsonString("abc") => true
        case _                 => false
    }
  }

  it should "not create JsonString with null value" in {
    assertThrows[NullPointerException](JsonString(null))
  }
