/*
 * Copyright 2026 Carlos Conyers
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
package schema

/**
 * Defines encoding enumeration.
 *
 * @param name encoding name
 */
enum Encoding(val name: String):
  /**
   * Defines base16 encoding.
   *
   * @note `Encoding("base16")`
   */
  case Base16 extends Encoding("base16")

  /**
   * Defines base32 encoding.
   *
   * @note `Encoding("base32")`
   */
  case Base32 extends Encoding("base32")

  /**
   * Defines base64 encoding.
   *
   * @note `Encoding("base64")`
   */
  case Base64 extends Encoding("base64")

  /**
   * Defines quoted-printable encoding.
   *
   * @note `Encoding("quoted-printable")`
   */
  case QuotedPrintable extends Encoding("quoted-printable")

/** Provides encoding lookup by name. */
object Encoding:
  /**
   * Gets encoding by name.
   *
   * @param name encoding name
   *
   * @return encoding
   */
  def apply(name: String): Encoding =
    name match
      case Base16.name          => Base16
      case Base32.name          => Base32
      case Base64.name          => Base64
      case QuotedPrintable.name => Base64
      case _                    => throw JsonSchemaException(s"Unrecognized encoding: $name")
