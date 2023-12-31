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

import scala.collection.immutable.{ SeqMap as ImmutableSeqMap }
import scala.collection.mutable.{ SeqMap as MutableSeqMap }

/**
 * Defines JSON object builder.
 *
 * {{{
 * import scala.language.implicitConversions
 *
 * import grapple.json.{ *, given }
 *
 * val user = JsonObjectBuilder()
 *   .add("id", 1000)
 *   .add("name", "lupita")
 *   .add("groups", Set("lupita", "sudoer"))
 *   .toJsonObject()
 *
 * assert { user("id").as[Int] == 1000 }
 * assert { user("name").as[String] == "lupita" }
 * assert { user("groups").as[Set[String]] == Set("lupita", "sudoer") }
 * }}}
 *
 * @see [[JsonObject]], [[JsonArrayBuilder]]
 */
class JsonObjectBuilder:
  private val values = MutableSeqMap[String, JsonValue]()

  /**
   * Adds field with null value to JSON object.
   *
   * @return this builder
   */
  def addNull(key: String): this.type =
    if key == null then
      throw NullPointerException()
    values += key -> JsonNull
    this

  /**
   * Adds field to JSON object.
   *
   * @return this builder
   */
  def add(key: String, value: String): this.type =
    if key == null || value == null then
      throw NullPointerException()
    values += key -> JsonString(value)
    this

  /**
   * Adds field to JSON object.
   *
   * @return this builder
   */
  def add(key: String, value: Boolean): this.type =
    if key == null then
      throw NullPointerException()
    values += key -> JsonBoolean(value)
    this

  /**
   * Adds field to JSON object.
   *
   * @return this builder
   */
  def add(key: String, value: Int): this.type =
    if key == null then
      throw NullPointerException()
    values += key -> JsonNumber(value)
    this

  /**
   * Adds field to JSON object.
   *
   * @return this builder
   */
  def add(key: String, value: Long): this.type =
    if key == null then
      throw NullPointerException()
    values += key -> JsonNumber(value)
    this

  /**
   * Adds field to JSON object.
   *
   * @return this builder
   */
  def add(key: String, value: Float): this.type =
    if key == null then
      throw NullPointerException()
    values += key -> JsonNumber(value)
    this

  /**
   * Adds field to JSON object.
   *
   * @return this builder
   */
  def add(key: String, value: Double): this.type =
    if key == null then
      throw NullPointerException()
    values += key -> JsonNumber(value)
    this

  /**
   * Adds field to JSON object.
   *
   * @return this builder
   */
  def add(key: String, value: BigInt): this.type =
    if key == null || value == null then
      throw NullPointerException()
    values += key -> JsonNumber(value)
    this

  /**
   * Adds field to JSON object.
   *
   * @return this builder
   */
  def add(key: String, value: BigDecimal): this.type =
    if key == null || value == null then
      throw NullPointerException()
    values += key -> JsonNumber(value)
    this

  /**
   * Adds field to JSON object.
   *
   * @return this builder
   */
  def add(key: String, value: JsonValue): this.type =
    if key == null || value == null then
      throw NullPointerException()
    values += key -> value
    this

  /** Builds JSON object. */
  def toJsonObject(): JsonObject =
    val obj = JsonObjectImpl(values.to(ImmutableSeqMap))
    values.clear()
    obj
