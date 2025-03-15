/*
 * Copyright 2023 Carlos Conyers
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

/**
 * Assumes either JSON object or JSON array.
 *
 * @note A structure facade is created by conversion only.
 *
 * @see [[jsonStructureFacadeConversion]]
 */
class JsonStructureFacade private[json] (json: JsonStructure) extends JsonObject, JsonArray:
  def size = json.size

  def keys = expect[JsonObject](json).keys
  def fields = expect[JsonObject](json).fields
  def apply(key: String) = expect[JsonObject](json).apply(key)
  def get(key: String) = expect[JsonObject](json).get(key)
  def updated(key: String, value: JsonValue) = expect[JsonObject](json).updated(key, value)
  def removed(key: String) = expect[JsonObject](json).removed(key)

  @targetName("concat")
  def ++(other: JsonObject) = expect[JsonObject](json).++(other)

  def values = expect[JsonArray](json).values
  def apply(index: Int) = expect[JsonArray](json).apply(index)
  def updated(index: Int, value: JsonValue) = expect[JsonArray](json).updated(index, value)
  def removed(index: Int) = expect[JsonArray](json).removed(index)

  @targetName("concat")
  def ++(suffix: JsonArray) = expect[JsonArray](json).++(suffix)

  @targetName("prepend")
  def +:(value: JsonValue) = expect[JsonArray](json).+:(value)

  @targetName("append")
  def :+(value: JsonValue) = expect[JsonArray](json).:+(value)

  /** Unwraps underlying JSON structure. */
  def unwrap: JsonStructure = json

  override lazy val toString = json.toString
