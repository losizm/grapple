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

import java.io.{ File, FileReader, InputStream, OutputStream, Reader, StringReader, Writer }

import javax.json._
import javax.json.{ Json => JavaxJson }
import javax.json.stream.{ JsonGenerator, JsonParser }

import scala.util.Try

/** Provides factory methods and other utilities. */
object Json {
  /** Converts value to JsonValue. */
  def toJson[T](value: T)(implicit convert: ToJson[T]): JsonValue =
    convert(value)

  /** Parses given text to JsonStructure. */
  def parse[T <: JsonStructure](text: String): T = {
    val in = new StringReader(text)
    try parse(in)
    finally Try(in.close())
  }

  /** Parses text from given input stream to JsonStructure. */
  def parse[T <: JsonStructure](in: InputStream): T = {
    val json = createReader(in)
    try json.read().asInstanceOf[T]
    finally Try(json.close())
  }

  /** Parses text from given reader to JsonStructure. */
  def parse[T <: JsonStructure](reader: Reader): T = {
    val json = createReader(reader)
    try json.read().asInstanceOf[T]
    finally Try(json.close())
  }

  /** Parses text from given file to JsonStructure. */
  def parse[T <: JsonStructure](file: File): T = {
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
