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

import scala.collection.mutable.ListBuffer

/**
 * Defines JSON array builder.
 *
 * {{{
 * import scala.language.implicitConversions
 *
 * import grapple.json.{ *, given }
 *
 * val user = JsonArrayBuilder()
 *   .add(1000)
 *   .add("lupita")
 *   .add(Set("lupita", "sudoer"))
 *   .toJsonArray()
 *
 * assert { user(0).as[Int] == 1000 }
 * assert { user(1).as[String] == "lupita" }
 * assert { user(2).as[Set[String]] == Set("lupita", "sudoer") }
 * }}}
 *
 * @see [[JsonArray]], [[JsonObjectBuilder]]
 */
class JsonArrayBuilder:
  private val values = ListBuffer[JsonValue]()

  /**
   * Adds value to JSON array.
   *
   * @return this builder
   */
  def add(value: JsonValue): this.type =
    if value == null then
      throw NullPointerException()
    values += value
    this

  /**
   * Adds value to JSON array.
   *
   * @return this builder
   */
  def add(value: String): this.type =
    if value == null then
      throw NullPointerException()
    values += JsonString(value)
    this

  /**
   * Adds value to JSON array.
   *
   * @return this builder
   */
  def add(value: Byte): this.type =
    values += JsonNumber(value)
    this

  /**
   * Adds value to JSON array.
   *
   * @return this builder
   */
  def add(value: Short): this.type =
    values += JsonNumber(value)
    this

  /**
   * Adds value to JSON array.
   *
   * @return this builder
   */
  def add(value: Int): this.type =
    values += JsonNumber(value)
    this

  /**
   * Adds value to JSON array.
   *
   * @return this builder
   */
  def add(value: Long): this.type =
    values += JsonNumber(value)
    this

  /**
   * Adds value to JSON array.
   *
   * @return this builder
   */
  def add(value: Float): this.type =
    values += JsonNumber(value)
    this

  /**
   * Adds value to JSON array.
   *
   * @return this builder
   */
  def add(value: Double): this.type =
    values += JsonNumber(value)
    this

  /**
   * Adds value to JSON array.
   *
   * @return this builder
   */
  def add(value: BigInt): this.type =
    if value == null then
      throw NullPointerException()
    values += JsonNumber(value)
    this

  /**
   * Adds value to JSON array.
   *
   * @return this builder
   */
  def add(value: BigDecimal): this.type =
    if value == null then
      throw NullPointerException()
    values += JsonNumber(value)
    this

  /**
   * Adds value to JSON array.
   *
   * @return this builder
   */
  def add(value: Boolean): this.type =
    values += JsonBoolean(value)
    this

  /**
   * Adds null value to JSON array.
   *
   * @return this builder
   */
  def addNull(): this.type =
    values += JsonNull
    this

  /** Builds JSON array. */
  def toJsonArray(): JsonArray =
    val arr = JsonArrayImpl(values.toSeq)
    values.clear()
    arr
