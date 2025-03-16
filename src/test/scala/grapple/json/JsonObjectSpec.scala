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
    assert(user.nonEmpty)
    assert(user.keys == Set("id", "name", "groups", "enabled", "other", "secret"))

    assertThrows[JsonObjectError](user.getObject("id"))
    assertThrows[JsonObjectError](user.getArray("id"))
    assertThrows[JsonObjectError](user.getString("id"))
    assert(user.getInt("id") == 100)
    assert(user.getLong("id") == 100)
    assert(user.getFloat("id") == 100)
    assert(user.getDouble("id") == 100)
    assert(user.getBigInt("id") == 100)
    assert(user.getBigDecimal("id") == 100)
    assertThrows[JsonObjectError](user.getBoolean("id"))
    assert(!user.isNull("id"))

    assertThrows[JsonObjectError](user.getObject("name"))
    assertThrows[JsonObjectError](user.getArray("name"))
    assert(user.getString("name") == "lupita")
    assertThrows[JsonObjectError](user.getInt("name"))
    assertThrows[JsonObjectError](user.getLong("name"))
    assertThrows[JsonObjectError](user.getFloat("name"))
    assertThrows[JsonObjectError](user.getDouble("name"))
    assertThrows[JsonObjectError](user.getBigInt("name"))
    assertThrows[JsonObjectError](user.getBigDecimal("name"))
    assertThrows[JsonObjectError](user.getBoolean("name"))
    assert(!user.isNull("name"))

    assertThrows[JsonObjectError](user.getObject("groups"))
    assert(user.getArray("groups") == JsonArray(Seq(JsonString("lupita"), JsonString("admin"), JsonNumber(100))))
    assertThrows[JsonObjectError](user.getString("groups"))
    assertThrows[JsonObjectError](user.getInt("groups"))
    assertThrows[JsonObjectError](user.getLong("groups"))
    assertThrows[JsonObjectError](user.getFloat("groups"))
    assertThrows[JsonObjectError](user.getDouble("groups"))
    assertThrows[JsonObjectError](user.getBigInt("groups"))
    assertThrows[JsonObjectError](user.getBigDecimal("groups"))
    assertThrows[JsonObjectError](user.getBoolean("groups"))
    assert(!user.isNull("groups"))

    assertThrows[JsonObjectError](user.getObject("enabled"))
    assertThrows[JsonObjectError](user.getArray("enabled"))
    assertThrows[JsonObjectError](user.getString("enabled"))
    assertThrows[JsonObjectError](user.getInt("enabled"))
    assertThrows[JsonObjectError](user.getLong("enabled"))
    assertThrows[JsonObjectError](user.getFloat("enabled"))
    assertThrows[JsonObjectError](user.getDouble("enabled"))
    assertThrows[JsonObjectError](user.getBigInt("enabled"))
    assertThrows[JsonObjectError](user.getBigDecimal("enabled"))
    assert(user.getBoolean("enabled"))
    assert(!user.isNull("enabled"))

    assert(user.getObject("other") == JsonObject(Map("name" -> JsonString("lupita"), "email" -> JsonString("lupita@localhost"), "timeout" -> JsonNumber(10_000_000_000L))))
    assertThrows[JsonObjectError](user.getArray("other"))
    assertThrows[JsonObjectError](user.getString("other"))
    assertThrows[JsonObjectError](user.getInt("other"))
    assertThrows[JsonObjectError](user.getLong("other"))
    assertThrows[JsonObjectError](user.getFloat("other"))
    assertThrows[JsonObjectError](user.getDouble("other"))
    assertThrows[JsonObjectError](user.getBigInt("other"))
    assertThrows[JsonObjectError](user.getBigDecimal("other"))
    assertThrows[JsonObjectError](user.getBoolean("other"))
    assert(!user.isNull("other"))

    assertThrows[JsonObjectError](user.getObject("secret"))
    assertThrows[JsonObjectError](user.getArray("secret"))
    assertThrows[JsonObjectError](user.getString("secret"))
    assertThrows[JsonObjectError](user.getInt("secret"))
    assertThrows[JsonObjectError](user.getLong("secret"))
    assertThrows[JsonObjectError](user.getFloat("secret"))
    assertThrows[JsonObjectError](user.getDouble("secret"))
    assertThrows[JsonObjectError](user.getBigInt("secret"))
    assertThrows[JsonObjectError](user.getBigDecimal("secret"))
    assertThrows[JsonObjectError](user.getBoolean("secret"))
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

    assert(user.get("_id").isEmpty)
    assert(user.get("id").contains(JsonNumber(100)))
    assert(user.get("_name").isEmpty)
    assert(user.get("name").contains(JsonString("lupita")))
    assert(user.get("_enabled").isEmpty)
    assert(user.get("enabled").contains(JsonBoolean.True))
    assert(user.get("_secret").isEmpty)
    assert(user.get("secret").contains(JsonNull))

    assertThrows[JsonObjectError](user.read[Int]("_id"))
    assert(user.read[Int]("id") == 100)
    assertThrows[JsonObjectError](user.read[Int]("_name"))
    assert(user.read[String]("name") == "lupita")
    assertThrows[JsonObjectError](user.read[Int]("_enabled"))
    assert(user.read[Boolean]("enabled"))
    assertThrows[JsonObjectError](user.read[String]("secret"))
    assertThrows[JsonObjectError](user.read[Int]("secret"))
    assertThrows[JsonObjectError](user.read[Boolean]("secret"))
    assertThrows[JsonObjectError](user.read[JsonNull.type]("secret"))

    assert(user.readOption[Int]("_id").isEmpty)
    assert(user.readOption[Int]("id").contains(100))
    assert(user.readOption[String]("_name").isEmpty)
    assert(user.readOption[String]("name").contains("lupita"))
    assert(user.readOption[Boolean]("_enabled").isEmpty)
    assert(user.readOption[Boolean]("enabled").contains(true))
    assert(user.readOption[String]("secret").isEmpty)
    assert(user.readOption[Int]("secret").isEmpty)
    assert(user.readOption[Boolean]("secret").isEmpty)
    assert(user.readOption[JsonNull.type]("secret").isEmpty)

    assert(user.readOrElse("_id", 0) == 0)
    assert(user.readOrElse("id", 0) == 100)
    assert(user.readOrElse("id", 0L) == 100L)
    assert(user.readOrElse("id", 0.0) == 100.0)
    assert(user.readOrElse("id", BigDecimal(0)) == BigDecimal(100))
    assert(user.readOrElse("id", JsonNumber(9999)) == JsonNumber(100))
    assert(user.readOrElse("_name", "none") == "none")
    assert(user.readOrElse("name", "none") == "lupita")
    assert(!user.readOrElse("_enabled", false))
    assert(user.readOrElse("enabled", false))
  }

  it should "inspect empty JsonObject" in {
    assert(JsonObject.empty.size == 0)
    assert(JsonObject.empty.isEmpty)
    assert(JsonObject.empty.keys.isEmpty)
  }

  it should "merge JsonObjects" in {
    val user = Json.obj("id" -> 100, "name" -> "guest") ++ Json.obj("id" -> 2000, "group" -> "staff")
    assert(user("id").as[Int] == 2000)
    assert(user("name").as[String] == "guest")
    assert(user("group").as[String] == "staff")
  }

  it should "add value to JsonObject" in {
    val user = Json.obj("id" -> 100, "name" -> "guest").updated("group", "staff")
    assert(user("id").as[Int] == 100)
    assert(user("name").as[String] == "guest")
    assert(user("group").as[String] == "staff")
  }

  it should "remove value from JsonObject" in {
    val user = Json.obj("id" -> 100, "name" -> "guest", "group" -> "staff").removed("group")
    assert(user("id").as[Int] == 100)
    assert(user("name").as[String] == "guest")
    assert(user.readOrElse("group", "none") == "none")
  }

  it should "deconstruct JsonObject" in {
    assert {
      Json.obj("a" -> 1, "b" -> 2) match
        case JsonObject(fields) => fields("a") == JsonNumber(1) && fields("b") == JsonNumber(2)
        case _                  => false
    }
  }
