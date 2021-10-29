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
import Implicits.{ *, given }

class JsonObjectSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "create JsonObject" in {
    val user = Json.obj(
      "id"      -> 100,
      "name"    -> "lupita",
      "groups"  -> Json.arr("lupita", "admin", 100),
      "enabled" -> true,
      "other"   -> Json.obj("name" -> "lupita", "email" -> "lupita@localhost", "timeout" -> 10_000_000_000L),
      "secret"  -> JsonNull
    )

    assert(user.size == 6)
    assert(!user.isEmpty)
    assert(user.names == Seq("id", "name", "groups", "enabled", "other", "secret"))

    assertThrows[ClassCastException](user.getJsonObject("id"))
    assertThrows[ClassCastException](user.getJsonArray("id"))
    assertThrows[ClassCastException](user.getJsonString("id"))
    assert(user.getJsonNumber("id") == JsonNumber(100))
    assertThrows[ClassCastException](user.getJsonBoolean("id"))
    assertThrows[ClassCastException](user.getJsonNull("id"))

    assertThrows[ClassCastException](user.getString("id"))
    assert(user.getByte("id") == 100)
    assert(user.getShort("id") == 100)
    assert(user.getInt("id") == 100)
    assert(user.getLong("id") == 100)
    assert(user.getFloat("id") == 100)
    assert(user.getDouble("id") == 100)
    assert(user.getBigInt("id") == 100)
    assert(user.getBigDecimal("id") == 100)
    assertThrows[ClassCastException](user.getBoolean("id"))
    assert(!user.isNull("id"))

    assertThrows[ClassCastException](user.getJsonObject("name"))
    assertThrows[ClassCastException](user.getJsonArray("name"))
    assert(user.getJsonString("name") == JsonString("lupita"))
    assertThrows[ClassCastException](user.getJsonNumber("name"))
    assertThrows[ClassCastException](user.getJsonBoolean("name"))
    assertThrows[ClassCastException](user.getJsonNull("name"))

    assert(user.getString("name") == "lupita")
    assertThrows[ClassCastException](user.getByte("name"))
    assertThrows[ClassCastException](user.getShort("name"))
    assertThrows[ClassCastException](user.getInt("name"))
    assertThrows[ClassCastException](user.getLong("name"))
    assertThrows[ClassCastException](user.getFloat("name"))
    assertThrows[ClassCastException](user.getDouble("name"))
    assertThrows[ClassCastException](user.getBigInt("name"))
    assertThrows[ClassCastException](user.getBigDecimal("name"))
    assertThrows[ClassCastException](user.getBoolean("name"))
    assert(!user.isNull("name"))

    assertThrows[ClassCastException](user.getJsonObject("groups"))
    assert(user.getJsonArray("groups") == JsonArray(Seq(JsonString("lupita"), JsonString("admin"), JsonNumber(100))))
    assertThrows[ClassCastException](user.getJsonString("groups"))
    assertThrows[ClassCastException](user.getJsonNumber("groups"))
    assertThrows[ClassCastException](user.getJsonBoolean("groups"))
    assertThrows[ClassCastException](user.getJsonNull("groups"))

    assertThrows[ClassCastException](user.getString("groups"))
    assertThrows[ClassCastException](user.getByte("groups"))
    assertThrows[ClassCastException](user.getShort("groups"))
    assertThrows[ClassCastException](user.getInt("groups"))
    assertThrows[ClassCastException](user.getLong("groups"))
    assertThrows[ClassCastException](user.getFloat("groups"))
    assertThrows[ClassCastException](user.getDouble("groups"))
    assertThrows[ClassCastException](user.getBigInt("groups"))
    assertThrows[ClassCastException](user.getBigDecimal("groups"))
    assertThrows[ClassCastException](user.getBoolean("groups"))
    assert(!user.isNull("groups"))

    assertThrows[ClassCastException](user.getJsonObject("enabled"))
    assertThrows[ClassCastException](user.getJsonArray("enabled"))
    assertThrows[ClassCastException](user.getJsonString("enabled"))
    assertThrows[ClassCastException](user.getJsonNumber("enabled"))
    assert(user.getJsonBoolean("enabled") == JsonBoolean(true))
    assertThrows[ClassCastException](user.getJsonNull("enabled"))

    assertThrows[ClassCastException](user.getString("enabled"))
    assertThrows[ClassCastException](user.getByte("enabled"))
    assertThrows[ClassCastException](user.getShort("enabled"))
    assertThrows[ClassCastException](user.getInt("enabled"))
    assertThrows[ClassCastException](user.getLong("enabled"))
    assertThrows[ClassCastException](user.getFloat("enabled"))
    assertThrows[ClassCastException](user.getDouble("enabled"))
    assertThrows[ClassCastException](user.getBigInt("enabled"))
    assertThrows[ClassCastException](user.getBigDecimal("enabled"))
    assert(user.getBoolean("enabled"))
    assert(!user.isNull("enabled"))

    assert(user.getJsonObject("other") == JsonObject(Map("name" -> JsonString("lupita"), "email" -> JsonString("lupita@localhost"), "timeout" -> JsonNumber(10_000_000_000L))))
    assertThrows[ClassCastException](user.getJsonArray("other"))
    assertThrows[ClassCastException](user.getJsonString("other"))
    assertThrows[ClassCastException](user.getJsonNumber("other"))
    assertThrows[ClassCastException](user.getJsonBoolean("other"))
    assertThrows[ClassCastException](user.getJsonNull("other"))

