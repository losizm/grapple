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

import scala.collection.immutable.{ SeqMap as ImmutableSeqMap }
import scala.collection.mutable.{ SeqMap as MutableSeqMap }

/**
 * Defines JSON object builder.
 *
 * {{{
 * import little.json.*
 * import little.json.Implicits.given
 * import scala.language.implicitConversions
 *
 * val user = JsonObjectBuilder()
 *   .add("id", 1000)
 *   .add("name", "jza")
 *   .add("groups", Set("jza", "sudo"))
 *   .build()
 *
 * assert { user("id").as[Int] == 1000 }
 * assert { user("name").as[String] == "jza" }
 * assert { user("groups").as[Set[String]] == Set("jza", "sudo") }
 * }}}
 *
 * @see [[JsonObject]], [[JsonArrayBuilder]]
 */
class JsonObjectBuilder:
  private val values = MutableSeqMap[String, JsonValue]()

  /**
   * Adds field to JSON object.
   *
   * @return this builder
   */
  def add(name: String, value: JsonValue): this.type =
    if name == null || value == null then
      throw NullPointerException()
    values += name -> value
    this

  /**
   * Adds field with null value to JSON object.
   *
   * @return this builder
   */
  def addNull(name: String): this.type =
    if name == null then
      throw NullPointerException()
    values += name -> JsonNull
    this

  /** Builds JSON object. */
  def build(): JsonObject =
    val obj = JsonObjectImpl(values.to(ImmutableSeqMap))
    values.clear()
    obj
