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

private case class JsonArrayImpl(values: Seq[JsonValue]) extends JsonArray:
  if values == null then
    throw NullPointerException()

  lazy val size = values.size
  lazy val head = values.head

  def apply(index: Int) =
    if index < 0 || index >= size then
      throw JsonArrayError(index, IndexOutOfBoundsException(index))
    values(index)

  def updated(index: Int, value: JsonValue): JsonArray =
    if index < 0 || index >= size then
      throw JsonArrayError(index, IndexOutOfBoundsException(index))
    if value == null then
      throw NullPointerException()
    JsonArrayImpl(values.updated(index, value))

  def removed(index: Int): JsonArray =
    if index < 0 || index >= size then
      throw JsonArrayError(index, IndexOutOfBoundsException(index))
    JsonArrayImpl(values.take(index) ++ values.drop(index + 1))

  @targetName("concat")
  def ++(suffix: JsonArray): JsonArray =
    if suffix == null then
      throw NullPointerException()
    JsonArrayImpl(values ++ suffix.values)

  @targetName("prepend")
  def +:(value: JsonValue): JsonArray =
    if value == null then
      throw NullPointerException()
    JsonArrayImpl(value +: values)

  @targetName("append")
  def :+(value: JsonValue): JsonArray =
    if value == null then
      throw NullPointerException()
    JsonArrayImpl(values :+ value)

  override lazy val toString = values.mkString("[", ",", "]")