    assertThrows[ClassCastException](user.getString("other"))
    assertThrows[ClassCastException](user.getByte("other"))
    assertThrows[ClassCastException](user.getShort("other"))
    assertThrows[ClassCastException](user.getInt("other"))
    assertThrows[ClassCastException](user.getLong("other"))
    assertThrows[ClassCastException](user.getFloat("other"))
    assertThrows[ClassCastException](user.getDouble("other"))
    assertThrows[ClassCastException](user.getBigInt("other"))
    assertThrows[ClassCastException](user.getBigDecimal("other"))
    assertThrows[ClassCastException](user.getBoolean("other"))
    assert(!user.isNull("other"))

    assertThrows[ClassCastException](user.getJsonObject("secret"))
    assertThrows[ClassCastException](user.getJsonArray("secret"))
    assertThrows[ClassCastException](user.getJsonString("secret"))
    assertThrows[ClassCastException](user.getJsonNumber("secret"))
    assertThrows[ClassCastException](user.getJsonBoolean("secret"))
    assert(user.getJsonNull("secret") == JsonNull)

    assertThrows[ClassCastException](user.getString("secret"))
    assertThrows[ClassCastException](user.getByte("secret"))
    assertThrows[ClassCastException](user.getShort("secret"))
    assertThrows[ClassCastException](user.getInt("secret"))
    assertThrows[ClassCastException](user.getLong("secret"))
    assertThrows[ClassCastException](user.getFloat("secret"))
    assertThrows[ClassCastException](user.getDouble("secret"))
    assertThrows[ClassCastException](user.getBigInt("secret"))
    assertThrows[ClassCastException](user.getBigDecimal("secret"))
    assertThrows[ClassCastException](user.getBoolean("secret"))
    assert(user.isNull("secret"))

    assert(user("id").as[Int] == 100)
    assert(user("id").as[Long] == 100L)
    assert(user("id").as[Double] == 100.0)
    assert(user("id").as[BigDecimal] == BigDecimal(100))
    assert(user("id") == JsonNumber(100))

    assert(user("name").as[String] == "lupita")

    assert(user("groups") == Json.arr("lupita", "admin", 100))
    assert((user \ "groups" \ 0).as[String] == "lupita")
    assert((user \ "groups" \ 1).as[String] == "admin")
    assert((user \ "groups" \ 2).as[Int] == 100)

    assert(user("enabled").as[Boolean])

    assert(user("other") == Json.obj("name" -> "lupita", "email" -> "lupita@localhost", "timeout" -> 10_000_000_000L))
    assert((user \ "other" \ "name").as[String] == "lupita")
    assert((user \ "other" \ "email").as[String] == "lupita@localhost")
    assert((user \ "other" \ "timeout").as[Long] == 10_000_000_000L)

    assert(user("secret").as[Option[String]] == None)
    assert(user("secret").as[Option[Int]] == None)
    assert(user("secret").as[Option[Boolean]] == None)
    assert(user("secret") == JsonNull)

    assert(user.getOrElse("_id", 0) == 0)
    assert(user.getOrElse("id", 0) == 100)
    assert(user.getOrElse("id", 0L) == 100L)
    assert(user.getOrElse("id", 0.0) == 100.0)
    assert(user.getOrElse("id", BigDecimal(0)) == BigDecimal(100))
    assert(user.getOrElse("id", JsonNumber(9999)) == JsonNumber(100))
    assert(user.getOrElse("_name", "none") == "none")
    assert(user.getOrElse("name", "none") == "lupita")
    assert(!user.getOrElse("_enabled", false))
    assert(user.getOrElse("enabled", false))

    assert(user.get("_id").isEmpty)
    assert(user.get("id").contains(JsonNumber(100)))
    assert(user.get("_name").isEmpty)
    assert(user.get("name").contains(JsonString("lupita")))
    assert(user.get("_enabled").isEmpty)
    assert(user.get("enabled").contains(JsonTrue))
    assert(user.get("_secret").isEmpty)
    assert(user.get("secret").contains(JsonNull))

    assert(user.map[Int]("_id").isEmpty)
    assert(user.map[Int]("id").contains(100))
    assert(user.map[String]("_name").isEmpty)
    assert(user.map[String]("name").contains("lupita"))
    assert(user.map[Boolean]("_enabled").isEmpty)
    assert(user.map[Boolean]("enabled").contains(true))
    assert(user.map[String]("secret").isEmpty)
    assert(user.map[Int]("secret").isEmpty)
    assert(user.map[Boolean]("secret").isEmpty)
    assert(user.map[JsonNull]("secret").isEmpty)
  }

  it should "inspect empty JsonObject" in {
    assert(JsonObject.empty.size == 0)
    assert(JsonObject.empty.isEmpty)
    assert(JsonObject.empty.names.isEmpty)
  }

  it should "merge JsonObjects" in {
    val user = Json.obj("id" -> 100, "name" -> "guest") ++ Json.obj("id" -> 2000, "group" -> "staff")
    assert(user("id").as[Int] == 2000)
    assert(user("name").as[String] == "guest")
    assert(user("group").as[String] == "staff")
  }

  it should "add value to JsonObject" in {
    val user = Json.obj("id" -> 100, "name" -> "guest") + ("group" -> "staff")
    assert(user("id").as[Int] == 100)
    assert(user("name").as[String] == "guest")
    assert(user("group").as[String] == "staff")
  }

  it should "remove value from JsonObject" in {
    val user = Json.obj("id" -> 100, "name" -> "guest", "group" -> "staff") - "group"
    assert(user("id").as[Int] == 100)
    assert(user("name").as[String] == "guest")
    assert(user.getOrElse("group", "none") == "none")
  }

  it should "destructure JsonObject" in {
    assert {
      Json.obj("a" -> 1, "b" -> 2) match
        case JsonObject(fields) => fields("a") == JsonNumber(1) && fields("b") == JsonNumber(2)
        case _                  => false
    }
  }
