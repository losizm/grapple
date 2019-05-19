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

import java.io.{ File, FileReader, InputStream, OutputStream, Reader, StringReader, Writer }

import javax.json._
import javax.json.{ Json => JavaxJson }
import javax.json.stream.{ JsonGenerator, JsonParser }

import scala.util.Try

/**
 * Provides factory methods and other utilities.
 *
 * {{{
 * import javax.json.JsonObject
 * import little.json.{ Json, FromJson, ToJson }
 * import little.json.Implicits._
 *
 * case class User(id: Int, name: String)
 *
 * // Define how to convert User to JsonObject
 * implicit val userToJson: ToJson[User] = { user =>
 *   Json.obj("id" -> user.id, "name" -> user.name)
 * }
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
 *
 * // Convert User to JsonObject
 * val jsonToo = Json.toJson(user)
 * }}}
 */
object Json {
  /** Converts T value to JsonValue. */
  def toJson[T](value: T)(implicit convert: ToJson[T]): JsonValue =
    convert(value)

  /** Converts JsonValue to T value. */
  def fromJson[T](json: JsonValue)(implicit convert: FromJson[T]): T =
    convert(json)

  /** Creates JsonArray from list of values. */
  def arr(values: JsonValue*): JsonArray =
    values.foldLeft(createArrayBuilder) { (builder, value) =>
      if (value == null) builder.addNull()
      else builder.add(value)
    }.build()

  /** Creates JsonObject from list of fields. */
  def obj(fields: (String, JsonValue)*): JsonObject =
    fields.foldLeft(createObjectBuilder) { (builder, field) =>
      if (field._2 == null) builder.addNull(field._1)
      else builder.add(field._1, field._2)
    }.build()

  /** Parses given text to JsonStructure. */
  def parse(text: String): JsonStructure = {
    val in = new StringReader(text)
    try parse(in)
    finally Try(in.close())
  }

  /** Parses text from given input stream to JsonStructure. */
  def parse(in: InputStream): JsonStructure = {
    val json = createReader(in)
    try json.read()
    finally Try(json.close())
  }

  /** Parses text from given reader to JsonStructure. */
  def parse(reader: Reader): JsonStructure = {
    val json = createReader(reader)
    try json.read()
    finally Try(json.close())
  }

  /** Parses text from given file to JsonStructure. */
  def parse(file: File): JsonStructure = {
    val in = new FileReader(file)
    try parse(in)
    finally Try(in.close())
  }

  /** Creates JsonArrayBuilder. */
  def createArrayBuilder(): JsonArrayBuilder =
    JavaxJson.createArrayBuilder()

  /** Creates JsonObjectBuilder. */
  def createObjectBuilder(): JsonObjectBuilder =
    JavaxJson.createObjectBuilder()

  /** Creates JsonReader with given input stream. */
  def createReader(in: InputStream): JsonReader =
    JavaxJson.createReader(in)

  /** Creates JsonReader with given reader. */
  def createReader(reader: Reader): JsonReader =
    JavaxJson.createReader(reader)

  /** Creates JsonWriter with given output stream. */
  def createWriter(out: OutputStream): JsonWriter =
    JavaxJson.createWriter(out)

  /** Creates JsonWriter with given writer. */
  def createWriter(writer: Writer): JsonWriter =
    JavaxJson.createWriter(writer)

  /** Creates JsonParser with given input stream. */
  def createParser(in: InputStream): JsonParser =
    JavaxJson.createParser(in)

  /** Creates JsonParser with given reader. */
  def createParser(reader: Reader): JsonParser =
    JavaxJson.createParser(reader)

  /** Creates JsonGenerator with given output stream. */
  def createGenerator(out: OutputStream): JsonGenerator =
    JavaxJson.createGenerator(out)

  /** Creates JsonGenerator with given writer. */
  def createGenerator(writer: Writer): JsonGenerator =
    JavaxJson.createGenerator(writer)
}
