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

import java.util.{ Collections, HashSet => JHashSet, LinkedList => JLinkedList, Map => JMap }
import java.util.AbstractMap.SimpleImmutableEntry

import javax.json.{ JsonArray, JsonNumber, JsonObject, JsonString, JsonValue }

import scala.collection.JavaConverters.asScalaSet
import scala.util.Try

private class MergedJsonObject(left: JsonObject, right: JsonObject) extends JsonObject {
  val getValueType = JsonValue.ValueType.OBJECT

  lazy val isEmpty: Boolean =
    right.isEmpty && left.isEmpty

  lazy val keySet = {
    val keys = new JHashSet(left.keySet)
    keys.addAll(right.keySet)
    Collections.unmodifiableSet(keys)
  }

  lazy val entrySet = {
    val entries = new JHashSet[JMap.Entry[String, JsonValue]]
    keySet.forEach { key =>
      entries.add(new SimpleImmutableEntry(key, get(key)))
    }
    Collections.unmodifiableSet(entries)
  }

  lazy val values = {
    val list = new JLinkedList[JsonValue]
    entrySet.forEach(entry => list.add(entry.getValue))
    Collections.unmodifiableCollection(list)
  }

  lazy val size: Int = keySet.size

  override lazy val toString: String =
    asScalaSet(entrySet).mkString("{", ",", "}")

  def getString(name: String): String =
    getJsonString(name).getString

  def getString(name: String, default: String): String =
    Try(getString(name)).getOrElse(default)

  def getInt(name: String): Int =
    getJsonNumber(name).intValue

  def getInt(name: String, default: Int): Int =
    Try(getInt(name)).getOrElse(default)

  def getBoolean(name: String): Boolean =
    get(name) match {
      case null            => throw new NullPointerException
      case JsonValue.TRUE  => true
      case JsonValue.FALSE => false
      case _               => throw new ClassCastException
    }

  def getBoolean(name: String, default: Boolean): Boolean =
    Try(getBoolean(name)).getOrElse(default)

  def getJsonString(name: String): JsonString =
    get(name) match {
      case null  => throw new NullPointerException
      case value => value.asInstanceOf[JsonString]
    }

  def getJsonNumber(name: String): JsonNumber =
    get(name) match {
      case null  => throw new NullPointerException
      case value => value.asInstanceOf[JsonNumber]
    }

  def getJsonArray(name: String): JsonArray =
    get(name) match {
      case null  => throw new NullPointerException
      case value => value.asInstanceOf[JsonArray]
    }

  def getJsonObject(name: String): JsonObject =
    get(name) match {
      case null  => throw new NullPointerException
      case value => value.asInstanceOf[JsonObject]
    }

  def isNull(name: String): Boolean =
    get(name) match {
      case null  => throw new NullPointerException
      case value => value == JsonValue.NULL
    }

  def get(key: Any): JsonValue =
    right.getOrDefault(key, left.get(key))

  def containsKey(key: Any): Boolean =
    right.containsKey(key) || left.containsKey(key)

  def containsValue(value: Any): Boolean =
    values.contains(value)

  def clear(): Unit = throw new UnsupportedOperationException
  def put(key: String, value: JsonValue): JsonValue = throw new UnsupportedOperationException
  def putAll(obj: JMap[_ <: String, _ <: JsonValue]): Unit = throw new UnsupportedOperationException
  def remove(key: Any): JsonValue = throw new UnsupportedOperationException
}
