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
package little.json

import scala.annotation.targetName

/** Defines JSON value. */
sealed trait JsonValue:
  /** Converts value. */
  final def as[T](using convert: JsonInput[T]): T =
    convert(this)

/** Defines JSON structure. */
sealed trait JsonStructure extends JsonValue:
  /** Gets size. */
  def size: Int

  /** Tests for emptiness. */
  def isEmpty: Boolean = size == 0

/**
 * Defines JSON object.
 *
 * @see [[JsonObjectBuilder]]
 */
trait JsonObject extends JsonStructure:
  /** Gets field names. */
  def names: Seq[String]

  /** Gets fields. */
  def fields: Map[String, JsonValue]

  /**
   * Gets value.
   *
   * @param name field name
   */
  def apply(name: String): JsonValue

  /**
   * Gets optional value.
   *
   * @param name field name
   */
  def get(name: String): Option[JsonValue]

  /**
   * Gets converted value or returns default if not present.
   *
   * @param name    field name
   * @param default default value
   */
  def getOrElse[T](name: String, default: => T)(using JsonInput[T]): T =
    get(name).map(_.as[T]).getOrElse(default)

  /**
   * Gets JSON object.
   *
   * @param name field name
   *
   * @throws ClassCastException if not JsonObject
   */
  def getJsonObject(name: String): JsonObject =
    apply(name).asInstanceOf[JsonObject]

  /**
   * Gets JSON array.
   *
   * @param name field name
   *
   * @throws ClassCastException if not JsonArray
   */
  def getJsonArray(name: String): JsonArray =
    apply(name).asInstanceOf[JsonArray]

  /**
   * Gets JSON string.
   *
   * @param name field name
   *
   * @throws ClassCastException if not JsonString
   */
  def getJsonString(name: String): JsonString =
    apply(name).asInstanceOf[JsonString]

  /**
   * Gets JSON number.
   *
   * @param name field name
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getJsonNumber(name: String): JsonNumber =
    apply(name).asInstanceOf[JsonNumber]

  /**
   * Gets JSON boolean.
   *
   * @param name field name
   *
   * @throws ClassCastException if not JsonBoolean
   */
  def getJsonBoolean(name: String): JsonBoolean =
    apply(name).asInstanceOf[JsonBoolean]

  /**
   * Gets JSON null.
   *
   * @param name field name
   *
   * @throws ClassCastException if not JsonNull
   */
  def getJsonNull(name: String): JsonNull =
    apply(name).asInstanceOf[JsonNull]

  /**
   * Gets String.
   *
   * @param name field name
   *
   * @throws ClassCastException if not JsonString
   */
  def getString(name: String): String =
    apply(name).asInstanceOf[JsonString].value

  /**
   * Gets Byte.
   *
   * @param name field name
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getByte(name: String): Byte =
    apply(name).asInstanceOf[JsonNumber].byteValue

  /**
   * Gets Short.
   *
   * @param name field name
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getShort(name: String): Short =
    apply(name).asInstanceOf[JsonNumber].shortValue

  /**
   * Gets Int.
   *
   * @param name field name
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getInt(name: String): Int =
    apply(name).asInstanceOf[JsonNumber].intValue

  /**
   * Gets Long.
   *
   * @param name field name
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getLong(name: String): Long =
    apply(name).asInstanceOf[JsonNumber].longValue

  /**
   * Gets Float.
   *
   * @param name field name
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getFloat(name: String): Float =
    apply(name).asInstanceOf[JsonNumber].floatValue

  /**
   * Gets Double.
   *
   * @param name field name
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getDouble(name: String): Double =
    apply(name).asInstanceOf[JsonNumber].doubleValue

  /**
   * Gets BigInt.
   *
   * @param name field name
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getBigInt(name: String): BigInt =
    apply(name).asInstanceOf[JsonNumber].bigIntValue

  /**
   * Gets BigDecimal.
   *
   * @param name field name
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getBigDecimal(name: String): BigDecimal =
    apply(name).asInstanceOf[JsonNumber].bigDecimalValue

  /**
   * Gets Boolean.
   *
   * @param name field name
   *
   * @throws ClassCastException if not JsonBoolean
   */
  def getBoolean(name: String): Boolean =
    apply(name).asInstanceOf[JsonBoolean].value

  /**
   * Tests for null.
   *
   * @param name field name
   *
   * @throws ClassCastException if not JsonNull
   */
  def isNull(name: String): Boolean =
    apply(name).isInstanceOf[JsonNull]

  /**
   * Maps optional value excluding null.
   *
   * @param name field name
   */
  def map[T](name: String)(using JsonInput[T]): Option[T] =
    get(name).filter(JsonNull.!=).map(_.as[T])

  /**
   * Concatenates JSON object.
   *
   * @return new JSON object
   */
  @targetName("concat")
  def ++(other: JsonObject): JsonObject

  /**
   * Adds or updates field.
   *
   * @return new JSON object
   */
  @targetName("updated")
  def +(field: (String, JsonValue)): JsonObject

  /**
   * Removes field.
   *
   * @param name field name
   *
   * @return new JSON object
   */
  @targetName("removed")
  def -(name: String): JsonObject

