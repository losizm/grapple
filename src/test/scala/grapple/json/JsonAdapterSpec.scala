/*
 * Copyright 2022 Carlos Conyers
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
import scala.util.Try

class JsonAdapterSpec extends org.scalatest.flatspec.AnyFlatSpec:
  private case class User(id: Int, name: String)

  private given JsonAdapter[User] with
    def read(value: JsonValue) =
      User(value \ "id", value \ "name")

    def write(value: User) =
      Json.obj("id" -> value.id, "name" -> value.name)

  it should "read JSON using adapter" in {
    val json = Json.parse("""{"id": 65534, "name": "nobody"}""")
    val user = json.as[User]

    assert(user.id == 65534)
    assert(user.name == "nobody")
  }

  it should "write JSON using adapter" in {
    val user = User(65534, "nobody")
    val json = Json.toJson(user)

    assert(json.getInt("id") == 65534)
    assert(json.getString("name") == "nobody")
  }
