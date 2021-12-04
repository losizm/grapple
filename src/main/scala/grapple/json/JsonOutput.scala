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

/**
 * Defines JSON output conversion.
 *
 * {{{
 * import scala.language.implicitConversions
 *
 * import grapple.json.{ *, given }
 *
 * case class User(id: Int, name: String)
 *
 * // Define how to convert User to JsonValue
 * given userOutput: JsonOutput[User] =
 *   user => Json.obj("id" -> user.id, "name" -> user.name)
 *
 * val users = Json.arr(User(0, "root"), User(1000, "lupita"))
 * assert { users(0) == Json.obj("id" -> 0, "name" -> "root") }
 * assert { users(1) == Json.obj("id" -> 1000, "name" -> "lupita") }
 * }}}
 *
 * @see [[JsonInput]]
 */
@FunctionalInterface
trait JsonOutput[T]:
  /** Converts to JSON value. */
  def write(value: T): JsonValue
