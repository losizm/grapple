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

/** Represents JSON value. */
sealed trait JsonValue:
  /**
   * Converts value.
   *
   * @param input converter
   */
  final def as[T](using input: JsonInput[T]): T =
    input.read(this)

/** Represents JSON null. */
case object JsonNull extends JsonValue:
  /**
   * Gets string representation.
   *
   * @return `"null"`
   */
  override val toString = "null"

/** Represents JSON string. */
sealed trait JsonString extends JsonValue:
  /** Gets value. */
  def value: String

/** Provides JSON string factory. */
object JsonString:
  /** Creates JSON string. */
  def apply(value: String): JsonString =
    JsonStringImpl(value)

  /** Deconstructs JSON string. */
  def unapply(json: JsonString): Option[String] =
    json != null match
      case true  => Some(json.value)
      case false => None

/** Represents JSON boolean. */
sealed trait JsonBoolean extends JsonValue:
  /** Gets value. */
  def value: Boolean

/** Provides JSON boolean factory. */
object JsonBoolean:
  /** Represents JSON true. */
  case object True extends JsonBoolean:
    val value = true

    /**
     * Gets string representation.
     *
     * @return `"true"`
     */
    override val toString = "true"

  /** Represents JSON false. */
  case object False extends JsonBoolean:
    val value = false

    /**
     * Gets string representation.
     *
     * @return `"false"`
     */
    override val toString = "false"

  /** Creates JSON boolean. */
  def apply(value: Boolean): JsonBoolean =
    if value then True else False

  /** Deconstructs JSON boolean. */
  def unapply(json: JsonBoolean): Option[Boolean] =
    json != null match
      case true  => Some(json.value)
      case false => None

/** Represents JSON number. */
sealed trait JsonNumber extends JsonValue:
  /**
   * Gets value as `Int`.
   *
   * @throws java.lang.ArithmeticException if not represented exactly
   */
  def toInt: Int

  /**
   * Gets value as `Long`.
   *
   * @throws java.lang.ArithmeticException if not represented exactly
   */
  def toLong: Long

  /** Gets value as `Float`. */
  def toFloat: Float

  /** Gets value as `Double`. */
  def toDouble: Double

  /**
   * Gets value as `BigInt`.
   *
   * @throws java.lang.ArithmeticException if not represented exactly
   */
  def toBigInt: BigInt

  /** Gets value as `BigDecimal`. */
  def toBigDecimal: BigDecimal

/** Provides JSON number factory. */
object JsonNumber:
  /**
   * Creates JSON number.
   *
   * @throws NumberFormatException if value is invalid numeric representation
   */
  def apply(value: String): JsonNumber =
    JsonNumberImpl(BigDecimal(value))

  /** Creates JSON number. */
  def apply(value: Int): JsonNumber =
    JsonNumberImpl(BigDecimal(value))

  /** Creates JSON number. */
  def apply(value: Long): JsonNumber =
    JsonNumberImpl(BigDecimal(value))

  /** Creates JSON number. */
  def apply(value: Double): JsonNumber =
    JsonNumberImpl(BigDecimal(value))

  /** Creates JSON number. */
  def apply(value: BigInt): JsonNumber =
    JsonNumberImpl(BigDecimal(value))

  /** Creates JSON number. */
  def apply(value: BigDecimal): JsonNumber =
    JsonNumberImpl(value)

  /** Deconstructs JSON number. */
  def unapply(json: JsonNumber): Option[BigDecimal] =
    json != null match
      case true  => Some(json.toBigDecimal)
      case false => None

/** Represents JSON structure. */
sealed trait JsonStructure extends JsonValue:
  /** Gets size. */
  def size: Int

  /** Tests for empty. */
  def isEmpty: Boolean = size == 0

  /** Tests for non-empty. */
  def nonEmpty: Boolean = size > 0

/**
 * Represents JSON object.
 *
 * @see [[JsonObjectBuilder]]
 */
