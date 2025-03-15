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
import scala.reflect.ClassTag
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
given jsonValueJsonInput: JsonInput[JsonValue] = identity(_)

/** Casts `JsonValue` to `JsonNull`. */
given jsonNullJsonInput: JsonInput[JsonNull.type] = expect(_)

/** Casts `JsonValue` to `JsonString`. */
given jsonStringJsonInput: JsonInput[JsonString] = expect(_)

/** Casts `JsonValue` to `JsonBoolean`. */
given jsonBooleanJsonInput: JsonInput[JsonBoolean] = expect(_)

/** Casts `JsonValue` to `JsonNumber`. */
given jsonNumberJsonInput: JsonInput[JsonNumber] = expect(_)

/** Casts `JsonValue` to `JsonObject`. */
given jsonObjectJsonInput: JsonInput[JsonObject] = expect(_)

/** Casts `JsonValue` to `JsonArray`. */
given jsonArrayJsonInput: JsonInput[JsonArray] = expect(_)

/** Converts `JsonValue` to `String`. */
given stringJsonInput: JsonInput[String] = expect[JsonString](_).value

/** Converts `String` to `JsonString`. */
given stringJsonOutput: JsonOutput[String] = JsonString(_)

/** Converts `JsonValue` to `Boolean`. */
given booleanJsonInput: JsonInput[Boolean] = expect[JsonBoolean](_).value

/** Converts `Boolean` to `JsonBoolean`. */
given booleanJsonOutput: JsonOutput[Boolean] = JsonBoolean(_)

/** Converts `JsonValue` to `Int`. */
given intJsonInput: JsonInput[Int] = expect[JsonNumber](_).toInt

/** Converts `Int` to `JsonNumber`. */
given intJsonOutput: JsonOutput[Int] = JsonNumber(_)

/** Converts `JsonValue` to `Long`. */
given longJsonInput: JsonInput[Long] = expect[JsonNumber](_).toLong

/** Converts `Long` to `JsonNumber`. */
given longJsonOutput: JsonOutput[Long] = JsonNumber(_)

/** Converts `JsonValue` to `Float`. */
given floatJsonInput: JsonInput[Float] = expect[JsonNumber](_).toFloat

/** Converts `Float` to `JsonNumber`. */
given floatJsonOutput: JsonOutput[Float] = JsonNumber(_)

/** Converts `JsonValue` to `Double`. */
given doubleJsonInput: JsonInput[Double] = expect[JsonNumber](_).toDouble

/** Converts `Double` to `JsonNumber`. */
given doubleJsonOutput: JsonOutput[Double] = JsonNumber(_)

/** Converts `JsonValue` to `BigInt`. */
given bigIntJsonInput: JsonInput[BigInt] = expect[JsonNumber](_).toBigInt

/** Converts `BigInt` to `JsonNumber`. */
given bigIntJsonOutput: JsonOutput[BigInt] = JsonNumber(_)

/** Converts `JsonValue` to `BigDecimal`. */
given bigDecimalJsonInput: JsonInput[BigDecimal] = expect[JsonNumber](_).toBigDecimal

/** Converts `BigDecimal` to `JsonNumber`. */
given bigDecimalJsonOutput: JsonOutput[BigDecimal] = JsonNumber(_)

given optionJsonInput[T](using input: JsonInput[T]): JsonInput[Option[T]] =
  case JsonNull => None
  case value    => Some(input.read(value))

/** Converts `Some` to `JsonValue` or returns `JsonNull` if `None`. */
given optionJsonOutput[T, C[T] <: Option[T]](using output: JsonOutput[T]): JsonOutput[C[T]] =
  _.fold(JsonNull)(output.write(_))

/** Converts `None` to `JsonNull`. */
given noneJsonOutput: JsonOutput[None.type] = _ => JsonNull

/** Converts `JsonValue` to `Try`. */
given tryJsonInput[T](using input: JsonInput[T]): JsonInput[Try[T]] =
  case JsonNull => Failure(NullPointerException())
  case value    => Try(input.read(value))

