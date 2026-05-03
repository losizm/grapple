/*
 * Copyright 2026 Carlos Conyers
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

class JsonParseAnySpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "parse null" in {
    Json.parseAny("null") == JsonNull
  }

  it should "parse boolean" in {
    Json.parseAny("true") == JsonTrue
    Json.parseAny("false") == JsonFalse
  }

  it should "parse number" in {
    Json.parseAny("1") == JsonNumber(1)
    Json.parseAny("123.456") == JsonNumber(123.456)
  }

  it should "parse string" in {
    Json.parseAny("\"Hello, world!!!\"") == JsonString("Hello, World!!!")
  }

  it should "parse array" in {
    Json.parseAny("""[1000, "lupita"]""") == Json.arr(1000, "lupita")
  }

  it should "parse object" in {
    Json.parseAny("""{ "id": 1000, "name": "lupita" }""") == Json.obj("id" -> 1000, "name" -> "lupita")
  }