trait JsonObject private[json] extends JsonStructure:
  /** Gets object keys. */
  def keys: Set[String]

  /** Gets object fields. */
  def fields: Map[String, JsonValue]

  /**
   * Gets value.
   *
   * @param key object key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   */
  def apply(key: String): JsonValue

  /**
   * Optionally gets value.
   *
   * @param key object key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   */
  def get(key: String): Option[JsonValue]

  /**
   * Gets value or returns default.
   *
   * @param key     object key
   * @param default default value
   */
  def getOrElse(key: String, default: => JsonValue): JsonValue =
    get(key).getOrElse(default)

  /**
   * Tests for null.
   *
   * @param key object key
   *
   *
   * @throws java.util.NoSuchElementException if key does not exist
   */
  def isNull(key: String): Boolean =
    apply(key) == JsonNull

  /**
   * Gets value as `String`.
   *
   * @param key object key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws JsonExpectationError if not JsonString
   */
  def getString(key: String): String =
    expect[JsonString](apply(key)).value

  /**
   * Gets value as `Boolean`.
   *
   * @param key object key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws JsonExpectationError if not JsonBoolean
   */
  def getBoolean(key: String): Boolean =
    expect[JsonBoolean](apply(key)).value

  /**
   * Gets value as `Int`.
   *
   * @param key object key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws JsonExpectationError if not JsonNumber
   * @throws java.lang.ArithmeticException if not represented exactly
   */
  def getInt(key: String): Int =
    expect[JsonNumber](apply(key)).toInt

  /**
   * Gets value as `Long`.
   *
   * @param key object key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws JsonExpectationError if not JsonNumber
   * @throws java.lang.ArithmeticException if not represented exactly
   */
  def getLong(key: String): Long =
    expect[JsonNumber](apply(key)).toLong

  /**
   * Gets value as `Float`.
   *
   * @param key object key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws JsonExpectationError if not JsonNumber
   */
  def getFloat(key: String): Float =
    expect[JsonNumber](apply(key)).toFloat

  /**
   * Gets value as `Double`.
   *
   * @param key object key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws JsonExpectationError if not JsonNumber
   */
  def getDouble(key: String): Double =
    expect[JsonNumber](apply(key)).toDouble

  /**
   * Gets value as `BigInt`.
   *
   * @param key object key
   *
   * @throws JsonExpectationError if not JsonNumber
   * @throws java.lang.ArithmeticException if not represented exactly
   */
  def getBigInt(key: String): BigInt =
    expect[JsonNumber](apply(key)).toBigInt

  /**
   * Gets value as `BigDecimal`.
   *
   * @param key object key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws JsonExpectationError if not JsonNumber
   */
  def getBigDecimal(key: String): BigDecimal =
    expect[JsonNumber](apply(key)).toBigDecimal

  /**
   * Gets value as `JsonObject`.
   *
   * @param key object key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws JsonExpectationError if not JsonObject
   */
  def getObject(key: String): JsonObject =
    expect { apply(key) }

  /**
   * Gets value as `JsonArray`.
   *
   * @param key object key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws JsonExpectationError if not JsonArray
   */
  def getArray(key: String): JsonArray =
    expect { apply(key) }

  /**
   * Reads value.
   *
   * @param key object key
   *
   * @throws java.util.NoSuchElementException if key does not exists
   * @throws java.lang.NullPointerException if value is null
   */
  def read[T](key: String)(using JsonInput[T]): T =
    apply(key) match
      case JsonNull => throw NullPointerException()
      case value    => value.as[T]

  /**
   * Optionally reads value.
   *
   * @param key object key
   *
   * @note The value is not read if it is null.
   */
  def readOption[T](key: String)(using JsonInput[T]): Option[T] =
    get(key).filter(JsonNull.!=).map(_.as[T])

  /**
   * Reads value or returns default value.
   *
   * @param key     object key
   * @param default default value
   *
   * @note The value is not read if it is null.
   */
  def readOrElse[T](key: String, default: => T)(using JsonInput[T]): T =
   readOption(key).getOrElse(default)

  /**
   * Adds or updates field.
   *
   * @param key   object key
   * @param value new value
   *
   * @return new JSON object
   */
  def updated(key: String, value: JsonValue): JsonObject

  /**
   * Removes field.
   *
   * @param key object key
   *
   * @return new JSON object
   */
  def removed(key: String): JsonObject

  /**
   * Concatenates JSON object.
   *
   * @return new JSON object
   */
  @targetName("concat")
  def ++(other: JsonObject): JsonObject

