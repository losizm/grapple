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

import java.io.{ EOFException, File, FileReader, InputStream, InputStreamReader, Reader, StringReader }
import java.nio.file.Path

/**
 * Defines JSON reader.
 *
 * {{{
 * import scala.language.implicitConversions
 *
 * import grapple.json.{ *, given }
 *
 * val in = JsonReader("""{ "id": 1000, "name": "lupita" }""")
 *
 * try
 *   val user = in.read()
 *   assert { user("id").as[Int] == 1000 }
 *   assert { user("name").as[String] == "lupita" }
 * finally
 *   in.close()
 * }}}
 *
 * @see [[JsonParser]], [[JsonWriter]]
 */
trait JsonReader extends AutoCloseable:
  /** Reads JSON structure. */
  def read(): JsonStructure

  /** Closes reader. */
  def close(): Unit

/** Provides JSON reader factory. */
object JsonReader:
  /** Creates JSON reader from text. */
  def apply(text: String): JsonReader =
    JsonReaderImpl(StringReader(text))

  /** Creates JSON reader from bytes. */
  def apply(bytes: Array[Byte]): JsonReader =
    JsonReaderImpl(StringReader(String(bytes)))

  /** Creates JSON reader from input. */
  def apply(input: Reader): JsonReader =
    JsonReaderImpl(input)

  /** Creates JSON reader from input. */
  def apply(input: InputStream): JsonReader =
    JsonReaderImpl(InputStreamReader(input))

  /** Creates JSON reader from input. */
  def apply(input: File): JsonReader =
    JsonReaderImpl(FileReader(input))

  /** Creates JSON reader from input. */
  def apply(input: Path): JsonReader =
    JsonReaderImpl(FileReader(input.toFile))
