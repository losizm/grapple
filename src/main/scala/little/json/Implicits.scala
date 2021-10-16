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

/** Provides extension methods and implicit conversions. */
object Implicits:
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
   * Returns `JsonValue` as is.
   *
   * This instance is required to perform actions such as the following:
   *
   * {{{
   * import little.json.{ Json, JsonValue }
   * import little.json.Implicits.given
   *
   * import scala.language.implicitConversions
   *
   * val json = Json.obj("values" -> Json.arr("abc", 123, true))
   *
   * // Requires jsonValueToJsonValue
   * val list = json("values").as[List[JsonValue]]
   * }}}
   */
  given jsonValueToJsonValue: JsonInput[JsonValue] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value

  /** Casts `JsonValue` to `JsonObject`. */
  given jsonValueToJsonObject: JsonInput[JsonObject] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonObject]

  /** Casts `JsonValue` to `JsonArray`. */
  given jsonValueToJsonArray: JsonInput[JsonArray] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonArray]

  /** Casts `JsonValue` to `JsonString`. */
  given jsonValueToJsonString: JsonInput[JsonString] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonString]

  /** Casts `JsonValue` to `JsonNumber`. */
  given jsonValueToJsonNumber: JsonInput[JsonNumber] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNumber]

  /** Casts `JsonValue` to `JsonBoolean`. */
  given jsonValueToJsonBoolean: JsonInput[JsonBoolean] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonBoolean]

  /** Casts `JsonValue` to `JsonNull`. */
  given jsonValueToJsonNull: JsonInput[JsonNull] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNull]

  /** Converts `JsonValue` to `String`. */
  given jsonValueToString: JsonInput[String] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonString].value

  /** Converts `JsonValue` to `Short`. */
  given jsonValueToShort: JsonInput[Short] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNumber].shortValue

  /** Converts `JsonValue` to `Int`. */
  given jsonValueToInt: JsonInput[Int] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNumber].intValue

  /** Converts `JsonValue` to `Long`. */
  given jsonValueToLong: JsonInput[Long] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNumber].longValue

  /** Converts `JsonValue` to `Float`. */
  given jsonValueToFloat: JsonInput[Float] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNumber].floatValue

  /** Converts `JsonValue` to `Double`. */
  given jsonValueToDouble: JsonInput[Double] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNumber].doubleValue

  /** Converts `JsonValue` to `BigInt`. */
  given jsonValueToBigInt: JsonInput[BigInt] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNumber].bigIntValue

  /** Converts `JsonValue` to `BigDecimal`. */
  given jsonValueToBigDecimal: JsonInput[BigDecimal] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonNumber].bigDecimalValue

  /** Converts `JsonValue` to `Boolean`. */
  given jsonValueToBoolean: JsonInput[Boolean] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonBoolean].value

  /** Converts `JsonValue` to collection of converted values. */
  given jsonValueToCollection[T, M[T]](using convert: JsonInput[T])(using factory: Factory[T, M[T]]): JsonInput[M[T]] with
    /** @inheritdoc */
    def apply(value: JsonValue) = value.asInstanceOf[JsonArray].values.map(_.as[T]).to(factory)

  /** Converts `JsonValue` to `Some` or returns `None` if value is `JsonNull`. */
  given jsonValueToOption[T](using convert: JsonInput[T]): JsonInput[Option[T]] with
    /** @inheritdoc */
    def apply(value: JsonValue) = Option.when(value != JsonNull)(value.as[T])

  /** Converts `JsonValue` to `Success` or returns `Failure` if unsuccessful. */
  given jsonValueToTry[T](using convert: JsonInput[T]): JsonInput[Try[T]] with
    /** @inheritdoc */
    def apply(value: JsonValue) = Try(value.as[T])

  /**
   * Converts `JsonValue` to `Right` using right converter or to `Left` using
   * left if right is unsuccessful.
   */
  given jsonValueToEither[A, B](using left: JsonInput[A])(using right: JsonInput[B]): JsonInput[Either[A, B]] with
    /** @inheritdoc */
    def apply(value: JsonValue) = Try(Right(right(value))).getOrElse(Left(left(value)))

  /** Converts `String` to `JsonString`. */
  given stringToJsonString: JsonOutput[String] with
    /** @inheritdoc */
    def apply(value: String): JsonString = JsonString(value)

  /** Converts `Short` to `JsonNumber`. */
  given shortToJsonNumber: JsonOutput[Short] with
    /** @inheritdoc */
    def apply(value: Short): JsonNumber = JsonNumber(value)

  /** Converts `Int` to `JsonNumber`. */
  given intToJsonNumber: JsonOutput[Int] with
    /** @inheritdoc */
    def apply(value: Int): JsonNumber = JsonNumber(value)

  /** Converts `Long` to `JsonNumber`. */
  given longToJsonNumber: JsonOutput[Long] with
    /** @inheritdoc */
    def apply(value: Long): JsonNumber = JsonNumber(value)

  /** Converts `Float` to `JsonNumber`. */
  given floatToJsonNumber: JsonOutput[Float] with
    /** @inheritdoc */
    def apply(value: Float): JsonNumber = JsonNumber(value)

  /** Converts `Double` to `JsonNumber`. */
  given doubleToJsonNumber: JsonOutput[Double] with
    /** @inheritdoc */
    def apply(value: Double): JsonNumber = JsonNumber(value)

  /** Converts `BigInt` to `JsonNumber`. */
  given bigIntToJsonNumber: JsonOutput[BigInt] with
    /** @inheritdoc */
    def apply(value: BigInt): JsonNumber = JsonNumber(value)

  /** Converts `BigDecimal` to `JsonNumber`. */
  given bigDecimalToJsonNumber: JsonOutput[BigDecimal] with
    /** @inheritdoc */
    def apply(value: BigDecimal): JsonNumber = JsonNumber(value)

  /** Converts `Boolean` to `JsonBoolean`. */
  given booleanToJsonBoolean: JsonOutput[Boolean] with
    /** @inheritdoc */
    def apply(value: Boolean): JsonBoolean = JsonBoolean(value)

  /** Converts `Array` to `JsonArray`. */
  given arrayToJsonArray[T](using convert: JsonOutput[T]): JsonOutput[Array[T]] with
    /** @inheritdoc */
    def apply(value: Array[T]): JsonArray = value.foldLeft(JsonArrayBuilder()) {
      (builder, value) => builder.add(convert(value))
    }.build()

  /** Converts `Iterable` to `JsonArray`. */
  given iterableToJsonArray[T, M[T] <: Iterable[T]](using convert: JsonOutput[T]): JsonOutput[M[T]] with
    /** @inheritdoc */
    def apply(value: M[T]): JsonArray = value.foldLeft(JsonArrayBuilder()) {
      (builder, value) => builder.add(convert(value))
    }.build()

  /** Converts `Some` to `JsonValue` or returns `JsonNull` if `None`. */
  given optionToJsonValue[T, M[T] <: Option[T]](using convert: JsonOutput[T]): JsonOutput[M[T]] with
    /** @inheritdoc */
    def apply(value: M[T]) = value.fold(JsonNull)(convert(_))

  /** Converts `None` to `JsonNull`. */
  given noneToJsonNull: JsonOutput[None.type] with
    /** @inheritdoc */
    def apply(value: None.type): JsonNull = JsonNull

  /** Converts `Success` to `JsonValue` or returns `JsonNull` if `Failure`. */
  given tryToJsonValue[T, M[T] <: Try[T]](using convert: JsonOutput[T]): JsonOutput[M[T]] with
    /** @inheritdoc */
    def apply(value: M[T]) = value.fold(_ => JsonNull, convert(_))

  /** Converts `Failure` to `JsonNull`. */
  given failureToJsonNull: JsonOutput[Failure[?]] with
    /** @inheritdoc */
    def apply(value: Failure[?]): JsonNull = JsonNull

  /**
   * Converts `Right` to `JsonValue` using right converter or converts `Left`
   * using left.
   */
  given eitherToJsonValue[A, B, M[A, B] <: Either[A, B]](using left: JsonOutput[A])(using right: JsonOutput[B]): JsonOutput[M[A, B]] with
    /** @inheritdoc */
    def apply(value: M[A, B]) = value.fold(left(_), right(_))

  /** Converts `Right` to `JsonValue`. */
  given rightToJsonValue[T](using convert: JsonOutput[T]): JsonOutput[Right[?, T]] with
    /** @inheritdoc */
    def apply(value: Right[?, T]) = value.fold(_ => JsonNull, convert(_))

  /** Converts `Left` to `JsonValue`. */
  given leftToJsonValue[T](using convert: JsonOutput[T]): JsonOutput[Left[T, ?]] with
    /** @inheritdoc */
    def apply(value: Left[T, ?]) = value.fold(convert(_), _ => JsonNull)

  /** Converts `(String, T)` to `(String, JsonValue)`. */
  given tupleToJsonField[T](using convert: JsonOutput[T]): Conversion[(String, T), (String, JsonValue)] with
    /** @inheritdoc */
    def apply(value: (String, T)) = value(0) -> convert(value(1))
