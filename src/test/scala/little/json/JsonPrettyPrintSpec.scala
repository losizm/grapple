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
package little.json

class JsonPrettyPrintSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "pretty print JSON object" in {
    val json = Json.parse("""{"a": "test", "b": 1, "c": true, "d": {"foo": "One", "bar": 2}, "e": ["baz", "quux"], "f": null}""")
    val text = Json.toPrettyPrint(json, "    ")

    assert(text == """{
                      |    "a": "test",
                      |    "b": 1,
                      |    "c": true,
                      |    "d": {
                      |        "foo": "One",
                      |        "bar": 2
                      |    },
                      |    "e": [
                      |        "baz",
                      |        "quux"
                      |    ],
                      |    "f": null
                      |}""".stripMargin)
  }

  it should "pretty print JSON array" in {
    val json = Json.parse("""["test", 1, true, {"foo": "One", "bar": 2}, ["baz", "quux"], null]""")
    val text = Json.toPrettyPrint(json, "    ")

    assert(text == """[
                      |    "test",
                      |    1,
                      |    true,
                      |    {
                      |        "foo": "One",
                      |        "bar": 2
                      |    },
                      |    [
                      |        "baz",
                      |        "quux"
                      |    ],
                      |    null
                      |]""".stripMargin)
  }
