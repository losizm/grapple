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

import java.io.*
import java.nio.file.Path

/**
 * Provides JSON utilities.
 *
 * {{{
 * import scala.language.implicitConversions
 *
 * import grapple.json.{ *, given }
 *
 * // Create JSON object
 * val user = Json.obj("id" -> 1000, "name" -> "lupita")
 *
 * // Create JSON array
 * val info = Json.arr(user, "/home/lupita", 8L * 1024 * 1024 * 1024)
 *
 * // Parse JSON text
 * val root = Json.parse("""{ "id": 0, "name": "root" }""")
 *
 * case class User(id: Int, name: String)
 *
 * given userOutput: JsonOutput[User] with
 *   def write(u: User) = Json.obj("id" -> u.id, "name" -> u.name)
 *
 * // Convert value to JSON object
 * val nobody = Json.toJson(User(65534, "nobody"))
 * }}}
 */
object Json:
  /** Creates JSON object with supplied fields. */
  def obj(fields: (String, JsonValue)*): JsonObject =
    JsonObject(fields)

  /** Creates JSON array with supplied values. */
  def arr(values: JsonValue*): JsonArray =
    JsonArray(values)

  /**
   * Parses JSON structure from input.
   *
   * @throws JsonParserError if input cannot be parsed to JSON structure
   */
  def parse(input: String): JsonStructure =
    val reader = JsonReader(input)
    try reader.read() finally reader.close()

  /**
   * Parses JSON structure from input.
   *
   * @throws JsonParserError if input cannot be parsed to JSON structure
   */
  def parse(input: Array[Byte]): JsonStructure =
    val reader = JsonReader(input)
    try reader.read() finally reader.close()

  /**
   * Parses JSON structure from input.
   *
   * @throws JsonParserError if input cannot be parsed to JSON structure
   */
  def parse(input: Array[Byte], offset: Int, length: Int): JsonStructure =
    val reader = JsonReader(input, offset, length)
    try reader.read() finally reader.close()

  /**
   * Parses JSON structure from input.
   *
   * @note Closes input on return.
   *
   * @throws JsonParserError if input cannot be parsed to JSON structure
   */
  def parse(input: Reader): JsonStructure =
    val reader = JsonReader(input)
    try reader.read() finally reader.close()

  /**
   * Parses JSON structure from input.
   *
   * @note Closes input on return.
   *
   * @throws JsonParserError if input cannot be parsed to JSON structure
   */
  def parse(input: InputStream): JsonStructure =
    val reader = JsonReader(input)
    try reader.read() finally reader.close()

  /**
   * Parses JSON structure from input.
   *
   * @throws JsonParserError if input cannot be parsed to JSON structure
   */
  def parse(input: File): JsonStructure =
    val reader = JsonReader(input)
    try reader.read() finally reader.close()

  /**
   * Parses JSON structure from input.
   *
   * @throws JsonParserError if input cannot be parsed to JSON structure
   */
  def parse(input: Path): JsonStructure =
    val reader = JsonReader(input)
    try reader.read() finally reader.close()

  /**
   * Creates "pretty" print of JSON using 2-space indent.
   *
   * @note If value is not a JSON structure, identation is not used and the
   * output is equivalent to `value.toString()`.
   */
  def toPrettyPrint(value: JsonValue): String =
    toPrettyPrint(value, "  ")

  /**
   * Creates "pretty" print of JSON using supplied indent.
   *
   * @note If value is not a JSON structure, identation is not used and the
   * output is equivalent to `value.toString()`.
   */
  def toPrettyPrint(value: JsonValue, indent: String): String =
    value match
      case struct: JsonStructure =>
        val output = StringWriter()
        val writer = JsonWriter(output, indent)
        try
          writer.write(struct)
          output.toString
        finally
          writer.close()
      case _ => value.toString

  /** Converts JSON value to UTF-8 encoded bytes. */
  def toBytes(value: JsonValue): Array[Byte] =
    value.toString.getBytes("UTF-8")

  /**
   * Converts value to JSON value.
   *
   * @param value  value
   * @param output converter
   */
  def toJson[T](value: T)(using output: JsonOutput[T]): JsonValue =
    output.write(value)
