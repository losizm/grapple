/*
 * Copyright 2018 Carlos Conyers
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

import javax.json.{ JsonException, JsonObject, JsonValue }

import scala.util.Success

object Test {
  case class User(id: Int, name: String, enabled: Boolean = true)

  implicit val userToJson: ToJson[User] = { user =>
    val json = Json.createObjectBuilder()
    json.add("id", user.id)
    json.add("name", user.name)
    json.add("enabled", user.enabled)
    json.build()
  }

  implicit val jsonToUser: FromJson[User] = {
    case json: JsonObject => User(json.getInt("id"), json.getString("name"), json.getBoolean("enabled", true))
    case json => throw new JsonException(s"""required OBJECT but found ${json.getValueType}""")
  }
}
