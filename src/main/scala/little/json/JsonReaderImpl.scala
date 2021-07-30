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

import java.io.{ EOFException, Reader }

private class JsonReaderImpl(input: Reader) extends JsonReader:
  import JsonParser.Event

  private val reader = JsonParser(input)

  def read(): JsonStructure =
    try
      reader.next() match
        case Event.StartObject => readObject()
        case Event.StartArray  => readArray()
        case _                 => throw JsonException("Unexpected event")
    catch
        case e: EOFException   => throw JsonException("Unexpected end of input", e)

  def close(): Unit = reader.close()

  private def readObject(): JsonObject =
    val builder = JsonObjectBuilder()

    var event = reader.next()
    while event != Event.EndObject do
      val name = getFieldName(event)
      reader.next() match
        case Event.StartObject => builder.add(name, readObject())
        case Event.StartArray  => builder.add(name, readArray())
        case event             => builder.add(name, getValue(event))
      event = reader.next()

    builder.build()

  private def readArray(): JsonArray =
    val builder = JsonArrayBuilder()

    var event = reader.next()
    while event != Event.EndArray do
      event match
        case Event.StartObject => builder.add(readObject())
        case Event.StartArray  => builder.add(readArray())
        case _                 => builder.add(getValue(event))
      event = reader.next()

    builder.build()

  private inline def getFieldName(event: Event): String =
    event.asInstanceOf[Event.FieldName].get

  private inline def getValue(event: Event): JsonValue =
    event.asInstanceOf[Event.Value].get
