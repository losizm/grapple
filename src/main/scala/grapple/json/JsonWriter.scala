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

import java.io.{ File, FileWriter, OutputStream, OutputStreamWriter, Writer }
import java.nio.file.Path

/**
 * Defines JSON writer.
 *
 * {{{
 * import java.io.StringWriter
 *
 * import scala.language.implicitConversions
 *
 * import grapple.json.*
 * import grapple.json.Implicits.given
 *
 * val buf = StringWriter()
 * val out = JsonWriter(buf)
 *
 * try
 *   val user = Json.obj("id" -> 1000, "name" -> "lupita")
 *   out.write(user)
 *
 *   val json = Json.parse(buf.toString)
 *   assert { json == user }
 * finally
 *   out.close()
 * }}}
 *
 * @see [[JsonGenerator]], [[JsonReader]]
 */
trait JsonWriter extends AutoCloseable:
  /**
   * Writes JSON structure.
   *
   * @param value JSON structure
   */
  def write(value: JsonStructure): this.type

  /** Closes writer. */
  def close(): Unit

/** Provides JSON writer factory. */
object JsonWriter:
  /**
   * Creates JSON writer to output using pretty printing with specified
   * indent.
   */
  def apply(output: Writer, indent: String): JsonWriter =
    JsonWriterImpl(output, PrettyPrinter(indent))

  /** Creates JSON writer to output. */
  def apply(output: Writer): JsonWriter =
    JsonWriterImpl(output, DefaultPrinter)

  /**
   * Creates JSON writer to output using pretty printing with specified
   * indent.
   */
  def apply(output: OutputStream, indent: String): JsonWriter =
    JsonWriterImpl(OutputStreamWriter(output), PrettyPrinter(indent))

  /** Creates JSON writer to output. */
  def apply(output: OutputStream): JsonWriter =
    JsonWriterImpl(OutputStreamWriter(output), DefaultPrinter)

  /**
   * Creates JSON writer to output using pretty printing with specified
   * indent.
   */
  def apply(output: File, indent: String): JsonWriter =
    JsonWriterImpl(FileWriter(output), PrettyPrinter(indent))

  /** Creates JSON writer to output. */
  def apply(output: File): JsonWriter =
    JsonWriterImpl(FileWriter(output), DefaultPrinter)

  /**
   * Creates JSON writer to output using pretty printing with specified
   * indent.
   */
  def apply(output: Path, indent: String): JsonWriter =
    JsonWriterImpl(FileWriter(output.toFile), PrettyPrinter(indent))

  /** Creates JSON writer to output. */
  def apply(output: Path): JsonWriter =
    JsonWriterImpl(FileWriter(output.toFile), DefaultPrinter)
