/*
 * Copyright 2019 Carlos Conyers
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

  implicit object UserAdapter extends JsonAdapter[User] {
    def reading(json: JsonValue): User =
      json.asInstanceOf[JsonObject] match {
        case obj => User(obj.getInt("id"), obj.getString("name"), obj.getBoolean("enabled", true))
      }

    def writing(user: User): JsonValue =
      Json.createObjectBuilder()
        .add("id", user.id)
        .add("name", user.name)
        .add("enabled", user.enabled)
        .build()
  }
}
