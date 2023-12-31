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

import scala.collection.Factory
import scala.util.{ Failure, Try }

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

/** Casts `JsonValue` to `JsonNull`. */
given jsonValueToJsonNull: JsonInput[JsonNull.type] = _.asInstanceOf[JsonNull.type]

/** Casts `JsonValue` to `JsonString`. */
given jsonValueToJsonString: JsonInput[JsonString] = _.asInstanceOf[JsonString]

/** Casts `JsonValue` to `JsonBoolean`. */
given jsonValueToJsonBoolean: JsonInput[JsonBoolean] = _.asInstanceOf[JsonBoolean]

/** Casts `JsonValue` to `JsonNumber`. */
given jsonValueToJsonNumber: JsonInput[JsonNumber] = _.asInstanceOf[JsonNumber]

/** Casts `JsonValue` to `JsonObject`. */
given jsonValueToJsonObject: JsonInput[JsonObject] = _.asInstanceOf[JsonObject]

/** Casts `JsonValue` to `JsonArray`. */
given jsonValueToJsonArray: JsonInput[JsonArray] = _.asInstanceOf[JsonArray]

/** Converts `JsonValue` to `String`. */
given jsonValueToString: JsonInput[String] = _.asInstanceOf[JsonString].value

/** Converts `JsonValue` to `Boolean`. */
given jsonValueToBoolean: JsonInput[Boolean] = _.asInstanceOf[JsonBoolean].value

/** Converts `JsonValue` to `Int`. */
given jsonValueToInt: JsonInput[Int] = _.asInstanceOf[JsonNumber].toInt

/** Converts `JsonValue` to `Long`. */
given jsonValueToLong: JsonInput[Long] = _.asInstanceOf[JsonNumber].toLong

/** Converts `JsonValue` to `Float`. */
given jsonValueToFloat: JsonInput[Float] = _.asInstanceOf[JsonNumber].toFloat

/** Converts `JsonValue` to `Double`. */
given jsonValueToDouble: JsonInput[Double] = _.asInstanceOf[JsonNumber].toDouble

/** Converts `JsonValue` to `BigInt`. */
given jsonValueToBigInt: JsonInput[BigInt] = _.asInstanceOf[JsonNumber].toBigInt

/** Converts `JsonValue` to `BigDecimal`. */
given jsonValueToBigDecimal: JsonInput[BigDecimal] = _.asInstanceOf[JsonNumber].toBigDecimal

/** Converts `JsonValue` to `Map`. */
given jsonValueToMap[T, M[T] <: Map[String, T]](using input: JsonInput[T])(using factory: Factory[(String, T), M[T]]): JsonInput[M[T]] =
  _.asInstanceOf[JsonObject].fields.foldLeft(factory.newBuilder) {
    case (builder, (key, value)) => builder += key -> input.read(value)
  }.result

/** Converts `JsonValue` to collection. */
given jsonValueToCollection[T, M[T]](using input: JsonInput[T])(using factory: Factory[T, M[T]]): JsonInput[M[T]] =
  _.asInstanceOf[JsonArray].values.foldLeft(factory.newBuilder) {
    (builder, value) => builder += input.read(value)
  }.result

/** Converts `JsonValue` to `Option`. */
given jsonValueToOption[T](using input: JsonInput[T]): JsonInput[Option[T]] =
  case JsonNull => None
  case value    => Some(input.read(value))

/** Converts `JsonValue` to `Try`. */
given jsonValueToTry[T](using input: JsonInput[T]): JsonInput[Try[T]] =
  case JsonNull => Failure(NullPointerException())
  case value    => Try(input.read(value))

/** Converts `JsonValue` to `Either`. */
given jsonValueToEither[A, B](using left: JsonInput[A])(using right: JsonInput[B]): JsonInput[Either[A, B]] =
  value => Try(Right(right.read(value))).getOrElse(Left(left.read(value)))

/** Converts `String` to `JsonString`. */
given stringToJsonString: JsonOutput[String] = JsonString(_)

/** Converts `Boolean` to `JsonBoolean`. */
given booleanToJsonBoolean: JsonOutput[Boolean] = JsonBoolean(_)

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

/** Converts `Map` to `JsonObject`. */
given mapToJsonObject[T, M[T] <: Map[String, T]](using output: JsonOutput[T]): JsonOutput[M[T]] =
  _.foldLeft(JsonObjectBuilder()) {
    case (builder, (key, value)) => builder.add(key, output.write(value))
  }.toJsonObject()

/** Converts `Array` to `JsonArray`. */
given arrayToJsonArray[T](using output: JsonOutput[T]): JsonOutput[Array[T]] =
  _.foldLeft(JsonArrayBuilder()) {
    (builder, value) => builder.add(output.write(value))
  }.toJsonArray()

/** Converts `Iterable` to `JsonArray`. */
given iterableToJsonArray[T, M[T] <: Iterable[T]](using output: JsonOutput[T]): JsonOutput[M[T]] =
  _.foldLeft(JsonArrayBuilder()) {
    (builder, value) => builder.add(output.write(value))
  }.toJsonArray()

/** Converts `Some` to `JsonValue` or returns `JsonNull` if `None`. */
given optionToJsonValue[T, M[T] <: Option[T]](using output: JsonOutput[T]): JsonOutput[M[T]] =
  _.fold(JsonNull)(output.write(_))

/** Converts `None` to `JsonNull`. */
given noneToJsonNull: JsonOutput[None.type] = _ => JsonNull

/** Converts `Success` to `JsonValue` or returns `JsonNull` if `Failure`. */
given tryToJsonValue[T, M[T] <: Try[T]](using output: JsonOutput[T]): JsonOutput[M[T]] =
  _.fold(_ => JsonNull, output.write(_))

/** Converts `Failure` to `JsonNull`. */
given failureToJsonNull: JsonOutput[Failure[?]] = _ => JsonNull

/** Converts `Either` to `JsonValue`. */
given eitherToJsonValue[A, B, M[A, B] <: Either[A, B]](using left: JsonOutput[A])(using right: JsonOutput[B]): JsonOutput[M[A, B]] =
  _.fold(left.write(_), right.write(_))

/** Converts `Right` to `JsonValue`. */
given rightToJsonValue[T](using output: JsonOutput[T]): JsonOutput[Right[?, T]] =
  _.fold(_ => JsonNull, output.write(_))

/** Converts `Left` to `JsonValue`. */
given leftToJsonValue[T](using output: JsonOutput[T]): JsonOutput[Left[T, ?]] =
  _.fold(output.write(_), _ => JsonNull)

/** Applies conversion using `JsonInput`. */
given jsonInputConversion[T](using input: JsonInput[T]): Conversion[JsonValue, T] =
  input.read(_)

/** Applies conversion using `JsonOutput`. */
given jsonOutputConversion[T](using output: JsonOutput[T]): Conversion[T, JsonValue] =
  output.write(_)

/** Converts `(String, T)` to `(String, JsonValue)`. */
given jsonFieldConversion[T](using output: JsonOutput[T]): Conversion[(String, T), (String, JsonValue)] =
  case (key, value) => key -> output.write(value)

/**
 * Converts `JsonValue` to `JsonStructureFacade`.
 *
 * @throws java.lang.ClassCastException if not JSON structure
 */
given jsonStructureFacadeConversion: Conversion[JsonValue, JsonStructureFacade] =
  json => JsonStructureFacade(json.asInstanceOf[JsonStructure])
