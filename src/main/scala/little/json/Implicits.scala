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
import scala.collection.Factory
import scala.util.{ Failure, Try }

/**
 * Provides extension methods and implicit conversions.
 *
 * @groupname json-exts Json Extension Methods
 * @groupprio json-exts 100
 * @groupdesc json-exts Defines extension methods
 *
 * @groupname json-input Json Input
 * @groupprio json-input 200
 * @groupdesc json-input Defines implcit conversions from JSON
 *
 * @groupname json-output Json Output
 * @groupprio json-output 300
 * @groupdesc json-output Defines implcit conversions to JSON
 */
object Implicits:
  /**
   * Adds extends methods to `JsonValue`.
   *
   * @group json-exts
   */
  extension (json: JsonValue)
    /**
     * Gets value in JSON object.
     *
     * @param name field name
     *
     * @throws ClassCastException if not [[JsonObject]]
     */
    @targetName("at")
    def \(name: String): JsonValue =
      json.asInstanceOf[JsonObject](name)

    /**
     * Gets value in JSON array.
     *
     * @param index array index
     *
     * @throws ClassCastException if not [[JsonArray]]
     */
    @targetName("at")
    def \(index: Int): JsonValue =
      json.asInstanceOf[JsonArray](index)

    /**
     * Collects values with given field name while traversing nested objects and
     * arrays.
     *
     * {{{
     * import little.json.Json
     * import little.json.Implicits.{ *, given }
     *
     * val json = Json.parse("""{
     *   "node": {
     *     "name": "localhost",
     *     "users": [
     *       { "id": 0,    "name": "root" },
     *       { "id": 1000, "name": "jza"  }
     *     ]
     *   }
     * }""")
     *
     * val names = (json \\ "name").map(_.as[String])
     *
     * assert { names == Seq("localhost", "root", "jza") }
     * }}}
     *
     * @param name field name
     */
    @targetName("collect")
    def \\(name: String): Seq[JsonValue] =
      json match
        case json: JsonObject =>
          Try(json(name))
            .toOption
            .toSeq ++
          json.fields.values.flatMap(_ \\ name).toSeq

        case json: JsonArray => json.values.flatMap(_ \\ name).toSeq
        case _               => Nil

  /**
   * Returns `JsonValue`.
   *
   * @group json-input
   */
  given jsonValueToJsonValue: JsonInput[JsonValue] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value

  /**
   * Casts value to `JsonObject`.
   *
   * @group json-input
   */
  given jsonValueToJsonObject: JsonInput[JsonObject] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonObject]

  /**
   * Casts value to `JsonArray`.
   *
   * @group json-input
   */
  given jsonValueToJsonArray: JsonInput[JsonArray] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonArray]

  /**
   * Casts value to `JsonString`.
   *
   * @group json-input
   */
  given jsonValueToJsonString: JsonInput[JsonString] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonString]

  /**
   * Casts value to `JsonNumber`.
   *
   * @group json-input
   */
  given jsonValueToJsonNumber: JsonInput[JsonNumber] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNumber]

  /**
   * Casts value to `JsonBoolean`.
   *
   * @group json-input
   */
  given jsonValueToJsonBoolean: JsonInput[JsonBoolean] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonBoolean]

  /**
   * Casts value to `JsonNull`.
   *
   * @group json-input
   */
  given jsonValueToJsonNull: JsonInput[JsonNull] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNull]

  /**
   * Converts `JsonString` to `String`.
   *
   * @group json-input
   */
  given jsonStringToString: JsonInput[String] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonString].value

  /**
   * Converts `JsonNumber` to `Short`.
   *
   * @group json-input
   */
  given jsonNumberToShort: JsonInput[Short] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNumber].shortValue

  /**
   * Converts `JsonNumber` to `Int`.
   *
   * @group json-input
   */
  given jsonNumberToInt: JsonInput[Int] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNumber].intValue

  /**
   * Converts `JsonNumber` to `Long`.
   *
   * @group json-input
   */
  given jsonNumberToLong: JsonInput[Long] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNumber].longValue

  /**
   * Converts `JsonNumber` to `Float`.
   *
   * @group json-input
   */
  given jsonNumberToFloat: JsonInput[Float] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNumber].floatValue

  /**
   * Converts `JsonNumber` to `Double`.
   *
   * @group json-input
   */
  given jsonNumberToDouble: JsonInput[Double] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNumber].doubleValue

  /**
   * Converts `JsonNumber` to `BigInt`.
   *
   * @group json-input
   */
  given jsonNumberToBigInt: JsonInput[BigInt] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNumber].bigIntValue

  /**
   * Converts `JsonNumber` to `BigDecimal`.
   *
   * @group json-input
   */
  given jsonNumberToBigDecimal: JsonInput[BigDecimal] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNumber].bigDecimalValue

  /**
   * Converts `JsonBoolean` to `Boolean`.
   *
   * @group json-input
   */
  given jsonNumberToBoolean: JsonInput[Boolean] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonBoolean].value

  /**
   * Converts `JsonArray` to collection of converted values.
   *
   * @group json-input
   */
  given jsonValueToCollection[T, M[T]](using convert: JsonInput[T])(using factory: Factory[T, M[T]]): JsonInput[M[T]] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonArray].values.map(_.as[T]).to(factory)

  /**
   * Converts `JsonValue` to `Some` or returns `None` if value is `JsonNull`.
   *
   * @group json-output
   */
  given jsonValueToOption[T](using convert: JsonInput[T]): JsonInput[Option[T]] with
    /** @inheritdoc */
    def apply(value: JsonValue) = Option.when(value != JsonNull)(value.as[T])

  /**
   * Converts `JsonValue` to `Success` or returns `Failure` if unsuccessful.
   *
   * @group json-output
   */
  given jsonValueToTry[T](using convert: JsonInput[T]): JsonInput[Try[T]] with
    /** @inheritdoc */
    def apply(value: JsonValue) = Try(value.as[T])

  /**
   * Converts `JsonValue` to `Right` using right converter or to `Left` using
   * left if right is unsuccessful.
   *
   * @group json-output
   */
  given jsonValueToEither[A, B](using left: JsonInput[A])(using right: JsonInput[B]): JsonInput[Either[A, B]] with
    /** @inheritdoc */
    def apply(value: JsonValue) = Try(Right(right(value))).getOrElse(Left(left(value)))

  /**
   * Converts `String` to `JsonString`.
   *
   * @group json-output
   */
  given stringToJsonString: JsonOutput[String] with
    /** @inheritdoc */
    def apply(value: String) = JsonString(value)

  /**
   * Converts `Short` to `JsonNumber`.
   *
   * @group json-output
   */
  given shortToJsonNumber: JsonOutput[Short] with
    /** @inheritdoc */
    def apply(value: Short) = JsonNumber(value)

  /**
   * Converts `Int` to `JsonNumber`.
   *
   * @group json-output
   */
  given intToJsonNumber: JsonOutput[Int] with
    /** @inheritdoc */
    def apply(value: Int) = JsonNumber(value)

  /**
   * Converts `Long` to `JsonNumber`.
   *
   * @group json-output
   */
  given longToJsonNumber: JsonOutput[Long] with
    /** @inheritdoc */
    def apply(value: Long) = JsonNumber(value)

  /**
   * Converts `Float` to `JsonNumber`.
   *
   * @group json-output
   */
  given floatToJsonNumber: JsonOutput[Float] with
    /** @inheritdoc */
    def apply(value: Float) = JsonNumber(value)

  /**
   * Converts `Double` to `JsonNumber`.
   *
   * @group json-output
   */
  given doubleToJsonNumber: JsonOutput[Double] with
    /** @inheritdoc */
    def apply(value: Double) = JsonNumber(value)

  /**
   * Converts `BigInt` to `JsonNumber`.
   *
   * @group json-output
   */
  given bigIntToJsonNumber: JsonOutput[BigInt] with
    /** @inheritdoc */
    def apply(value: BigInt) = JsonNumber(value)

  /**
   * Converts `BigDecimal` to `JsonNumber`.
   *
   * @group json-output
   */
  given bigDecimalToJsonNumber: JsonOutput[BigDecimal] with
    /** @inheritdoc */
    def apply(value: BigDecimal) = JsonNumber(value)

  /**
   * Converts `Boolean` to `JsonBoolean`.
   *
   * @group json-output
   */
  given booleanToJsonBoolean: JsonOutput[Boolean] with
    /** @inheritdoc */
    def apply(value: Boolean) = JsonBoolean(value)

  /**
   * Converts `Array` to `JsonArray`.
   *
   * @group json-output
   */
  given arrayToJsonArray[T](using convert: JsonOutput[T]): JsonOutput[Array[T]] with
    /** @inheritdoc */
    def apply(value: Array[T]) = value.foldLeft(JsonArrayBuilder()) {
      (builder, value) => builder.add(convert(value))
    }.build()

  /**
   * Converts `Iterable` to `JsonArray`.
   *
   * @group json-output
   */
  given iterableToJsonArray[T, M[T] <: Iterable[T]](using convert: JsonOutput[T]): JsonOutput[M[T]] with
    /** @inheritdoc */
    def apply(value: M[T]) = value.foldLeft(JsonArrayBuilder()) {
      (builder, value) => builder.add(convert(value))
    }.build()

  /**
   * Converts `Some` to `JsonValue` or returns `JsonNull` if `None`.
   *
   * @group json-output
   */
  given optionToJsonValue[T, M[T] <: Option[T]](using convert: JsonOutput[T]): JsonOutput[M[T]] with
    /** @inheritdoc */
    def apply(value: M[T]) = value.fold(JsonNull)(convert(_))

  /**
   * Converts `None` to `JsonNull`.
   *
   * @group json-output
   */
  given noneToJsonValue: JsonOutput[None.type] with
    /** @inheritdoc */
    def apply(value: None.type) = JsonNull

  /**
   * Converts `Success` to `JsonValue` or returns `JsonNull` if `Failure`.
   *
   * @group json-output
   */
  given tryToJsonValue[T, M[T] <: Try[T]](using convert: JsonOutput[T]): JsonOutput[M[T]] with
    /** @inheritdoc */
    def apply(value: M[T]) = value.fold(_ => JsonNull, convert(_))

  /**
   * Converts `Failure` to `JsonNull`.
   *
   * @group json-output
   */
  given failureToJsonValue: JsonOutput[Failure[?]] with
    /** @inheritdoc */
    def apply(value: Failure[?]) = JsonNull

  /**
   * Converts `Right` to `JsonValue` using right converter or converts `Left`
   * using left.
   *
   * @group json-output
   */
  given eitherToJsonValue[A, B, M[A, B] <: Either[A, B]](using left: JsonOutput[A])(using right: JsonOutput[B]): JsonOutput[M[A, B]] with
    /** @inheritdoc */
    def apply(value: M[A, B]) = value.fold(left(_), right(_))

  /**
   * Converts `Right` to `JsonValue`.
   *
   * @group json-output
   */
  given rightToJsonValue[T](using convert: JsonOutput[T]): JsonOutput[Right[?, T]] with
    /** @inheritdoc */
    def apply(value: Right[?, T]) = value.fold(_ => JsonNull, convert(_))

  /**
   * Converts `Left` to `JsonValue`.
   *
   * @group json-output
   */
  given leftToJsonValue[T](using convert: JsonOutput[T]): JsonOutput[Left[T, ?]] with
    /** @inheritdoc */
    def apply(value: Left[T, ?]) = value.fold(convert(_), _ => JsonNull)

  /**
   * Converts `(String, T)` to `(String, JsonValue)`.
   *
   * @group json-output
   */
  given tupleToJsonField[T](using convert: JsonOutput[T]): Conversion[(String, T), (String, JsonValue)] with
    /** @inheritdoc */
    def apply(value: (String, T)) = value(0) -> convert(value(1))
