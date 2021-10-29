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

private object EncodedString:
  private val unicodeSequences =
    for
      c <- 0 to Char.MaxValue
    yield
      "\\u" + ("0000" + c.toHexString).takeRight(4)

  def apply(value: String): String =
    val buffer = StringBuilder(value.length + 16)
    buffer += '"'
    value.foreach(c => encode(c, buffer))
    buffer += '"'
    buffer.toString

  def apply(value: Char): String =
    if value == '\'' then
      "(')"
    else if value >= 0x20 && value <= 0x10ffff then
      s"'$value'"
    else
      val buffer = StringBuilder(8)
      buffer += '\''
      encode(value, buffer)
      buffer += '\''
      buffer.toString

  private inline def encode(value: Char, buffer: StringBuilder): Unit =
    if value == '"' || value == '\\' then
      buffer += '\\'
      buffer += value
    else if value >= 0x20 && value <= 0x10ffff then
      buffer += value
    else
      value match
        case '\t' => buffer ++= "\\t"
        case '\r' => buffer ++= "\\r"
        case '\n' => buffer ++= "\\n"
        case '\f' => buffer ++= "\\f"
        case '\b' => buffer ++= "\\b"
        case _    => buffer ++= unicodeSequences(value)