/** Provides JSON object factory. */
object JsonObject:
  private val emptyObject = JsonObjectBuilder().build()

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
        }.build()

  /** Creates JSON object with supplied fields. */
  def apply(fields: Seq[(String, JsonValue)]): JsonObject =
    fields.isEmpty match
      case true  => emptyObject
      case false =>
        fields.foldLeft(JsonObjectBuilder()) { (builder, field) =>
          builder.add(field._1, field._2)
        }.build()

  /** Destructures JSON object to its fields. */
  def unapply(json: JsonObject): Option[Map[String, JsonValue]] =
    json != null match
      case true  => Some(json.fields)
      case false => None

/**
 * Defines JSON array.
 *
 * @see [[JsonArrayBuilder]]
 */
trait JsonArray extends JsonStructure:
  /** Gets values. */
  def values: Seq[JsonValue]

  /** Gets value at given index. */
  def apply(index: Int): JsonValue

  /**
   * Gets JSON object at given index.
   *
   * @throws ClassCastException if not JsonObject
   */
  def getJsonObject(index: Int): JsonObject =
    apply(index).asInstanceOf[JsonObject]

  /**
   * Gets JSON array at given index.
   *
   * @throws ClassCastException if not JsonArray
   */
  def getJsonArray(index: Int): JsonArray =
    apply(index).asInstanceOf[JsonArray]

  /**
   * Gets JSON string at given index.
   *
   * @throws ClassCastException if not JsonString
   */
  def getJsonString(index: Int): JsonString =
    apply(index).asInstanceOf[JsonString]

  /**
   * Gets JSON number at given index.
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getJsonNumber(index: Int): JsonNumber =
    apply(index).asInstanceOf[JsonNumber]

  /**
   * Gets JSON boolean at given index.
   *
   * @throws ClassCastException if not JsonBoolean
   */
  def getJsonBoolean(index: Int): JsonBoolean =
    apply(index).asInstanceOf[JsonBoolean]

  /**
   * Gets JSON null at given index.
   *
   * @throws ClassCastException if not JsonNull
   */
  def getJsonNull(index: Int): JsonNull =
    apply(index).asInstanceOf[JsonNull]

  /**
   * Gets String at given index.
   *
   * @throws ClassCastException if not JsonString
   */
  def getString(index: Int): String =
    apply(index).asInstanceOf[JsonString].value

  /**
   * Gets Byte at given index.
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getByte(index: Int): Byte =
    apply(index).asInstanceOf[JsonNumber].byteValue

  /**
   * Gets Short at given index.
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getShort(index: Int): Short =
    apply(index).asInstanceOf[JsonNumber].shortValue

  /**
   * Gets Int at given index.
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getInt(index: Int): Int =
    apply(index).asInstanceOf[JsonNumber].intValue

  /**
   * Gets Long at given index.
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getLong(index: Int): Long =
    apply(index).asInstanceOf[JsonNumber].longValue

  /**
   * Gets Float at given index.
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getFloat(index: Int): Float =
    apply(index).asInstanceOf[JsonNumber].floatValue

  /**
   * Gets Double at given index.
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getDouble(index: Int): Double =
    apply(index).asInstanceOf[JsonNumber].doubleValue

  /**
   * Gets BigInt at given index.
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getBigInt(index: Int): BigInt =
    apply(index).asInstanceOf[JsonNumber].bigIntValue

  /**
   * Gets BigDecimal at given index.
   *
   * @throws ClassCastException if not JsonNumber
   */
  def getBigDecimal(index: Int): BigDecimal =
    apply(index).asInstanceOf[JsonNumber].bigDecimalValue

  /**
   * Gets Boolean at given index.
   *
   * @throws ClassCastException if not JsonBoolean
   */
  def getBoolean(index: Int): Boolean =
    apply(index).asInstanceOf[JsonBoolean].value

  /** Tests for null at given index. */
  def isNull(index: Int): Boolean =
    apply(index).isInstanceOf[JsonNull]

  /**
   * Updates value at given index.
   *
   * @return new JSON array
   */
  def updated(index: Int, value: JsonValue): JsonArray

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
  private val emptyArray = JsonArrayBuilder().build()

  /** Gets empty JSON array. */
  def empty: JsonArray = emptyArray

  /** Creates empty JSON array. */
  def apply(): JsonArray = emptyArray

  /** Creates JSON array with supplied values. */
  def apply(values: Seq[JsonValue]): JsonArray =
    values.isEmpty match
      case true  => emptyArray
      case false => values.foldLeft(JsonArrayBuilder())(_ add _).build()

  /** Destructures JSON array to its values. */
  def unapply(json: JsonArray): Option[Seq[JsonValue]] =
    json != null match
      case true  => Some(json.values)
      case false => None

