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

private object DefaultPrinter extends JsonPrinter:
  def write(name: String, value: JsonValue, depth: Int)(using writer: BufferedWriter): Unit =
    writer.write(EncodedString(name))
    writer.write(':')
    writer.write(value.toString)

  def write(value: JsonValue, depth: Int)(using writer: BufferedWriter): Unit =
    writer.write(value.toString)

  def writeStart(name: String, start: Char, depth: Int)(using writer: BufferedWriter): Unit =
    writer.write(EncodedString(name))
    writer.write(':')
    writer.write(start)

  def writeStart(start: Char, depth: Int)(using writer: BufferedWriter): Unit =
    writer.write(start)

  def writeEnd(end: Char, isEmpty: Boolean, depth: Int)(using writer: BufferedWriter): Unit =
    writer.write(end)
