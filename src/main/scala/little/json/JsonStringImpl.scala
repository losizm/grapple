/*
 * Copyright 2019 Carlos Conyers
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

import javax.json.{ JsonString, JsonValue }

private case class JsonStringImpl(value: String) extends JsonString {
  val getValueType: JsonValue.ValueType = JsonValue.ValueType.STRING
  val getChars: CharSequence = value
  val getString: String = value

  override lazy val toString: String = {
    val buf = new StringBuilder(value.length + 16)
    buf += '"'

    value.foreach {
      case c if c >= 0x20 && c <= 0x10ffff =>
        if (c == '"' || c == '\\')
          buf += '\\'
        buf += c

      case '\t' => buf += '\\' += 't'
      case '\r' => buf += '\\' += 'r'
      case '\n' => buf += '\\' += 'n'
      case '\f' => buf += '\\' += 'f'
      case '\b' => buf += '\\' += 'b'

      case c =>
        val hex = c.toHexString
        val pad = "0" * (4 - hex.length)
        buf ++= "\\u" ++= pad ++= hex
    }

    buf += '"'
    buf.toString
  }
}
