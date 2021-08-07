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

class JsonObjectSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "create JsonObject" in {
    val user = Json.obj(
      "id"      -> 1000,
      "name"    -> "jza",
      "groups"  -> Json.arr("jza", "admin", 1000),
      "enabled" -> true,
      "other"   -> Json.obj("name" -> "Jza", "email" -> "jza@localhost", "timeout" -> 10_000_000_000L),
      "secret"  -> JsonNull
    )

    assert(user.size == 6)
    assert(!user.isEmpty)
    assert(user.names == Seq("id", "name", "groups", "enabled", "other", "secret"))

    assertThrows[ClassCastException](user.getObject("id"))
    assertThrows[ClassCastException](user.getArray("id"))
    assertThrows[ClassCastException](user.getString("id"))
    assert(user.getNumber("id") == JsonNumber(1000))
    assertThrows[ClassCastException](user.getBoolean("id"))
    assertThrows[ClassCastException](user.getNull("id"))

    assertThrows[ClassCastException](user.getObject("name"))
    assertThrows[ClassCastException](user.getArray("name"))
    assert(user.getString("name") == JsonString("jza"))
    assertThrows[ClassCastException](user.getNumber("name"))
    assertThrows[ClassCastException](user.getBoolean("name"))
    assertThrows[ClassCastException](user.getNull("name"))

    assertThrows[ClassCastException](user.getObject("groups"))
    assert(user.getArray("groups") == JsonArray(Seq(JsonString("jza"), JsonString("admin"), JsonNumber(1000))))
    assertThrows[ClassCastException](user.getString("groups") == JsonString("groups"))
    assertThrows[ClassCastException](user.getNumber("groups"))
    assertThrows[ClassCastException](user.getBoolean("groups"))
    assertThrows[ClassCastException](user.getNull("groups"))

    assertThrows[ClassCastException](user.getObject("enabled"))
    assertThrows[ClassCastException](user.getArray("enabled"))
    assertThrows[ClassCastException](user.getString("enabled"))
    assertThrows[ClassCastException](user.getNumber("enabled"))
    assert(user.getBoolean("enabled") == JsonBoolean(true))
    assertThrows[ClassCastException](user.getNull("enabled"))

    assert(user.getObject("other") == JsonObject(Map("name" -> JsonString("Jza"), "email" -> JsonString("jza@localhost"), "timeout" -> JsonNumber(10_000_000_000L))))
    assertThrows[ClassCastException](user.getArray("other"))
    assertThrows[ClassCastException](user.getString("other"))
    assertThrows[ClassCastException](user.getNumber("other"))
    assertThrows[ClassCastException](user.getBoolean("other"))
    assertThrows[ClassCastException](user.getNull("other"))

    assertThrows[ClassCastException](user.getObject("secret"))
    assertThrows[ClassCastException](user.getArray("secret"))
    assertThrows[ClassCastException](user.getString("secret"))
    assertThrows[ClassCastException](user.getNumber("secret"))
    assertThrows[ClassCastException](user.getBoolean("secret"))
    assert(user.getNull("secret") == JsonNull)

    assert(user("id").as[Int] == 1000)
    assert(user("id").as[Long] == 1000L)
    assert(user("id").as[Double] == 1000.0)
    assert(user("id").as[BigDecimal] == BigDecimal(1000))
    assert(user("id") == JsonNumber(1000))

    assert(user("name").as[String] == "jza")

    assert(user("groups") == Json.arr("jza", "admin", 1000))
    assert((user \ "groups" \ 0).as[String] == "jza")
    assert((user \ "groups" \ 1).as[String] == "admin")
    assert((user \ "groups" \ 2).as[Int] == 1000)

    assert(user("enabled").as[Boolean])

    assert(user("other") == Json.obj("name" -> "Jza", "email" -> "jza@localhost", "timeout" -> 10_000_000_000L))
    assert((user \ "other" \ "name").as[String] == "Jza")
    assert((user \ "other" \ "email").as[String] == "jza@localhost")
    assert((user \ "other" \ "timeout").as[Long] == 10_000_000_000L)

    assert(user("secret").as[Option[String]] == None)
    assert(user("secret").as[Option[Int]] == None)
    assert(user("secret").as[Option[Boolean]] == None)
    assert(user("secret") == JsonNull)

    assert(user.getOrElse("_id", 0) == 0)
    assert(user.getOrElse("id", 0) == 1000)
    assert(user.getOrElse("id", 0L) == 1000L)
    assert(user.getOrElse("id", 0.0) == 1000.0)
    assert(user.getOrElse("id", BigDecimal(0)) == BigDecimal(1000))
    assert(user.getOrElse("id", JsonNumber(9999)) == JsonNumber(1000))
    assert(user.getOrElse("_name", "none") == "none")
    assert(user.getOrElse("name", "none") == "jza")
    assert(!user.getOrElse("_enabled", false))
    assert(user.getOrElse("enabled", false))

    assert(user.get("_id").isEmpty)
    assert(user.get("id").contains(JsonNumber(1000)))
    assert(user.get("_name").isEmpty)
    assert(user.get("name").contains(JsonString("jza")))
    assert(user.get("_enabled").isEmpty)
    assert(user.get("enabled").contains(JsonTrue))
    assert(user.get("_secret").isEmpty)
    assert(user.get("secret").contains(JsonNull))

    assert(user.map[Int]("_id").isEmpty)
    assert(user.map[Int]("id").contains(1000))
    assert(user.map[String]("_name").isEmpty)
    assert(user.map[String]("name").contains("jza"))
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
    val user = Json.obj("id" -> 1000, "name" -> "guest") ++ Json.obj("id" -> 2000, "group" -> "staff")
    assert(user("id").as[Int] == 2000)
    assert(user("name").as[String] == "guest")
    assert(user("group").as[String] == "staff")
  }

  it should "add value to JsonObject" in {
    val user = Json.obj("id" -> 1000, "name" -> "guest") + ("group" -> "staff")
    assert(user("id").as[Int] == 1000)
    assert(user("name").as[String] == "guest")
    assert(user("group").as[String] == "staff")
  }

  it should "remove value from JsonObject" in {
    val user = Json.obj("id" -> 1000, "name" -> "guest", "group" -> "staff") - "group"
    assert(user("id").as[Int] == 1000)
    assert(user("name").as[String] == "guest")
    assert(user.getOrElse("group", "none") == "none")
  }