/** Defines JSON string. */
trait JsonString extends JsonValue:
  /** Gets value. */
  def value: String

/** Provides JSON string factory. */
object JsonString:
  /** Creates JSON string with value. */
  def apply(value: String): JsonString =
    JsonStringImpl(value)

  /** Destructures JSON string to its value. */
  def unapply(json: JsonString): Option[String] =
    json != null match
      case true  => Some(json.value)
      case false => None

/** Defines JSON number. */
trait JsonNumber extends JsonValue:
  /**
   * Gets value as `Byte`.
   *
   * @throws ArithmeticException if value cannot be represented exactly
   */
  def byteValue: Byte

  /**
   * Gets value as `Short`.
   *
   * @throws ArithmeticException if value cannot be represented exactly
   */
  def shortValue: Short

  /**
   * Gets value as `Int`.
   *
   * @throws ArithmeticException if value cannot be represented exactly
   */
  def intValue: Int

  /**
   * Gets value as `Long`.
   *
   * @throws ArithmeticException if value cannot be represented exactly
   */
  def longValue: Long

  /** Gets value as `Float`. */
  def floatValue: Float

  /** Gets value as `Double`. */
  def doubleValue: Double

  /**
   * Gets value as `BigInt`.
   *
   * @throws ArithmeticException if value cannot be represented exactly
   */
  def bigIntValue: BigInt

  /** Gets value as `BigDecimal`. */
  def bigDecimalValue: BigDecimal

/** Provides JSON number factory. */
object JsonNumber:
  /**
   * Creates JSON number with value.
   *
   * @throws NumberFormatException if value is invalid numeric representation
   */
  def apply(value: String): JsonNumber =
    JsonNumberImpl(BigDecimal(value))

  /** Creates JSON number with value. */
  def apply(value: Int): JsonNumber =
    JsonNumberImpl(BigDecimal(value))

  /** Creates JSON number with value. */
  def apply(value: Long): JsonNumber =
    JsonNumberImpl(BigDecimal(value))

  /** Creates JSON number with value. */
  def apply(value: Double): JsonNumber =
    JsonNumberImpl(BigDecimal(value))

  /** Creates JSON number with value. */
  def apply(value: BigInt): JsonNumber =
    JsonNumberImpl(BigDecimal(value))

  /** Creates JSON number with value. */
  def apply(value: BigDecimal): JsonNumber =
    JsonNumberImpl(value)

  /** Destructures JSON number to its value. */
  def unapply(json: JsonNumber): Option[BigDecimal] =
    json != null match
      case true  => Some(json.bigDecimalValue)
      case false => None

/** Defines JSON boolean. */
sealed trait JsonBoolean extends JsonValue:
  /** Gets value. */
  def value: Boolean

/** Provides JSON boolean factory. */
object JsonBoolean:
  /** Gets JSON boolean with value. */
  def apply(value: Boolean): JsonBoolean =
    if value then JsonTrue else JsonFalse

  /** Destructures JSON boolean to its value. */
  def unapply(json: JsonBoolean): Option[Boolean] =
    json != null match
      case true  => Some(json.value)
      case false => None

/** Represents JSON true. */
case object JsonTrue extends JsonBoolean:
  /**
   * @inheritdoc
   *
   * @return `true`
   */
  val value = true

  /** Returns `"true"`. */
  override val toString = "true"

/** Represents JSON false. */
case object JsonFalse extends JsonBoolean:
  /**
   * @inheritdoc
   *
   * @return `false`
   */
  val value = false

  /** Returns `"false"`. */
  override val toString = "false"

/** Defines JSON null. */
sealed trait JsonNull extends JsonValue

/** Represents JSON null. */
case object JsonNull extends JsonNull:
  /** Returns `"null"`. */
  override val toString = "null"
