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

import javax.json.JsonValue

/**
 * Converts JsonValue to T value.
 *
 * {{{
 * import javax.json.JsonObject
 * import little.json.{ Json, FromJson }
 * import little.json.Implicits.JsonValueType
 *
 * case class User(id: Int, name: String)
 *
 * // Define how to convert JsonObject to User
 * implicit val userFromJson: FromJson[User] = {
 *   case json: JsonObject => User(json.getInt("id"), json.getString("name"))
 *   case json => throw new IllegalArgumentException("JsonObject required")
 * }
 *
 * // Parse String to JsonObject
 * val json = Json.parse("""{ "id": 0, "name": "root" }""")
 *
 * // Convert JsonObject to User
 * val user = json.as[User]
 * }}}
 * @see [[ToJson]]
 */
trait FromJson[T] extends (JsonValue => T) {
  /** Converts JsonValue to T value. */
  def apply(json: JsonValue): T
}
