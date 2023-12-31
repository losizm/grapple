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

import scala.annotation.targetName

private case class JsonObjectImpl(fields: Map[String, JsonValue]) extends JsonObject:
  if fields == null then
    throw NullPointerException()

  lazy val keys = fields.keys.toSet
  lazy val size = fields.size

  def apply(key: String) =
    fields(key)

  def get(key: String) =
    fields.get(key)

  def updated(key: String, value: JsonValue): JsonObject =
    (key, value) match
      case (null, _    ) => throw NullPointerException()
      case (key,  null ) => JsonObjectImpl(fields.updated(key, JsonNull))
      case (key,  value) => JsonObjectImpl(fields.updated(key, value))

  def removed(key: String): JsonObject =
    if key == null then
      throw NullPointerException()
    JsonObjectImpl(fields.removed(key))

  @targetName("concat")
  def ++(other: JsonObject): JsonObject =
    if other == null then
      throw NullPointerException()
    JsonObjectImpl(fields ++ other.fields)

  override lazy val toString =
    fields.map((key, value) => s"${EncodedString(key)}:$value")
      .mkString("{", ",", "}")
