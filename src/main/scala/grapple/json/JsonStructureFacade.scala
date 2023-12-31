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

  def keys = json.asInstanceOf[JsonObject].keys
  def fields = json.asInstanceOf[JsonObject].fields
  def apply(key: String) = json.asInstanceOf[JsonObject].apply(key)
  def get(key: String) = json.asInstanceOf[JsonObject].get(key)
  def updated(key: String, value: JsonValue) = json.asInstanceOf[JsonObject].updated(key, value)
  def removed(key: String) = json.asInstanceOf[JsonObject].removed(key)

  @targetName("concat")
  def ++(other: JsonObject) = json.asInstanceOf[JsonObject].++(other)

  def values = json.asInstanceOf[JsonArray].values
  def apply(index: Int) = json.asInstanceOf[JsonArray].apply(index)
  def updated(index: Int, value: JsonValue) = json.asInstanceOf[JsonArray].updated(index, value)
  def removed(index: Int) = json.asInstanceOf[JsonArray].removed(index)

  @targetName("concat")
  def ++(suffix: JsonArray) = json.asInstanceOf[JsonArray].++(suffix)

  @targetName("prepend")
  def +:(value: JsonValue) = json.asInstanceOf[JsonArray].+:(value)

  @targetName("append")
  def :+(value: JsonValue) = json.asInstanceOf[JsonArray].:+(value)

  override lazy val toString = json.toString