/** Provides JSON object factory. */
object JsonObject:
  private val emptyObject = JsonObjectBuilder().toJsonObject()

  /** Gets empty JSON object. */
  def empty: JsonObject = emptyObject

  /** Creates empty JSON object. */
  def apply(): JsonObject = emptyObject

  /** Creates JSON object with supplied fields. */
  def apply(fields: Map[String, JsonValue]): JsonObject =
    fields.isEmpty match
      case true  => emptyObject
      case false =>
        fields.foldLeft(JsonObjectBuilder()) { (builder, field) =>
          builder.add(field._1, field._2)
        }.toJsonObject()

  /** Creates JSON object with supplied fields. */
  def apply(fields: Seq[(String, JsonValue)]): JsonObject =
    fields.isEmpty match
      case true  => emptyObject
      case false =>
        fields.foldLeft(JsonObjectBuilder()) { (builder, field) =>
          builder.add(field._1, field._2)
        }.toJsonObject()

  /** Deconstructs JSON object. */
  def unapply(json: JsonObject): Option[Map[String, JsonValue]] =
    json != null match
      case true  => Some(json.fields)
      case false => None

/**
 * Represents JSON array.
 *
 * @see [[JsonArrayBuilder]]
 */
trait JsonArray private[json] extends JsonStructure:
  /** Gets array values. */
  def values: Seq[JsonValue]

  /**
   * Gets value.
   *
   * @param index array index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   */
  def apply(index: Int): JsonValue

  /**
   * Tests for null.
   *
   * @param index array index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   */
  def isNull(index: Int): Boolean =
    apply(index) == JsonNull

  /**
   * Gets value as `String`.
   *
   * @param index array index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws JsonExpectationError if not JsonString
   */
  def getString(index: Int): String =
    expect[JsonString](apply(index)).value

  /**
   * Gets value as `Boolean`.
   *
   * @param index array index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws JsonExpectationError if not JsonBoolean
   */
  def getBoolean(index: Int): Boolean =
    expect[JsonBoolean](apply(index)).value

  /**
   * Gets value as `Int`.
   *
   * @param index array index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws JsonExpectationError if not JsonNumber
   * @throws java.lang.ArithmeticException if not represented exactly
   */
  def getInt(index: Int): Int =
    expect[JsonNumber](apply(index)).toInt

  /**
   * Gets value as `Long`.
   *
   * @param index array index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws JsonExpectationError if not JsonNumber
   * @throws java.lang.ArithmeticException if not represented exactly
   */
  def getLong(index: Int): Long =
    expect[JsonNumber](apply(index)).toLong

  /**
   * Gets value as `Float`.
   *
   * @param index array index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws JsonExpectationError if not JsonNumber
   */
  def getFloat(index: Int): Float =
    expect[JsonNumber](apply(index)).toFloat

  /**
   * Gets value as `Double`.
   *
   * @param index array index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws JsonExpectationError if not JsonNumber
   */
  def getDouble(index: Int): Double =
    expect[JsonNumber](apply(index)).toDouble

  /**
   * Gets value as `BigInt`.
   *
   * @param index array index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws JsonExpectationError if not JsonNumber
   * @throws java.lang.ArithmeticException if value cannot be represented exactly
   */
  def getBigInt(index: Int): BigInt =
    expect[JsonNumber](apply(index)).toBigInt

  /**
   * Gets value as `BigDecimal`.
   *
   * @param index array index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws JsonExpectationError if not JsonNumber
   */
  def getBigDecimal(index: Int): BigDecimal =
    expect[JsonNumber](apply(index)).toBigDecimal

  /**
   * Gets value as `JsonObject`.
   *
   * @param index array index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws JsonExpectationError if not JsonObject
   */
  def getObject(index: Int): JsonObject =
    expect { apply(index) }

  /**
   * Gets value as `JsonArray`.
   *
   * @param index array index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws JsonExpectationError if not JsonArray
   */
  def getArray(index: Int): JsonArray =
    expect { apply(index) }

  /**
   * Reads value.
   *
   * @param index array index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws java.lang.NullPointerException if value is null
   */
  def read[T](index: Int)(using JsonInput[T]): T =
    apply(index) match
      case JsonNull => throw NullPointerException()
      case value    => value.as[T]

  /**
   * Optionally reads value.
   *
   * @param index array index
   *
   * @note The value is not read if it is null.
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   */
  def readOption[T](index: Int)(using JsonInput[T]): Option[T] =
    apply(index) match
      case JsonNull => None
      case value    => Some(value.as[T])

  /**
   * Reads value or returns default value.
   *
   * @param index   array index
   * @param default default value
   *
   * @note The value is not read if it is null.
   */
  def readOrElse[T](index: Int, default: => T)(using JsonInput[T]): T =
   readOption(index).getOrElse(default)

  /**
   * Updates value at given index.
   *
   * @param index array index
   * @param value JSON value
   *
   * @return new JSON array
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   */
  def updated(index: Int, value: JsonValue): JsonArray

  /**
   * Removes value at given index.
   *
   * @param index array index
   *
   * @return new JSON array
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   *
   * @note Subsequent values to index are shifted left.
   */
  def removed(index: Int): JsonArray

  /**
   * Concatenates JSON array.
   *
   * @return new JSON array
   */
  @targetName("concat")
  def ++(suffix: JsonArray): JsonArray

  /**
   * Prepends value.
   *
   * @return new JSON array
   */
  @targetName("prepend")
  def +:(value: JsonValue): JsonArray

  /**
   * Appends value.
   *
   * @return new JSON array
   */
  @targetName("append")
  def :+(value: JsonValue): JsonArray

