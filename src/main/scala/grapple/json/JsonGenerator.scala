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
 * Defines JSON generator.
 *
 * {{{
 * import java.io.StringWriter
 *
 * import scala.language.implicitConversions
 *
 * import grapple.json.{ *, given }
 *
 * val buf = StringWriter()
 * val out = JsonGenerator(buf)
 *
 * try
 *   out.writeStartObject()          // start root object
 *   out.write("id", 1000)
 *   out.write("name", "lupita")
 *   out.writeStartArray("groups")   // start nested array
 *   out.write("lupita")
 *   out.write("admin")
 *   out.write("sudoer")
 *   out.writeEnd()                  // end nested array
 *   out.writeStartObject("info")    // start nested object
 *   out.write("home", "/home/lupita")
 *   out.write("storage", "8 GiB")
 *   out.writeEnd()                  // end nested object
 *   out.writeEnd()                  // end root object
 *   out.flush()
 *
 *   val json = Json.parse(buf.toString)
 *   assert { json("id") == JsonNumber(1000) }
 *   assert { json("name") == JsonString("lupita") }
 *   assert { json("groups") == Json.arr("lupita", "admin", "sudoer") }
 *   assert { json("info") == Json.obj("home" -> "/home/lupita", "storage" -> "8 GiB") }
 * finally
 *   out.close()
 * }}}
 *
 * @see [[JsonWriter]]
 */
trait JsonGenerator extends AutoCloseable:
  /** Writes opening brace to start object context. */
  def writeStartObject(): this.type

  /**
   * Writes field declaration and opening brace to start object context.
   *
   * @param key object key
   */
  def writeStartObject(key: String): this.type

  /** Writes opening bracket to start array context. */
  def writeStartArray(): this.type

  /**
   * Writes field declaration and opening bracket to start array context.
   *
   * @param key object key
   */
  def writeStartArray(key: String): this.type

  /** Writes closing brace or bracket based on current context. */
  def writeEnd(): this.type

  /** Writes field with null value to object context. */
  def writeNull(key: String): this.type =
    write(key, JsonNull)

  /** Writes field to object context. */
  def write(key: String, value: String): this.type =
    write(key, JsonString(value))

  /** Writes field to object context. */
  def write(key: String, value: Boolean): this.type =
    write(key, JsonBoolean(value))

  /** Writes field to object context. */
  def write(key: String, value: Int): this.type =
    write(key, JsonNumber(value))

  /** Writes field to object context. */
  def write(key: String, value: Long): this.type =
    write(key, JsonNumber(value))

  /** Writes field to object context. */
  def write(key: String, value: Float): this.type =
    write(key, JsonNumber(value))

  /** Writes field to object context. */
  def write(key: String, value: Double): this.type =
    write(key, JsonNumber(value))

  /** Writes field to object context. */
  def write(key: String, value: BigInt): this.type =
    write(key, JsonNumber(value))

  /** Writes field to object context. */
  def write(key: String, value: BigDecimal): this.type =
    write(key, JsonNumber(value))

  /** Writes field to object context. */
  def write(key: String, value: JsonValue): this.type

  /** Writes null value to array context. */
  def writeNull(): this.type =
    write(JsonNull)

  /** Writes value to array context. */
  def write(value: String): this.type =
    write(JsonString(value))

  /** Writes value to array context. */
  def write(value: Boolean): this.type =
    write(JsonBoolean(value))

  /** Writes value to array context. */
  def write(value: Int): this.type =
    write(JsonNumber(value))

  /** Writes value to array context. */
  def write(value: Long): this.type =
    write(JsonNumber(value))

  /** Writes value to array context. */
  def write(value: Float): this.type =
    write(JsonNumber(value))

  /** Writes value to array context. */
  def write(value: Double): this.type =
    write(JsonNumber(value))

  /** Writes value to array context. */
  def write(value: BigInt): this.type =
    write(JsonNumber(value))

  /** Writes value to array context. */
  def write(value: BigDecimal): this.type =
    write(JsonNumber(value))

  /** Writes value to array context. */
  def write(value: JsonValue): this.type

  /** Flushes writer. */
  def flush(): Unit

  /** Closes writer. */
  def close(): Unit

/** Provides JSON generator factory. */
object JsonGenerator:
  /**
   * Creates JSON generator to output using pretty printing with specified
   * indent.
   */
  def apply(output: Writer, indent: String): JsonGenerator =
    JsonGeneratorImpl(output, PrettyPrinter(indent))

  /** Creates JSON generator to output. */
  def apply(output: Writer): JsonGenerator =
    JsonGeneratorImpl(output, DefaultPrinter)

  /**
   * Creates JSON generator to output using pretty printing with specified
   * indent.
   */
  def apply(output: OutputStream, indent: String): JsonGenerator =
    JsonGeneratorImpl(OutputStreamWriter(output), PrettyPrinter(indent))

  /** Creates JSON generator to output. */
  def apply(output: OutputStream): JsonGenerator =
    JsonGeneratorImpl(OutputStreamWriter(output), DefaultPrinter)

  /**
   * Creates JSON generator to output using pretty printing with specified
   * indent.
   */
  def apply(output: File, indent: String): JsonGenerator =
    JsonGeneratorImpl(FileWriter(output), PrettyPrinter(indent))

  /** Creates JSON generator to output. */
  def apply(output: File): JsonGenerator =
    JsonGeneratorImpl(FileWriter(output), DefaultPrinter)

  /**
   * Creates JSON generator to output using pretty printing with specified
   * indent.
   */
  def apply(output: Path, indent: String): JsonGenerator =
    JsonGeneratorImpl(FileWriter(output.toFile), PrettyPrinter(indent))

  /** Creates JSON generator to output. */
  def apply(output: Path): JsonGenerator =
    JsonGeneratorImpl(FileWriter(output.toFile), DefaultPrinter)
