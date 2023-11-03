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

class JsonValueToJsonValueSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "get list of JSON values" in {
    val obj = Json.obj(
      "values" -> Json.arr(
        JsonString("abc"),
        JsonNumber(123),
        JsonBoolean(true),
        JsonNull
      )
    )

    val list = obj("values").as[List[JsonValue]]
    assert(list(0) == JsonString("abc"))
    assert(list(1) == JsonNumber(123))
    assert(list(2) == JsonBoolean.True)
    assert(list(3) == JsonNull)
  }
