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

import java.io.BufferedWriter

private class PrettyPrinter(indent: String) extends JsonPrinter:
  if !indent.matches("[ \t\r\n]*") then
    throw IllegalArgumentException("Indent restricted to whitespace: [ \\t\\r\\n]*")

  def writeStart(name: String, start: Char, depth: Int)(using writer: BufferedWriter): Unit =
    if depth > 0 then writeIndent(depth)
    writer.write(EncodedString(name))
    writer.write(": ")
    writer.write(start)

  def write(name: String, value: JsonValue, depth: Int)(using writer: BufferedWriter): Unit =
    if depth > 0 then writeIndent(depth)
    writer.write(EncodedString(name))
    writer.write(": ")
    writeValue(value, depth)

  def writeStart(start: Char, depth: Int)(using writer: BufferedWriter): Unit =
    if depth > 0 then writeIndent(depth)
    writer.write(start)

  def write(value: JsonValue, depth: Int)(using writer: BufferedWriter): Unit =
    if depth > 0 then writeIndent(depth)
    writeValue(value, depth)

  def writeEnd(end: Char, isEmpty: Boolean, depth: Int)(using writer: BufferedWriter): Unit =
    if !isEmpty then writeIndent(depth)
    writer.write(end)

  private def writeIndent(depth: Int)(using writer: BufferedWriter): Unit =
    writer.newLine()
    writer.write(indent * depth)

  private def writeValue(value: JsonValue, depth: Int)(using writer: BufferedWriter): Unit =
    value match
      case value: JsonObject => writeObject(value, depth)
      case value: JsonArray  => writeArray(value, depth)
      case _                 => writer.write(value.toString)

  private def writeObject(json: JsonObject, depth: Int)(using writer: BufferedWriter): Unit =
    writer.write('{')

    json.fields.zipWithIndex.foreach {
      case ((name, value), index) =>
        if index > 0 then writer.write(',')
        write(name, value, depth + 1)
    }

    if !json.isEmpty then writeIndent(depth)
    writer.write('}')

  private def writeArray(json: JsonArray, depth: Int)(using writer: BufferedWriter): Unit =
    writer.write('[')

    json.values.zipWithIndex.foreach {
      case (value, index) =>
        if index > 0 then writer.write(',')
        write(value, depth + 1)
    }

    if !json.isEmpty then writeIndent(depth)
    writer.write(']')