/** Provides JSON array factory. */
object JsonArray:
  private val emptyArray = JsonArrayBuilder().toJsonArray()

  /** Gets empty JSON array. */
  def empty: JsonArray = emptyArray

  /** Creates empty JSON array. */
  def apply(): JsonArray = emptyArray

  /** Creates JSON array with supplied values. */
  def apply(values: Seq[JsonValue]): JsonArray =
    values.isEmpty match
      case true  => emptyArray
      case false => values.foldLeft(JsonArrayBuilder())(_ add _).toJsonArray()

  /** Deconstructs JSON array. */
  def unapply(json: JsonArray): Option[Seq[JsonValue]] =
    json != null match
      case true  => Some(json.values)
      case false => None

private case class JsonStringImpl(value: String) extends JsonString:
  if value == null then
    throw NullPointerException()

  override lazy val toString = EncodedString(value)

private case class JsonNumberImpl(toBigDecimal: BigDecimal) extends JsonNumber:
  if toBigDecimal == null then
    throw NullPointerException()

  lazy val toInt    = toBigDecimal.toIntExact
  lazy val toLong   = toBigDecimal.toLongExact
  lazy val toFloat  = toBigDecimal.toFloat
  lazy val toDouble = toBigDecimal.toDouble
  lazy val toBigInt = toBigDecimal.toBigIntExact.getOrElse(throw ArithmeticException())

  override lazy val toString = toBigDecimal.toString
