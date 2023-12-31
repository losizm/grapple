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
 * Defines JSON parser.
 *
 * {{{
 * import scala.language.implicitConversions
 *
 * import grapple.json.{ *, given }
 * import grapple.json.JsonParser.Event
 *
 * val parser = JsonParser("""{ "id": 1000, "name": "lupita", "groups": ["lupita", "admin"] }""")
 *
 * try
 *   // Get first event (start root object)
 *   assert { parser.next() == Event.StartObject }
 *
 *   // Get key and value
 *   assert { parser.next() == Event.Key("id") }
 *   assert { parser.next() == Event.Value(1000) }
 *
 *   // Get key and value
 *   assert { parser.next() == Event.Key("name") }
 *   assert { parser.next() == Event.Value("lupita") }
 *
 *   // Get key and value
 *   assert { parser.next() == Event.Key("groups") }
 *   assert { parser.next() == Event.StartArray } // start nested array
 *   assert { parser.next() == Event.Value("lupita") }
 *   assert { parser.next() == Event.Value("admin") }
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

  /**
   * Parses and collects remainder of current JSON object.
   *
   * {{{
   * import scala.language.implicitConversions
   *
   * import grapple.json.{ Json, JsonParser, given }
   * import grapple.json.JsonParser.Event
   *
   * val parser = JsonParser("""{ "id": 1000, "name": "lupita" }""")
   *
   * assert { parser.next() == Event.StartObject }
   * assert { parser.getObject() == Json.obj("id" -> 1000, "name" -> "lupita") }
   * }}}
   *
   * @note Parser must be in object context.
   */
  def getObject(): JsonObject

  /**
   * Parses and collects remainder of current JSON array.
   *
   * {{{
   * import scala.language.implicitConversions
   *
   * import grapple.json.{ Json, JsonParser, given }
   * import grapple.json.JsonParser.Event
   *
   * val parser = JsonParser("""[1000, "lupita"]""")
   *
   * assert { parser.next() == Event.StartArray }
   * assert { parser.getArray() == Json.arr(1000, "lupita") }
   * }}}
   *
   * @note Parser must be in array context.
   */
  def getArray(): JsonArray

  /** Closes parser. */
  def close(): Unit

/** Provides JSON parser factory and other utilities. */
object JsonParser:
  /** Defines enumeration of parser events. */
  enum Event:
    /** Signals start of object. */
    case StartObject extends Event

    /** Signals start of array. */
    case StartArray extends Event

    /**
     * Signals key.
     *
     * @param get parsed key
     */
    case Key(get: String) extends Event

    /**
     * Signals value.
     *
     * @param get parsed value
     */
    case Value(get: JsonValue) extends Event

    /** Signals end of object. */
    case EndObject extends Event

    /** Signals end of array. */
    case EndArray extends Event

  /** Creates JSON parser from input. */
  def apply(input: Reader): JsonParser =
    JsonParserImpl(input)

  /** Creates JSON parser from input. */
  def apply(input: String): JsonParser =
    JsonParserImpl(StringReader(input))

  /** Creates JSON parser from input. */
  def apply(input: Array[Byte]): JsonParser =
    JsonParserImpl(InputStreamReader(ByteArrayInputStream(input)))

  /** Creates JSON parser from input. */
  def apply(input: Array[Byte], offset: Int, length: Int): JsonParser =
    JsonParserImpl(InputStreamReader(ByteArrayInputStream(input, offset, length)))

  /** Creates JSON parser from input. */
  def apply(input: InputStream): JsonParser =
    JsonParserImpl(InputStreamReader(input))

  /** Creates JSON parser from input. */
  def apply(input: File): JsonParser =
    JsonParserImpl(FileReader(input))

  /** Creates JSON parser from input. */
  def apply(input: Path): JsonParser =
    JsonParserImpl(FileReader(input.toFile))
