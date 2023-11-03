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
import scala.collection.Factory
import scala.util.{ Failure, Try }

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
   * import grapple.json.{ Json, \\, given }
   *
   * val json = Json.parse("""{
   *   "node": {
   *     "name": "localhost",
   *     "users": [
   *       { "id": 0,    "name": "root" },
   *       { "id": 1000, "name": "lupita"  }
   *     ]
   *   }
   * }""")
   *
   * val names = (json \\ "name").map(_.as[String])
   *
   * assert { names == Seq("localhost", "root", "lupita") }
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
 * import scala.language.implicitConversions
 *
 * import grapple.json.{ Json, JsonValue, given }
 *
 * val json = Json.obj("values" -> Json.arr("abc", 123, true))
 *
 * // Requires jsonValueToJsonValue
 * val list = json("values").as[List[JsonValue]]
 * }}}
 */
given jsonValueToJsonValue: JsonInput[JsonValue] = identity(_)

/** Casts `JsonValue` to `JsonObject`. */
given jsonValueToJsonObject: JsonInput[JsonObject] = _.asInstanceOf[JsonObject]

/** Casts `JsonValue` to `JsonArray`. */
given jsonValueToJsonArray: JsonInput[JsonArray] = _.asInstanceOf[JsonArray]

/** Casts `JsonValue` to `JsonString`. */
given jsonValueToJsonString: JsonInput[JsonString] = _.asInstanceOf[JsonString]

/** Casts `JsonValue` to `JsonNumber`. */
given jsonValueToJsonNumber: JsonInput[JsonNumber] = _.asInstanceOf[JsonNumber]

/** Casts `JsonValue` to `JsonBoolean`. */
given jsonValueToJsonBoolean: JsonInput[JsonBoolean] = _.asInstanceOf[JsonBoolean]

/** Casts `JsonValue` to `JsonNull`. */
given jsonValueToJsonNull: JsonInput[JsonNull] = _.asInstanceOf[JsonNull]

/** Converts `JsonValue` to `String`. */
given jsonValueToString: JsonInput[String] = _.asInstanceOf[JsonString].value

/** Converts `JsonValue` to `Byte`. */
given jsonValueToByte: JsonInput[Byte] = _.asInstanceOf[JsonNumber].byteValue

/** Converts `JsonValue` to `Short`. */
given jsonValueToShort: JsonInput[Short] = _.asInstanceOf[JsonNumber].shortValue

/** Converts `JsonValue` to `Int`. */
given jsonValueToInt: JsonInput[Int] = _.asInstanceOf[JsonNumber].intValue

/** Converts `JsonValue` to `Long`. */
given jsonValueToLong: JsonInput[Long] = _.asInstanceOf[JsonNumber].longValue

/** Converts `JsonValue` to `Float`. */
given jsonValueToFloat: JsonInput[Float] = _.asInstanceOf[JsonNumber].floatValue

/** Converts `JsonValue` to `Double`. */
given jsonValueToDouble: JsonInput[Double] = _.asInstanceOf[JsonNumber].doubleValue

/** Converts `JsonValue` to `BigInt`. */
given jsonValueToBigInt: JsonInput[BigInt] = _.asInstanceOf[JsonNumber].bigIntValue

/** Converts `JsonValue` to `BigDecimal`. */
given jsonValueToBigDecimal: JsonInput[BigDecimal] = _.asInstanceOf[JsonNumber].bigDecimalValue

/** Converts `JsonValue` to `Boolean`. */
given jsonValueToBoolean: JsonInput[Boolean] = _.asInstanceOf[JsonBoolean].value

/** Converts `JsonValue` to collection. */
given jsonValueToCollection[T, M[T]](using converter: JsonInput[T])(using factory: Factory[T, M[T]]): JsonInput[M[T]] =
  _.asInstanceOf[JsonArray].values.map(_.as[T]).to(factory)

/** Converts `JsonValue` to `Option`. */
given jsonValueToOption[T](using converter: JsonInput[T]): JsonInput[Option[T]] =
  value => Option.when(value != JsonNull)(value.as[T])

/** Converts `JsonValue` to `Try`. */
given jsonValueToTry[T](using converter: JsonInput[T]): JsonInput[Try[T]] =
  value => Try(value.as[T])

/** Converts `JsonValue` to `Either`. */
given jsonValueToEither[A, B](using left: JsonInput[A])(using right: JsonInput[B]): JsonInput[Either[A, B]] =
  value => Try(Right(right.read(value))).getOrElse(Left(left.read(value)))

/** Converts `String` to `JsonString`. */
given stringToJsonString: JsonOutput[String] = JsonString(_)

