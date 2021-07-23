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
import scala.util.{ Failure, Success }
import Implicits.given

class JsonOutputSpec extends org.scalatest.flatspec.AnyFlatSpec:
  case class User(id: Int, name: String, groups: Set[String])

  val root  = User(0,    "root",  Set("root", "admin"))
  val guest = User(1000, "guest", Set("guest"))
  val other = User(1001, "other", Set("other"))

  given userToJson: JsonOutput[User] with
    def apply(value: User) =
      Json.obj(
        "id"     -> value.id,
        "name"   -> value.name,
        "groups" -> value.groups
      )

  it should "create JsonObject using JsonOutput" in {
    val obj = Json.obj(
      "root"    -> root,
      "guest"   -> guest,
      "other"   -> other,
      "users"   -> Seq(root, guest, other),
      "some"    -> Some(guest),
      "none"    -> None,
      "right"   -> Right("Hello"),
      "left"    -> Left(1),
      "success" -> Success(root),
      "failure" -> Failure(Exception())
    )

    assert(obj.names == Seq("root", "guest", "other", "users", "some", "none", "right", "left", "success", "failure"))
    assert(obj("root")  == Json.obj("id" -> 0,    "name" -> "root",  "groups" -> Set("root", "admin")))
    assert(obj("guest") == Json.obj("id" -> 1000, "name" -> "guest", "groups" -> Set("guest")))
    assert(obj("other") == Json.obj("id" -> 1001, "name" -> "other", "groups" -> Set("other")))

    assert(obj("users") == Json.arr(
      Json.obj("id" -> 0,    "name" -> "root",  "groups" -> Set("root", "admin")),
      Json.obj("id" -> 1000, "name" -> "guest", "groups" -> Set("guest")),
      Json.obj("id" -> 1001, "name" -> "other", "groups" -> Set("other"))
    ))

    assert(obj("some")    == Json.obj("id" -> 1000, "name" -> "guest", "groups" -> Set("guest")))
    assert(obj("none")    == JsonNull)
    assert(obj("right")   == JsonString("Hello"))
    assert(obj("left")    == JsonNumber(1))
    assert(obj("success") == Json.obj("id" -> 0,    "name" -> "root",  "groups" -> Set("root", "admin")))
    assert(obj("failure") == JsonNull)
  }

  it should "create JsonArray using JsonOutput" in {
    val arr = Json.arr(
      root,
      guest,
      other,
      Seq(root, guest, other),
      Some(guest),
      None,
      Right("Hello"),
      Left(1),
      Success(root),
      Failure(Exception())
    )

    assert(arr.size == 10)
    assert(arr(0) == Json.obj("id" -> 0,    "name" -> "root",  "groups" -> Set("root", "admin")))
    assert(arr(1) == Json.obj("id" -> 1000, "name" -> "guest", "groups" -> Set("guest")))
    assert(arr(2) == Json.obj("id" -> 1001, "name" -> "other", "groups" -> Set("other")))

    assert(arr(3) == Json.arr(
      Json.obj("id" -> 0,    "name" -> "root",  "groups" -> Set("root", "admin")),
      Json.obj("id" -> 1000, "name" -> "guest", "groups" -> Set("guest")),
      Json.obj("id" -> 1001, "name" -> "other", "groups" -> Set("other"))
    ))

    assert(arr(4) == Json.obj("id" -> 1000, "name" -> "guest", "groups" -> Set("guest")))
    assert(arr(5)  == JsonNull)
    assert(arr(6) == JsonString("Hello"))
    assert(arr(7) == JsonNumber(1))
    assert(arr(8) == Json.obj("id" -> 0,    "name" -> "root",  "groups" -> Set("root", "admin")))
    assert(arr(9)  == JsonNull)
  }
