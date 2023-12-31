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

import scala.language.implicitConversions

class JsonArraySpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "create JsonArray" in {
    val user = Json.arr(
      100,
      "lupita",
      Json.arr("lupita", "admin", 100),
      true,
      Json.obj("name" -> "lupita", "email" -> "lupita@localhost", "timeout" -> 10_000_000_000L),
      JsonNull
    )

    assert(user.nonEmpty)
    assert(user.size == 6)

    assertThrows[ClassCastException](user.getObject(0))
    assertThrows[ClassCastException](user.getArray(0))
    assertThrows[ClassCastException](user.getString(0))
    assert(user.getInt(0) == 100)
    assert(user.getLong(0) == 100)
    assert(user.getFloat(0) == 100)
    assert(user.getDouble(0) == 100)
    assert(user.getBigInt(0) == 100)
    assert(user.getBigDecimal(0) == 100)
    assertThrows[ClassCastException](user.getBoolean(0))
    assert(!user.isNull(0))

    assertThrows[ClassCastException](user.getObject(1))
    assertThrows[ClassCastException](user.getArray(1))
    assert(user.getString(1) == "lupita")
    assertThrows[ClassCastException](user.getInt(1))
    assertThrows[ClassCastException](user.getLong(1))
    assertThrows[ClassCastException](user.getFloat(1))
    assertThrows[ClassCastException](user.getDouble(1))
    assertThrows[ClassCastException](user.getBigInt(1))
    assertThrows[ClassCastException](user.getBigDecimal(1))
    assertThrows[ClassCastException](user.getBoolean(1))
    assert(!user.isNull(1))

    assertThrows[ClassCastException](user.getObject(2))
    assert(user.getArray(2) == JsonArray(Seq(JsonString("lupita"), JsonString("admin"), JsonNumber(100))))
    assertThrows[ClassCastException](user.getString(2))
    assertThrows[ClassCastException](user.getInt(2))
    assertThrows[ClassCastException](user.getLong(2))
    assertThrows[ClassCastException](user.getFloat(2))
    assertThrows[ClassCastException](user.getDouble(2))
    assertThrows[ClassCastException](user.getBigInt(2))
    assertThrows[ClassCastException](user.getBigDecimal(2))
    assertThrows[ClassCastException](user.getBoolean(2))
    assert(!user.isNull(2))

    assertThrows[ClassCastException](user.getObject(3))
    assertThrows[ClassCastException](user.getArray(3))
    assertThrows[ClassCastException](user.getString(3))
    assertThrows[ClassCastException](user.getInt(3))
    assertThrows[ClassCastException](user.getLong(3))
    assertThrows[ClassCastException](user.getFloat(3))
    assertThrows[ClassCastException](user.getDouble(3))
    assertThrows[ClassCastException](user.getBigInt(3))
    assertThrows[ClassCastException](user.getBigDecimal(3))
    assert(user.getBoolean(3))
    assert(!user.isNull(3))

    assert(user.getObject(4) == JsonObject(Map("name" -> JsonString("lupita"), "email" -> JsonString("lupita@localhost"), "timeout" -> JsonNumber(10_000_000_000L))))
    assertThrows[ClassCastException](user.getArray(4))
    assertThrows[ClassCastException](user.getString(4))
    assertThrows[ClassCastException](user.getInt(4))
    assertThrows[ClassCastException](user.getLong(4))
    assertThrows[ClassCastException](user.getFloat(4))
    assertThrows[ClassCastException](user.getDouble(4))
    assertThrows[ClassCastException](user.getBigInt(4))
    assertThrows[ClassCastException](user.getBigDecimal(4))
    assertThrows[ClassCastException](user.getBoolean(4))
    assert(!user.isNull(4))

    assertThrows[ClassCastException](user.getObject(5))
    assertThrows[ClassCastException](user.getArray(5))
    assertThrows[ClassCastException](user.getString(5))
    assertThrows[ClassCastException](user.getInt(5))
    assertThrows[ClassCastException](user.getLong(5))
    assertThrows[ClassCastException](user.getFloat(5))
    assertThrows[ClassCastException](user.getDouble(5))
    assertThrows[ClassCastException](user.getBigInt(5))
    assertThrows[ClassCastException](user.getBigDecimal(5))
    assertThrows[ClassCastException](user.getBoolean(5))
    assert(user.isNull(5))

    assert(user.read[Int](0) == 100)
    assert(user.read[Long](0) == 100L)
    assert(user.read[Float](0) == 100.0f)
    assert(user.read[Double](0) == 100.0)
    assert(user.read[BigInt](0) == BigInt(100))
    assert(user.read[BigDecimal](0) == BigDecimal(100))
    assert(user.read[JsonNumber](0) == JsonNumber(100))
    assertThrows[ClassCastException](user.read[String](0))
    assertThrows[ClassCastException](user.read[Boolean](0))
    assertThrows[ClassCastException](user.read[JsonString](0))
    assertThrows[ClassCastException](user.read[JsonArray](0))

