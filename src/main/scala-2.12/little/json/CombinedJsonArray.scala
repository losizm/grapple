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

import java.{ util => jutil }

import javax.json.{ JsonArray, JsonNumber, JsonObject, JsonString, JsonValue }

import scala.collection.JavaConverters.{ asJavaCollection, asJavaIterator, asScalaBuffer, seqAsJavaList => asJavaList }

import scala.util.Try

private class CombinedJsonArray(left: JsonArray, right: JsonArray) extends JsonArray {
  private lazy val arr = asScalaBuffer(left) ++ asScalaBuffer(right)

  val getValueType = JsonValue.ValueType.ARRAY
  val isEmpty: Boolean = left.isEmpty && right.isEmpty
  val size: Int = left.size + right.size

  override lazy val toString: String =
    arr.mkString("[", ",", "]")

  def getString(index: Int): String =
    getJsonString(index).getString

  def getString(index: Int, default: String): String =
    Try(getString(index)).getOrElse(default)

  def getInt(index: Int): Int =
    getJsonNumber(index).intValue

  def getInt(index: Int, default: Int): Int =
    Try(getInt(index)).getOrElse(default)

  def getBoolean(index: Int): Boolean =
    get(index) match {
      case null             => throw new NullPointerException
      case JsonValue.TRUE   => true
      case JsonValue.FALSE  => false
      case _                => throw new ClassCastException
    }

  def getBoolean(index: Int, default: Boolean): Boolean =
    Try(getBoolean(index)).getOrElse(default)

  def getJsonString(index: Int): JsonString =
    get(index) match {
      case null  => throw new NullPointerException
      case value => value.asInstanceOf[JsonString]
    }

  def getJsonNumber(index: Int): JsonNumber =
    get(index) match {
      case null  => throw new NullPointerException
      case value => value.asInstanceOf[JsonNumber]
    }

  def getJsonArray(index: Int): JsonArray =
    get(index) match {
      case null  => throw new NullPointerException
      case value => value.asInstanceOf[JsonArray]
    }

  def getJsonObject(index: Int): JsonObject =
    get(index) match {
      case null  => throw new NullPointerException
      case value => value.asInstanceOf[JsonObject]
    }

  def isNull(index: Int): Boolean =
    get(index) match {
      case null  => throw new NullPointerException
      case value => value == JsonValue.NULL
    }

  def getValuesAs[T <: JsonValue](c: Class[T]): jutil.List[T] =
    asJavaList(arr.map(_.asInstanceOf[T]))

  def get(index: Int): JsonValue =
    if (index < 0 || index >= size)
      throw new IndexOutOfBoundsException
    else if (index < left.size)
      left.get(index)
    else
      right.get(index - left.size)

  def contains(value: Any): Boolean =
    left.contains(value) || right.contains(value)

  def containsAll(values: jutil.Collection[_]): Boolean =
    values.stream.allMatch {
      case value: JsonValue => contains(value)
      case _ => false
    }

  def indexOf(value: Any): Int =
    left.indexOf(value) match {
      case -1 =>
        right.indexOf(value) match {
          case -1    => -1
          case index => left.size + index
        }
      case index => index
    }

  def lastIndexOf(value: Any): Int =
    right.lastIndexOf(value) match {
      case -1    => left.lastIndexOf(value)
      case index => left.size + index
    }

  def iterator(): jutil.Iterator[JsonValue] = asJavaIterator(arr.iterator)
  def listIterator(): jutil.ListIterator[JsonValue] = asJavaList(arr).listIterator
  def listIterator(index: Int): jutil.ListIterator[JsonValue] = asJavaList(arr).listIterator(index)
  def subList(fromIndex: Int, toIndex: Int): jutil.List[JsonValue] = asJavaList(arr.slice(fromIndex, toIndex))
  def toArray(): Array[AnyRef] = arr.toArray[AnyRef]
  def toArray[T](buf: Array[T with AnyRef]): Array[T with AnyRef] = asJavaList(arr).toArray[T](buf)

  def clear(): Unit = throw new UnsupportedOperationException
  def set(index: Int, value: JsonValue): JsonValue = throw new UnsupportedOperationException
  def add(value: JsonValue): Boolean = throw new UnsupportedOperationException
  def add(index: Int, value: JsonValue): Unit = throw new UnsupportedOperationException
  def addAll(arr: jutil.Collection[_ <: JsonValue]): Boolean = throw new UnsupportedOperationException
  def addAll(index: Int, arr: jutil.Collection[_ <: JsonValue]): Boolean = throw new UnsupportedOperationException
  def remove(index: Int): JsonValue = throw new UnsupportedOperationException
  def remove(value: Any): Boolean = throw new UnsupportedOperationException
  def removeAll(values: jutil.Collection[_]): Boolean = throw new UnsupportedOperationException
  def retainAll(values: jutil.Collection[_]): Boolean = throw new UnsupportedOperationException
}