/** Converts `Success` to `JsonValue` or returns `JsonNull` if `Failure`. */
given tryJsonOutput[T, C[T] <: Try[T]](using output: JsonOutput[T]): JsonOutput[C[T]] =
  _.fold(_ => JsonNull, output.write(_))

/** Converts `Failure` to `JsonNull`. */
given failureJsonOutput: JsonOutput[Failure[?]] = _ => JsonNull

/** Converts `JsonValue` to `Either`. */
given eitherJsonInput[A, B](using left: JsonInput[A])(using right: JsonInput[B]): JsonInput[Either[A, B]] =
  value => Try(Right(right.read(value))).getOrElse(Left(left.read(value)))

/** Converts `Either` to `JsonValue`. */
given eitherJsonOutput[A, B, C[A, B] <: Either[A, B]](using left: JsonOutput[A])(using right: JsonOutput[B]): JsonOutput[C[A, B]] =
  _.fold(left.write(_), right.write(_))

/** Converts `Right` to `JsonValue`. */
given rightJsonOutput[T](using output: JsonOutput[T]): JsonOutput[Right[?, T]] =
  _.fold(_ => JsonNull, output.write(_))

/** Converts `Left` to `JsonValue`. */
given leftJsonOutput[T](using output: JsonOutput[T]): JsonOutput[Left[T, ?]] =
  _.fold(output.write(_), _ => JsonNull)

/** Converts `JsonValue` to `Array`. */
given arrayJsonInput[T](using input: JsonInput[T])(using ctag: ClassTag[T]): JsonInput[Array[T]] =
  expect[JsonArray](_).values.map(input.read).toArray

/** Converts `Array` to `JsonArray`. */
given arrayJsonOutput[T](using output: JsonOutput[T]): JsonOutput[Array[T]] =
  _.foldLeft(JsonArrayBuilder()) {
    (builder, value) => builder.add(output.write(value))
  }.toJsonArray()

/** Converts `JsonValue` to `Iterable`. */
given iterableJsonInput[T, C[T] <: Iterable[T]](using input: JsonInput[T])(using factory: Factory[T, C[T]]): JsonInput[C[T]] =
  expect[JsonArray](_).values.foldLeft(factory.newBuilder) {
    (builder, value) => builder += input.read(value)
  }.result

/** Converts `Iterable` to `JsonArray`. */
given iterableJsonOutput[T, C[T] <: Iterable[T]](using output: JsonOutput[T]): JsonOutput[C[T]] =
  _.foldLeft(JsonArrayBuilder()) {
    (builder, value) => builder.add(output.write(value))
  }.toJsonArray()

/** Converts `JsonValue` to `Map`. */
given mapJsonInput[T, C[T] <: Map[String, T]](using input: JsonInput[T])(using factory: Factory[(String, T), C[T]]): JsonInput[C[T]] =
  expect[JsonObject](_).fields.foldLeft(factory.newBuilder) {
    case (builder, (key, value)) => builder += key -> input.read(value)
  }.result

/** Converts `Map` to `JsonObject`. */
given mapJsonOutput[T, C[T] <: Map[String, T]](using output: JsonOutput[T]): JsonOutput[C[T]] =
  _.foldLeft(JsonObjectBuilder()) {
    case (builder, (key, value)) => builder.add(key, output.write(value))
  }.toJsonObject()

/** Converts `JsonValue` to `Option`. */
/** Applies conversion using `JsonInput`. */
given jsonInputConversion[T](using input: JsonInput[T]): Conversion[JsonValue, T] =
  input.read(_)

/** Applies conversion using `JsonOutput`. */
given jsonOutputConversion[T](using output: JsonOutput[T]): Conversion[T, JsonValue] =
  output.write(_)

/** Converts `(String, T)` to `(String, JsonValue)`. */
given jsonFieldConversion[T](using output: JsonOutput[T]): Conversion[(String, T), (String, JsonValue)] =
  _ -> output.write(_)

/** Converts `JsonValue` to `JsonStructureFacade`. */
given jsonStructureFacadeConversion: Conversion[JsonValue, JsonStructureFacade] =
  json => JsonStructureFacade(expect(json))
