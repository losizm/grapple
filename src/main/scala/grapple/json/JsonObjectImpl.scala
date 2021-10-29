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

  lazy val names = fields.keys.toSeq
  lazy val size  = fields.size

  def apply(name: String) =
    fields(name)

  def get(name: String) =
    fields.get(name)

  @targetName("concat")
  def ++(other: JsonObject): JsonObject =
    if other == null then
      throw NullPointerException()
    JsonObjectImpl(fields ++ other.fields)

  @targetName("updated")
  def +(field: (String, JsonValue)): JsonObject =
    field match
      case (null, _    ) => throw NullPointerException()
      case (name, null ) => JsonObjectImpl(fields + (name -> JsonNull))
      case (name, value) => JsonObjectImpl(fields + (name -> value))

  @targetName("removed")
  def -(name: String): JsonObject =
    if name == null then
      throw NullPointerException()
    JsonObjectImpl(fields - name)

  override lazy val toString =
    fields.map((name, value) => s"${EncodedString(name)}:$value")
      .mkString("{", ",", "}")
