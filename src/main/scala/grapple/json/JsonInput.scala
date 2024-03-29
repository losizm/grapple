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
 * Defines JSON input conversion.
 *
 * {{{
 * import scala.language.implicitConversions
 *
 * import grapple.json.{ *, given }
 *
 * case class User(id: Int, name: String)
 *
 * // Define how to convert JsonValue to User
 * given userInput: JsonInput[User] =
 *   json => User(json("id"), json("name"))
 *
 * val json = Json.obj("id" -> 0, "name" -> "root")
 * assert { json.as[User] == User(0, "root") }
 * }}}
 *
 * @see [[JsonOutput]]
 */
@FunctionalInterface
trait JsonInput[T]:
  /** Converts JSON value. */
  def read(value: JsonValue): T
