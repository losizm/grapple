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

import javax.json.{ JsonArrayBuilder, JsonObjectBuilder, JsonValue }
import javax.json.stream.JsonGenerator

/**
 * Converts value of type T to JSON.
 *
 * {{{
 * import little.json.{ Json, ToJson }
 *
 * case class User(id: Int, name: String)
 *
 * // Define how to convert User to JSON
 * implicit val userToJson: ToJson[User] = { user =>
 *   Json.createObjectBuilder()
 *     .add("id", user.id)
 *     .add("name", user.name)
 *     .build()
 * }
 *
 * // Convert User to JSON
 * val json = Json.toJson(User(0, "root"))
 * }}}
 *
 * @see [[FromJson]]
 */
trait ToJson[T] extends (T => JsonValue) with BuilderCompanion[T] with ContextWriter[T] {
  /** Converts value to JSON. */
  def apply(value: T): JsonValue

  /** Converts value to JSON and adds it to array builder. */
  def add(value: T)(implicit builder: JsonArrayBuilder): JsonArrayBuilder =
    builder.add(apply(value))

  /** Converts value to JSON and adds it to object builder. */
  def add(name: String, value: T)(implicit builder: JsonObjectBuilder): JsonObjectBuilder =
    builder.add(name, apply(value))

  /** Converts value to JSON and writes it to array context. */
  def write(value: T)(implicit generator: JsonGenerator): JsonGenerator =
    generator.write(apply(value))

  /** Converts value to JSON and writes it to object context. */
  def write(name: String, value: T)(implicit generator: JsonGenerator): JsonGenerator =
    generator.write(name, apply(value))
}
