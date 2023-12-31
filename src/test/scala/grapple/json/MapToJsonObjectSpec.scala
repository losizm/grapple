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

class MapToJsonObjectSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "create JsonObject from Map[String, Int]" in {
    val users = Map("root" -> 0, "lupita" -> 100, "nobody" -> 65534)
    val json  = Json.toJson(users).as[JsonObject]

    assert(json.size == 3)
    assert(json.keys == Set("root", "lupita", "nobody"))
    assert(json.getInt("root")   == 0)
    assert(json.getInt("lupita") == 100)
    assert(json.getInt("nobody") == 65534)
  }

  it should "create JsonObject from Map[String, Array[String]]" in {
    val users = Map(
      "root"     -> Array("root"),
      "chadwick" -> Array("chadwick", "sudo", "admin"),
      "lupita"   -> Array("lupita", "admin"),
      "nobody"   -> Array("nobody")
    )
    val json  = Json.toJson(users).as[JsonObject]

    assert(json.size == 4)
    assert(json.keys == Set("root", "chadwick", "lupita", "nobody"))
    assert(json.getArray("root")     == Json.arr("root"))
    assert(json.getArray("chadwick") == Json.arr("chadwick", "sudo", "admin"))
    assert(json.getArray("lupita")   == Json.arr("lupita", "admin"))
    assert(json.getArray("nobody")   == Json.arr("nobody"))
  }

  it should "create JsonObject from Map[String, Map[String, Int]]" in {
    val users = Map(
      "root"     -> Map("id" -> 0),
      "lupita"   -> Map("id" -> 100),
      "nobody"   -> Map("id" -> 65534)
    )
    val json  = Json.toJson(users).as[JsonObject]

    assert(json.size == 3)
    assert(json.keys == Set("root", "lupita", "nobody"))
    assert(json.getObject("root")   == Json.obj("id" -> 0))
    assert(json.getObject("lupita") == Json.obj("id" -> 100))
    assert(json.getObject("nobody") == Json.obj("id" -> 65534))
  }

  it should "create JsonObject from Map[String, User]" in {
    case class User(id: Int, name: String, groups: Seq[String])

    given JsonOutput[User] =
      user => Json.obj(
        "id"     -> user.id,
        "name"   -> user.name,
        "groups" -> user.groups
      )

    val users = Map(
      "root"     -> User(0, "root", Seq("root")),
      "lupita"   -> User(100, "lupita", Seq("lupita", "admin")),
      "nobody"   -> User(65534, "nobody", Seq("nobody"))
    )
    val json  = Json.toJson(users).as[JsonObject]

    assert(json.size == 3)
    assert(json.keys == Set("root", "lupita", "nobody"))
    assert(json.getObject("root")   == Json.obj("id" -> 0,     "name" -> "root",   "groups" -> Json.arr("root")))
    assert(json.getObject("lupita") == Json.obj("id" -> 100,   "name" -> "lupita", "groups" -> Json.arr("lupita", "admin")))
    assert(json.getObject("nobody") == Json.obj("id" -> 65534, "name" -> "nobody", "groups" -> Json.arr("nobody")))
  }
