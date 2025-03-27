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

import java.io.{ EOFException, Reader }

import JsonParser.Event

private class JsonReaderImpl(input: Reader) extends JsonReader:
  private val parser = JsonParserImpl(input)

  def read(): JsonStructure =
    try
      parser.next(true) match
        case Event.StartObject => readObject()
        case Event.StartArray  => readArray()
        case event             => throw JsonException(s"Unexpected event: $event")
    catch
        case e: EOFException   => throw JsonException("Unexpected end of input", e)

  def close(): Unit = parser.close()

  private def readObject(): JsonObject =
    val builder = JsonObjectBuilder()

    var event = parser.next()
    while event != Event.EndObject do
      val key = getKey(event)
      parser.next() match
        case Event.StartObject => builder.add(key, readObject())
        case Event.StartArray  => builder.add(key, readArray())
        case event             => builder.add(key, getValue(event))
      event = parser.next()

    builder.toJsonObject()

  private def readArray(): JsonArray =
    val builder = JsonArrayBuilder()

    var event = parser.next()
    while event != Event.EndArray do
      event match
        case Event.StartObject => builder.add(readObject())
        case Event.StartArray  => builder.add(readArray())
        case _                 => builder.add(getValue(event))
      event = parser.next()

    builder.toJsonArray()

  private inline def getKey(event: Event): String =
    event match
      case Event.Key(key) => key
      case _              => throw JsonException(s"Unexpected event: $event")

  private inline def getValue(event: Event): JsonValue =
    event match
      case Event.Value(value) => value
      case _                  => throw JsonException(s"Unexpected event: $event")