    assert(user(0).as[Int] == 100)
    assert(user(0).as[Long] == 100L)
    assert(user(0).as[Double] == 100.0)
    assert(user(0).as[BigDecimal] == BigDecimal(100))
    assert(user(0) == JsonNumber(100))

    assert(user(1).as[String] == "lupita")

    assert(user(2) == Json.arr("lupita", "admin", 100))
    assert((user \ 2 \ 0).as[String] == "lupita")
    assert((user \ 2 \ 1).as[String] == "admin")
    assert((user \ 2 \ 2).as[Int] == 100)

    assert(user(3))

    assert(user(4) == Json.obj("name" -> "lupita", "email" -> "lupita@localhost", "timeout" -> 10_000_000_000L))
    assert((user \ 4 \ "name").as[String] == "lupita")
    assert((user \ 4 \ "email").as[String] == "lupita@localhost")
    assert((user \ 4 \ "timeout").as[Long] == 10_000_000_000L)

    assert(user(5) == JsonNull)
    assertThrows[NullPointerException](user.read[String](5))
  }

  it should "inspect empty JsonArray" in {
    assert(JsonArray.empty.isEmpty)
    assert(JsonArray.empty.size == 0)
  }

  it should "remove values from JsonArray" in {
    assertThrows[IndexOutOfBoundsException](Json.arr().removed(-1))
    assertThrows[IndexOutOfBoundsException](Json.arr().removed(0))
    assertThrows[IndexOutOfBoundsException](Json.arr().removed(1))

    assert(Json.arr(0).removed(0).isEmpty)
    assertThrows[IndexOutOfBoundsException](Json.arr(0).removed(-1))
    assertThrows[IndexOutOfBoundsException](Json.arr(0).removed(1))

    assert(Json.arr(0, 1, 2, 3).removed(0) == Json.arr(1, 2, 3))
    assert(Json.arr(0, 1, 2, 3).removed(1) == Json.arr(0, 2, 3))
    assert(Json.arr(0, 1, 2, 3).removed(2) == Json.arr(0, 1, 3))
    assert(Json.arr(0, 1, 2, 3).removed(3) == Json.arr(0, 1, 2))
    assertThrows[IndexOutOfBoundsException](Json.arr(0, 1, 2, 3).removed(-1))
    assertThrows[IndexOutOfBoundsException](Json.arr(0, 1, 2, 3).removed(4))
  }

  it should "update JsonArray" in {
    val user1 = Json.arr(100, "guest", Json.arr("cdrom", "usb"))
    assert(user1(0).as[Int] == 100)
    assert(user1(1).as[String] == "guest")
    assert(user1(2).as[Set[String]] == Set("cdrom", "usb"))

    val user2 = user1.updated(0, 65534)
    assert(user2(0).as[Int] == 65534)
    assert(user2(1).as[String] == "guest")
    assert(user2(2).as[Set[String]] == Set("cdrom", "usb"))

    val user3 = user2.updated(1, "nobody")
    assert(user3(0).as[Int] == 65534)
    assert(user3(1).as[String] == "nobody")
    assert(user3(2).as[Set[String]] == Set("cdrom", "usb"))

    val user4 = user3.updated(2, JsonNull)
    assert(user4(0).as[Int] == 65534)
    assert(user4(1).as[String] == "nobody")
    assert(user4(2) == JsonNull)

    assertThrows[IndexOutOfBoundsException](user1.updated(3, "staff"))
  }

  it should "concat JsonArrays" in {
    val user = Json.arr(100, "guest") ++ Json.arr("staff", "Guest")
    assert(user(0).as[Int] == 100)
    assert(user(1).as[String] == "guest")
    assert(user(2).as[String] == "staff")
    assert(user(3).as[String] == "Guest")
  }

  it should "prepend value to JsonArray" in {
    val user = 100 +: Json.arr("guest", "staff", "Guest")
    assert(user(0).as[Int] == 100)
    assert(user(1).as[String] == "guest")
    assert(user(2).as[String] == "staff")
    assert(user(3).as[String] == "Guest")
  }

  it should "append value to JsonObject" in {
    val user = Json.arr(100, "guest", "staff") :+ "Guest"
    assert(user(0).as[Int] == 100)
    assert(user(1).as[String] == "guest")
    assert(user(2).as[String] == "staff")
    assert(user(3).as[String] == "Guest")
  }

  it should "deconstruct JsonArray" in {
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
