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

import java.io.{ File, FileInputStream, FileReader }
import java.nio.file.Paths

import scala.language.implicitConversions

class JsonReaderSpec extends org.scalatest.flatspec.AnyFlatSpec:
  private val textObject = """{ "id": 1000, "name": "lupita", "groups": ["lupita", "admin", "sudoer"] }"""
  private val textArray  = """[1000, "lupita", ["lupita", "admin", "sudoer"]]"""

  it should "parse JSON text" in {
    val obj = Json.parse(textObject)
    assert(obj.keys == Set("id", "name", "groups"))
    assert((obj \ "id")     == JsonNumber(1000))
    assert((obj \ "name")   == JsonString("lupita"))
    assert((obj \ "groups") == JsonArray(Seq("lupita", "admin", "sudoer")))

    val arr = Json.parse(textArray)
    assert(arr.size == 3)
    assert((arr \ 0) == JsonNumber(1000))
    assert((arr \ 1) == JsonString("lupita"))
    assert((arr \ 2) == JsonArray(Seq("lupita", "admin", "sudoer")))

    assert(Json.parse("{}").isEmpty)
    assert(Json.parse("[]").isEmpty)
  }

  it should "parse JSON bytes" in {
    val obj = Json.parse(textObject.getBytes())
    assert(obj.keys == Set("id", "name", "groups"))
    assert((obj \ "id")     == JsonNumber(1000))
    assert((obj \ "name")   == JsonString("lupita"))
    assert((obj \ "groups") == JsonArray(Seq("lupita", "admin", "sudoer")))

    val arr = Json.parse(textArray.getBytes())
    assert(arr.size == 3)
    assert((arr \ 0) == JsonNumber(1000))
    assert((arr \ 1) == JsonString("lupita"))
    assert((arr \ 2) == JsonArray(Seq("lupita", "admin", "sudoer")))

    assert(Json.parse("{}").isEmpty)
    assert(Json.parse("[]").isEmpty)
  }

  it should "parse JSON file" in {
    val json1 = Json.parse(File("./src/test/resources/users.json"))
    assert(json1.keys == Set("root", "guest", "lupita"))

    assert((json1 \ "root" \ "id")     == JsonNumber(0))
    assert((json1 \ "root" \ "name")   == JsonString("root"))
    assert((json1 \ "root" \ "groups") == JsonArray(Seq("root")))

    assert((json1 \ "guest" \ "id")     == JsonNumber(500))
    assert((json1 \ "guest" \ "name")   == JsonString("guest"))
    assert((json1 \ "guest" \ "groups") == JsonArray(Seq("guest")))

    assert((json1 \ "lupita" \ "id")     == JsonNumber(1000))
    assert((json1 \ "lupita" \ "name")   == JsonString("lupita"))
    assert((json1 \ "lupita" \ "groups") == JsonArray(Seq("lupita", "admin", "sudoer")))

    val json2 = Json.parse(Paths.get("./src/test/resources/users.json"))
    assert(json2.keys == Set("root", "guest", "lupita"))

    assert((json2 \ "root" \ "id")     == JsonNumber(0))
    assert((json2 \ "root" \ "name")   == JsonString("root"))
    assert((json2 \ "root" \ "groups") == JsonArray(Seq("root")))

    assert((json2 \ "guest" \ "id")     == JsonNumber(500))
    assert((json2 \ "guest" \ "name")   == JsonString("guest"))
    assert((json2 \ "guest" \ "groups") == JsonArray(Seq("guest")))

    assert((json2 \ "lupita" \ "id")     == JsonNumber(1000))
    assert((json2 \ "lupita" \ "name")   == JsonString("lupita"))
    assert((json2 \ "lupita" \ "groups") == JsonArray(Seq("lupita", "admin", "sudoer")))
  }

  it should "parse JSON input stream" in {
    val json1 = withResource(FileInputStream("./src/test/resources/users.json")) { Json.parse(_) }
    assert(json1.keys == Set("root", "guest", "lupita"))

    assert((json1 \ "root" \ "id")     == JsonNumber(0))
    assert((json1 \ "root" \ "name")   == JsonString("root"))
    assert((json1 \ "root" \ "groups") == JsonArray(Seq("root")))

    assert((json1 \ "guest" \ "id")     == JsonNumber(500))
    assert((json1 \ "guest" \ "name")   == JsonString("guest"))
    assert((json1 \ "guest" \ "groups") == JsonArray(Seq("guest")))

    assert((json1 \ "lupita" \ "id")     == JsonNumber(1000))
    assert((json1 \ "lupita" \ "name")   == JsonString("lupita"))
    assert((json1 \ "lupita" \ "groups") == JsonArray(Seq("lupita", "admin", "sudoer")))

    val json2 = withResource(FileReader("./src/test/resources/users.json")) { Json.parse(_) }
    assert(json2.keys == Set("root", "guest", "lupita"))

    assert((json2 \ "root" \ "id")     == JsonNumber(0))
    assert((json2 \ "root" \ "name")   == JsonString("root"))
    assert((json2 \ "root" \ "groups") == JsonArray(Seq("root")))

    assert((json2 \ "guest" \ "id")     == JsonNumber(500))
    assert((json2 \ "guest" \ "name")   == JsonString("guest"))
    assert((json2 \ "guest" \ "groups") == JsonArray(Seq("guest")))

    assert((json2 \ "lupita" \ "id")     == JsonNumber(1000))
    assert((json2 \ "lupita" \ "name")   == JsonString("lupita"))
    assert((json2 \ "lupita" \ "groups") == JsonArray(Seq("lupita", "admin", "sudoer")))
  }

  it should "not parse invalid JSON input" in {
    assertThrows[JsonException](Json.parse("""{"""))
    assertThrows[JsonException](Json.parse("""}"""))
    assertThrows[JsonParserError](Json.parse("""{,}"""))
    assertThrows[JsonParserError](Json.parse("""{"id"}"""))
    assertThrows[JsonParserError](Json.parse("""{"id",}"""))
    assertThrows[JsonParserError](Json.parse("""{"id":}"""))
    assertThrows[JsonParserError](Json.parse("""{"id":,}"""))
    assertThrows[JsonException](Json.parse("["))
    assertThrows[JsonParserError](Json.parse("]"))
    assertThrows[JsonParserError](Json.parse("[,]"))
    assertThrows[JsonParserError](Json.parse("""["id":]"""))

    val err1 = intercept[JsonParserError](Json.parse("""{ "a: 1, "b": 2, "c": 3 }"""))
    assert(err1.offset == 10)

    val err2 = intercept[JsonParserError](Json.parse("""[1, "b": 2, 3]"""))
    assert(err2.offset == 7)

    val err3 = intercept[JsonParserError](Json.parse("""{ "a": 1, "b": 2, "c": trxe }"""))
    assert(err3.offset == 25)

    val err4 = intercept[JsonParserError](Json.parse("""[1, falsey, 3]"""))
    assert(err4.offset == 9)
  }

  it should "read objects and arrays" in {
    val reader = JsonReader("""
      { "id": 0, "name": "root" }
      {
        "id": 1000,
        "name": "lupita"
      }
      [{ "id": 0, "name": "root" }, { "id": 1000, "name": "lupita" }]
      [
        { "id": 0, "name": "root" },
        { "id": 1000, "name": "lupita" }
      ]
    """)

    val obj1 = reader.read()
    assert(obj1 \ "id" == JsonNumber(0))
    assert(obj1 \ "name" == JsonString("root"))

    val obj2 = reader.read()
    assert(obj2 \ "id" == JsonNumber(1000))
    assert(obj2 \ "name" == JsonString("lupita"))

    val arr1 = reader.read()
    assert(arr1 \ 0 \ "id" == JsonNumber(0))
    assert(arr1 \ 0 \ "name" == JsonString("root"))
    assert(arr1 \ 1 \ "id" == JsonNumber(1000))
    assert(arr1 \ 1 \ "name" == JsonString("lupita"))

    val arr2 = reader.read()
    assert(arr2 \ 0 \ "id" == JsonNumber(0))
    assert(arr2 \ 0 \ "name" == JsonString("root"))
    assert(arr2 \ 1 \ "id" == JsonNumber(1000))
    assert(arr2 \ 1 \ "name" == JsonString("lupita"))

    assertThrows[JsonException] { reader.read() }
  }

  private def withResource[R <: AutoCloseable, T](res: R)(op: R => T): T =
    try op(res) finally res.close()
