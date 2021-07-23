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

import java.io.{ File, FileReader, InputStream, InputStreamReader, Reader, StringReader }
import java.nio.file.Path

/**
 * Defines JSON parser.
 *
 * {{{
 * import little.json.*
 * import little.json.Implicits.given
 * import scala.language.implicitConversions
 *
 * import JsonParser.Event
 *
 * val parser = JsonParser("""{ "id": 1000, "name": "jza", "groups": ["jza", "adm"] }""")
 *
 * try
 *   // Get first event (start root object)
 *   assert { parser.next() == Event.StartObject }
 *
 *   // Get field name and value
 *   assert { parser.next() == Event.FieldName("id") }
 *   assert { parser.next() == Event.Value(1000) }
 *
 *   // Get field name and value
 *   assert { parser.next() == Event.FieldName("name") }
 *   assert { parser.next() == Event.Value("jza") }
 *
 *   // Get field name and value
 *   assert { parser.next() == Event.FieldName("groups") }
 *   assert { parser.next() == Event.StartArray } // start nested array
 *   assert { parser.next() == Event.Value("jza") }
 *   assert { parser.next() == Event.Value("adm") }
 *   assert { parser.next() == Event.EndArray }   // end nested array
 *
 *   // Get final event (end root object)
 *   assert { parser.next() == Event.EndObject }
 *
 *   // No more events
 *   assert { !parser.hasNext }
 * finally
 *   parser.close()
 * }}}
 *
 * @see [[JsonParser.Event]], [[JsonReader]]
 */
trait JsonParser extends Iterator[JsonParser.Event], AutoCloseable:
  /** Tests for next event. */
  def hasNext: Boolean

  /** Gets next event. */
  def next(): JsonParser.Event

  /** Closes parser. */
  def close(): Unit

/** Provides JSON parser factory and other utilities. */
object JsonParser:
  /** Defines enumeration of parser events. */
  enum Event:
    /** Indicates start of object is parsed. */
    case StartObject extends Event

    /** Indicates start of array is parsed. */
    case StartArray extends Event

    /** Indicates field name is parsed. */
    case FieldName(get: String) extends Event

    /** Indicates value is parsed. */
    case Value(get: JsonValue) extends Event

    /** Indicates end of object is parsed. */
    case EndObject extends Event

    /** Indicates end of array is parsed. */
    case EndArray extends Event

  /** Creates JSON parser from input. */
  def apply(input: Reader): JsonParser =
    JsonParserImpl(input)

  /** Creates JSON parser from text. */
  def apply(text: String): JsonParser =
    JsonParserImpl(StringReader(text))

  /** Creates JSON parser from bytes. */
  def apply(bytes: Array[Byte]): JsonParser =
    JsonParserImpl(StringReader(String(bytes)))

  def apply(input: InputStream): JsonParser =
    JsonParserImpl(InputStreamReader(input))

  /** Creates JSON parser from input. */
  def apply(input: File): JsonParser =
    JsonParserImpl(FileReader(input))

  /** Creates JSON parser from input. */
  def apply(input: Path): JsonParser =
    JsonParserImpl(FileReader(input.toFile))