/** Converts `Byte` to `JsonNumber`. */
given byteToJsonNumber: JsonOutput[Byte] = JsonNumber(_)

/** Converts `Short` to `JsonNumber`. */
given shortToJsonNumber: JsonOutput[Short] = JsonNumber(_)

/** Converts `Int` to `JsonNumber`. */
given intToJsonNumber: JsonOutput[Int] = JsonNumber(_)

/** Converts `Long` to `JsonNumber`. */
given longToJsonNumber: JsonOutput[Long] = JsonNumber(_)

/** Converts `Float` to `JsonNumber`. */
given floatToJsonNumber: JsonOutput[Float] = JsonNumber(_)

/** Converts `Double` to `JsonNumber`. */
given doubleToJsonNumber: JsonOutput[Double] = JsonNumber(_)

/** Converts `BigInt` to `JsonNumber`. */
given bigIntToJsonNumber: JsonOutput[BigInt] = JsonNumber(_)

/** Converts `BigDecimal` to `JsonNumber`. */
given bigDecimalToJsonNumber: JsonOutput[BigDecimal] = JsonNumber(_)

/** Converts `Boolean` to `JsonBoolean`. */
given booleanToJsonBoolean: JsonOutput[Boolean] = JsonBoolean(_)

/** Converts `Map` to `JsonObject`. */
given mapToJsonObject[T, M[T] <: Map[String, T]](using converter: JsonOutput[T]): JsonOutput[M[T]] =
  _.foldLeft(JsonObjectBuilder()) {
    case (builder, (name, value)) => builder.add(name, converter.write(value))
  }.toJsonObject()

/** Converts `Array` to `JsonArray`. */
given arrayToJsonArray[T](using converter: JsonOutput[T]): JsonOutput[Array[T]] =
  _.foldLeft(JsonArrayBuilder()) {
    (builder, value) => builder.add(converter.write(value))
  }.toJsonArray()

/** Converts `Iterable` to `JsonArray`. */
given iterableToJsonArray[T, M[T] <: Iterable[T]](using converter: JsonOutput[T]): JsonOutput[M[T]] =
  _.foldLeft(JsonArrayBuilder()) {
    (builder, value) => builder.add(converter.write(value))
  }.toJsonArray()

/** Converts `Some` to `JsonValue` or returns `JsonNull` if `None`. */
given optionToJsonValue[T, M[T] <: Option[T]](using converter: JsonOutput[T]): JsonOutput[M[T]] =
  _.fold(JsonNull)(converter.write(_))

/** Converts `None` to `JsonNull`. */
given noneToJsonNull: JsonOutput[None.type] = _ => JsonNull

/** Converts `Success` to `JsonValue` or returns `JsonNull` if `Failure`. */
given tryToJsonValue[T, M[T] <: Try[T]](using converter: JsonOutput[T]): JsonOutput[M[T]] =
  _.fold(_ => JsonNull, converter.write(_))

/** Converts `Failure` to `JsonNull`. */
given failureToJsonNull: JsonOutput[Failure[?]] = _ => JsonNull

/** Converts `Either` to `JsonValue`. */
given eitherToJsonValue[A, B, M[A, B] <: Either[A, B]](using left: JsonOutput[A])(using right: JsonOutput[B]): JsonOutput[M[A, B]] =
  _.fold(left.write(_), right.write(_))

/** Converts `Right` to `JsonValue`. */
given rightToJsonValue[T](using converter: JsonOutput[T]): JsonOutput[Right[?, T]] =
  _.fold(_ => JsonNull, converter.write(_))

/** Converts `Left` to `JsonValue`. */
given leftToJsonValue[T](using converter: JsonOutput[T]): JsonOutput[Left[T, ?]] =
  _.fold(converter.write(_), _ => JsonNull)

/** Converts `(String, T)` to `(String, JsonValue)`. */
given tupleToJsonField[T](using converter: JsonOutput[T]): Conversion[(String, T), (String, JsonValue)] =
  case (name, value) => name -> converter.write(value)

/** Applies conversion using `JsonInput`. */
given jsonInputConversion[T](using converter: JsonInput[T]): Conversion[JsonValue, T] =
  converter.read(_)

/** Applies conversion using `JsonOutput`. */
given jsonOutputConversion[T](using converter: JsonOutput[T]): Conversion[T, JsonValue] =
  converter.write(_)

/** Applies conversion using `JsonInput[JsonObject]`. */
given toJsonObjectConversion(using converter: JsonInput[JsonObject]): Conversion[JsonValue, JsonObject] =
  converter.read(_)

/** Applies conversion using `JsonInput[JsonArray]`. */
given toJsonArrayConversion(using converter: JsonInput[JsonArray]): Conversion[JsonValue, JsonArray] =
  converter.read(_)
