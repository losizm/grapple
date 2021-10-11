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

import java.io.{ File, InputStream, Reader, StringWriter }
import java.nio.file.Path

/**
 * Provides JSON utilities.
 *
 * {{{
 * import little.json.*
 * import little.json.Implicits.given
 * import scala.language.implicitConversions
 *
 * // Create JSON object
 * val user = Json.obj("id" -> 1000, "name" -> "jza")
 *
 * // Create JSON array
 * val info = Json.arr(user, "/home/jza", 8L * 1024 * 1024 * 1024)
 *
 * // Parse JSON text
 * val root = Json.parse("""{ "id": 0, "name": "root" }""")
 *
 * case class User(id: Int, name: String)
 *
 * given userToJson: JsonOutput[User] with
 *   def apply(u: User) = Json.obj("id" -> u.id, "name" -> u.name)
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

  /** Parses JSON structure from text. */
  def parse(text: String): JsonStructure =
    val reader = JsonReader(text)
    try reader.read() finally reader.close()

  /** Parses JSON structure from bytes. */
  def parse(bytes: Array[Byte]): JsonStructure =
    val reader = JsonReader(bytes)
    try reader.read() finally reader.close()

  /**
   * Parses JSON structure from input.
   *
   * @note Closes input on return.
   */
  def parse(input: Reader): JsonStructure =
    val reader = JsonReader(input)
    try reader.read() finally reader.close()

  /**
   * Parses JSON structure from input.
   *
   * @note Closes input on return.
   */
  def parse(input: InputStream): JsonStructure =
    val reader = JsonReader(input)
    try reader.read() finally reader.close()

  /** Parses JSON structure from input. */
  def parse(input: File): JsonStructure =
    val reader = JsonReader(input)
    try reader.read() finally reader.close()

  /** Parses JSON structure from input. */
  def parse(input: Path): JsonStructure =
    val reader = JsonReader(input)
    try reader.read() finally reader.close()

  /** Creates "pretty" print of JSON using 2-space indent. */
  def toPrettyPrint(json: JsonStructure): String =
    toPrettyPrint(json, "  ")

  /** Creates "pretty" print of JSON using supplied indent. */
  def toPrettyPrint(json: JsonStructure, indent: String): String =
    val output = StringWriter()
    val writer = JsonWriter(output, indent)
    try
      writer.write(json)
      output.toString
    finally
      writer.close()

  /** Converts JSON structure to UTF-8 encoded bytes. */
  def toBytes(json: JsonStructure): Array[Byte] =
    json.toString.getBytes("UTF-8")

  /**
   * Converts value to JSON value.
   *
   * @param value   value
   * @param convert output converter
   */
  def toJson[T](value: T)(using convert: JsonOutput[T]): JsonValue =
    convert(value)
