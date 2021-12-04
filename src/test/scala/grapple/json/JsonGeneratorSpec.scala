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

import java.io.StringWriter

import scala.language.implicitConversions

class JsonGeneratorSpec extends org.scalatest.flatspec.AnyFlatSpec:
  case class User(id: Int, name: String)

  given userToJson: JsonOutput[User] with
    def write(u: User) = Json.obj("id" -> u.id, "name" -> u.name)

  it should "write JSON object" in {
    val json = Json.obj(
      "id"     -> 1000,
      "name"   -> "lupita",
      "groups" -> Seq("lupita", "admin", "sudoer"),
      "info"   -> Json.obj("home" -> "/home/lupita", "storage" -> 8L * 1024 * 1024 * 1024),
      "root"   -> User(0, "root"),
      "nobody" -> User(65534, "nobody"),
      "photo"  -> JsonNull
    )

    val buf = StringWriter()
    val out = JsonGenerator(buf)

    try
      out.writeStartObject()
      out.write("id", 1000)
      out.write("name", "lupita")
      out.writeStartArray("groups")
      out.write("lupita")
      out.write("admin")
      out.write("sudoer")
      out.writeEnd()
      out.writeStartObject("info")
      out.write("home", "/home/lupita")
      out.write("storage", 8L * 1024 * 1024 * 1024)
      out.writeEnd()
      out.write("root", User(0, "root"))
      out.write("nobody", User(65534, "nobody"))
      out.writeNull("photo")
      out.writeEnd()
      out.flush()

      val copy = Json.parse(buf.toString)
      assert(copy == json)
    finally
      out.close()
  }

  it should "write JSON array" in {
    val json = Json.arr(
      1000,
      "lupita",
      Seq("lupita", "admin", "sudoer"),
      Json.obj("home" -> "/home/lupita", "storage" -> 8L * 1024 * 1024 * 1024),
      User(0, "root"),
      User(65534, "nobody"),
      JsonNull
    )

    val buf = StringWriter()
    val out = JsonGenerator(buf)

    try
      out.writeStartArray()
      out.write(1000)
      out.write("lupita")
      out.writeStartArray()
      out.write("lupita")
      out.write("admin")
      out.write("sudoer")
      out.writeEnd()
      out.writeStartObject()
      out.write("home", "/home/lupita")
      out.write("storage", 8L * 1024 * 1024 * 1024)
      out.writeEnd()
      out.write(User(0, "root"))
      out.write(User(65534, "nobody"))
      out.writeNull()
      out.writeEnd()
      out.flush()

      val copy = Json.parse(buf.toString)
      assert(copy == json)
    finally
      out.close()
  }

  it should "not write vale in empty context" in {
    val out = JsonGenerator(StringWriter())
    try
      assertThrows[JsonGeneratorError](out.write(1))
      assertThrows[JsonGeneratorError](out.write("a", 1))
    finally
      out.close()
  }

  it should "not write standalone value in object context" in {
    val out = JsonGenerator(StringWriter())
    try
      out.writeStartObject()
      assertThrows[JsonGeneratorError](out.write(1))
      assertThrows[JsonGeneratorError](out.writeStartObject())
      assertThrows[JsonGeneratorError](out.writeStartArray())

      out.writeStartObject("test")
      assertThrows[JsonGeneratorError](out.write(1))
      assertThrows[JsonGeneratorError](out.writeStartObject())
      assertThrows[JsonGeneratorError](out.writeStartArray())

      out.write("test", 0)
      assertThrows[JsonGeneratorError](out.write(1))
      assertThrows[JsonGeneratorError](out.writeStartObject())
      assertThrows[JsonGeneratorError](out.writeStartArray())
    finally
      out.close()
  }

  it should "not write field in array context" in {
    val out = JsonGenerator(StringWriter())
    try
      out.writeStartArray()
      assertThrows[JsonGeneratorError](out.write("a", 1))
      assertThrows[JsonGeneratorError](out.writeStartObject("a"))
      assertThrows[JsonGeneratorError](out.writeStartArray("a"))

      out.writeStartArray()
      assertThrows[JsonGeneratorError](out.write("a", 1))
      assertThrows[JsonGeneratorError](out.writeStartObject("a"))
      assertThrows[JsonGeneratorError](out.writeStartArray("a"))

      out.write(0)
      assertThrows[JsonGeneratorError](out.write("a", 1))
      assertThrows[JsonGeneratorError](out.writeStartObject("a"))
      assertThrows[JsonGeneratorError](out.writeStartArray("a"))
    finally
      out.close()
  }
