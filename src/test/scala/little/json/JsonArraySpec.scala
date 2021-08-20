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

import scala.language.implicitConversions
import Implicits.{ *, given }

class JsonArraySpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "create JsonArray" in {
    val user = Json.arr(
      1000,
      "jza",
      Json.arr("jza", "admin", 1000),
      true,
      Json.obj("name" -> "Jza", "email" -> "jza@localhost", "timeout" -> 10_000_000_000L),
      JsonNull
    )

    assert(!user.isEmpty)
    assert(user.size == 6)

    assertThrows[ClassCastException](user.getObject(0))
    assertThrows[ClassCastException](user.getArray(0))
    assertThrows[ClassCastException](user.getString(0))
    assert(user.getNumber(0) == JsonNumber(1000))
    assertThrows[ClassCastException](user.getBoolean(0))
    assertThrows[ClassCastException](user.getNull(0))

    assertThrows[ClassCastException](user.getObject(1))
    assertThrows[ClassCastException](user.getArray(1))
    assert(user.getString(1) == JsonString("jza"))
    assertThrows[ClassCastException](user.getNumber(1))
    assertThrows[ClassCastException](user.getBoolean(1))
    assertThrows[ClassCastException](user.getNull(1))

    assertThrows[ClassCastException](user.getObject(2))
    assert(user.getArray(2) == JsonArray(Seq(JsonString("jza"), JsonString("admin"), JsonNumber(1000))))
    assertThrows[ClassCastException](user.getString(2) == JsonString("groups"))
    assertThrows[ClassCastException](user.getNumber(2))
    assertThrows[ClassCastException](user.getBoolean(2))
    assertThrows[ClassCastException](user.getNull(2))

    assertThrows[ClassCastException](user.getObject(3))
    assertThrows[ClassCastException](user.getArray(3))
    assertThrows[ClassCastException](user.getString(3))
    assertThrows[ClassCastException](user.getNumber(3))
    assert(user.getBoolean(3) == JsonBoolean(true))
    assertThrows[ClassCastException](user.getNull(3))

    assert(user.getObject(4) == JsonObject(Map("name" -> JsonString("Jza"), "email" -> JsonString("jza@localhost"), "timeout" -> JsonNumber(10_000_000_000L))))
    assertThrows[ClassCastException](user.getArray(4))
    assertThrows[ClassCastException](user.getString(4))
    assertThrows[ClassCastException](user.getNumber(4))
    assertThrows[ClassCastException](user.getBoolean(4))
    assertThrows[ClassCastException](user.getNull(4))

    assertThrows[ClassCastException](user.getObject(5))
    assertThrows[ClassCastException](user.getArray(5))
    assertThrows[ClassCastException](user.getString(5))
    assertThrows[ClassCastException](user.getNumber(5))
    assertThrows[ClassCastException](user.getBoolean(5))
    assert(user.getNull(5) == JsonNull)

    assert(user(0).as[Int] == 1000)
    assert(user(0).as[Long] == 1000L)
    assert(user(0).as[Double] == 1000.0)
    assert(user(0).as[BigDecimal] == BigDecimal(1000))
    assert(user(0).as[JsonValue] == JsonNumber(1000))

    assert(user(1).as[String] == "jza")

    assert(user(2) == Json.arr("jza", "admin", 1000))
    assert((user \ 2 \ 0).as[String] == "jza")
    assert((user \ 2 \ 1).as[String] == "admin")
    assert((user \ 2 \ 2).as[Int] == 1000)

    assert(user(3))

    assert(user(4) == Json.obj("name" -> "Jza", "email" -> "jza@localhost", "timeout" -> 10_000_000_000L))
    assert((user \ 4 \ "name").as[String] == "Jza")
    assert((user \ 4 \ "email").as[String] == "jza@localhost")
    assert((user \ 4 \ "timeout").as[Long] == 10_000_000_000L)
  }

  it should "inspect emtpy JsonArray" in {
    assert(JsonArray.empty.isEmpty)
    assert(JsonArray.empty.size == 0)
  }

  it should "concat JsonArrays" in {
    val user = Json.arr(1000, "guest") ++ Json.arr("staff", "Guest")
    assert(user(0).as[Int] == 1000)
    assert(user(1).as[String] == "guest")
    assert(user(2).as[String] == "staff")
    assert(user(3).as[String] == "Guest")
  }

  it should "prepend value to JsonArray" in {
    val user = 1000 +: Json.arr("guest", "staff", "Guest")
    assert(user(0).as[Int] == 1000)
    assert(user(1).as[String] == "guest")
    assert(user(2).as[String] == "staff")
    assert(user(3).as[String] == "Guest")
  }

  it should "append value to JsonObject" in {
    val user = Json.arr(1000, "guest", "staff") :+ "Guest"
    assert(user(0).as[Int] == 1000)
    assert(user(1).as[String] == "guest")
    assert(user(2).as[String] == "staff")
    assert(user(3).as[String] == "Guest")
  }

  it should "destructure JsonArray" in {
    assert {
      Json.arr(1, 2, "three") match
        case JsonArray(values) => values(0) == JsonNumber(1) && values(1) == JsonNumber(2) && values(2) == JsonString("three")
        case _                 => false
    }

    assert {
      Json.arr(1, 2, "three") match
        case JsonArray(Seq(JsonNumber(0), JsonNumber(1), JsonNumber(2)))       => false
        case JsonArray(Seq(JsonNumber(1), JsonNumber(2), JsonString("three"))) => true
        case _                                                                 => false
    }
  }
