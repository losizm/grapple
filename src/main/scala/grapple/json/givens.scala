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
 * This is required to perform actions such as the following:
 *
 * {{{
 * import scala.language.implicitConversions
 *
 * import grapple.json.{ Json, JsonValue, given }
 *
 * val json = Json.obj("values" -> Json.arr("abc", 123, true))
 *
 * // Requires jsonValueJsonInput
 * val list = json("values").as[List[JsonValue]]
 * }}}
 */
given jsonValueJsonInput: JsonInput[JsonValue] = identity(_)

/** Casts JSON value to `JsonNull`. */
given jsonNullJsonInput: JsonInput[JsonNull.type] = expect(_)

/** Casts JSON value to `JsonString`. */
given jsonStringJsonInput: JsonInput[JsonString] = expect(_)

/** Casts JSON value to `JsonBoolean`. */
given jsonBooleanJsonInput: JsonInput[JsonBoolean] = expect(_)

/** Casts JSON value to `JsonNumber`. */
given jsonNumberJsonInput: JsonInput[JsonNumber] = expect(_)

/** Casts JSON value to `JsonObject`. */
given jsonObjectJsonInput: JsonInput[JsonObject] = expect(_)

/** Casts JSON value to `JsonArray`. */
given jsonArrayJsonInput: JsonInput[JsonArray] = expect(_)

/** Provides JSON input for `String`. */
given stringJsonInput: JsonInput[String] = expect[JsonString](_).value

/** Provides JSON output for `String`. */
given stringJsonOutput: JsonOutput[String] = JsonString(_)

/** Provides JSON input for `Boolean`. */
given booleanJsonInput: JsonInput[Boolean] = expect[JsonBoolean](_).value

/** Provides JSON output for `Boolean`. */
given booleanJsonOutput: JsonOutput[Boolean] = JsonBoolean(_)

/** Provides JSON input for `Int`. */
given intJsonInput: JsonInput[Int] = expect[JsonNumber](_).toInt

/** Provides JSON output for `Int`. */
given intJsonOutput: JsonOutput[Int] = JsonNumber(_)

/** Provides JSON input for `Long`. */
given longJsonInput: JsonInput[Long] = expect[JsonNumber](_).toLong

/** Provides JSON output for `Long`. */
given longJsonOutput: JsonOutput[Long] = JsonNumber(_)

/** Provides JSON input for `Float`. */
given floatJsonInput: JsonInput[Float] = expect[JsonNumber](_).toFloat

/** Provides JSON output for `Float`. */
given floatJsonOutput: JsonOutput[Float] = JsonNumber(_)

/** Provides JSON input for `Double`. */
given doubleJsonInput: JsonInput[Double] = expect[JsonNumber](_).toDouble

/** Provides JSON output for `Double`. */
given doubleJsonOutput: JsonOutput[Double] = JsonNumber(_)

/** Provides JSON input for `BigInt`. */
given bigIntJsonInput: JsonInput[BigInt] = expect[JsonNumber](_).toBigInt

/** Provides JSON output for `BigInt`. */
given bigIntJsonOutput: JsonOutput[BigInt] = JsonNumber(_)

/** Provides JSON input for `BigDecimal`. */
given bigDecimalJsonInput: JsonInput[BigDecimal] = expect[JsonNumber](_).toBigDecimal

/** Provides JSON output for `BigDecimal`. */
given bigDecimalJsonOutput: JsonOutput[BigDecimal] = JsonNumber(_)

/**
 * Provides JSON input for `Option[T]`.
 *
 * @note `JsonNull` translates to `None`.
 */
given optionJsonInput[T](using input: JsonInput[T]): JsonInput[Option[T]] =
  case JsonNull => None
  case value    => Some(input.read(value))

/**
 * Provides JSON output for `Option[T]`.
 *
 * @note `None` translates to `JsonNull`.
 */
given optionJsonOutput[T, C[T] <: Option[T]](using output: JsonOutput[T]): JsonOutput[C[T]] =
  _.fold(JsonNull)(output.write(_))

/** Provides JSON output for `None`. */
given noneJsonOutput: JsonOutput[None.type] = _ => JsonNull

/**
 * Provides JSON input for `Try[T]`.
 *
 * @note `JsonNull` translates to `Failure`.
 */
given tryJsonInput[T](using input: JsonInput[T]): JsonInput[Try[T]] =
  case JsonNull => Failure(NullPointerException())
  case value    => Try(input.read(value))

/**
 * Provides JSON output for `Try[T]`.
 *
 * @note `Failure` translates to `JsonNull`.
 */
given tryJsonOutput[T, C[T] <: Try[T]](using output: JsonOutput[T]): JsonOutput[C[T]] =
  _.fold(_ => JsonNull, output.write(_))

/** Provides JSON output for `Failure[?]`. */
given failureJsonOutput: JsonOutput[Failure[?]] = _ => JsonNull

/** Provides JSON input for `Either[A, B]`. */
given eitherJsonInput[A, B](using left: JsonInput[A])(using right: JsonInput[B]): JsonInput[Either[A, B]] =
  value => Try(Right(right.read(value))).getOrElse(Left(left.read(value)))

/** Provides JSON output for `Either[A, B]`. */
given eitherJsonOutput[A, B, C[A, B] <: Either[A, B]](using left: JsonOutput[A])(using right: JsonOutput[B]): JsonOutput[C[A, B]] =
  _.fold(left.write(_), right.write(_))

/** Provides JSON output for `Right[?, T]`. */
given rightJsonOutput[T](using output: JsonOutput[T]): JsonOutput[Right[?, T]] =
  _.fold(_ => JsonNull, output.write(_))

/** Provides JSON output for `Left[?, T]`. */
given leftJsonOutput[T](using output: JsonOutput[T]): JsonOutput[Left[T, ?]] =
  _.fold(output.write(_), _ => JsonNull)

/** Provides JSON input for `Array[T]`. */
given arrayJsonInput[T](using input: JsonInput[T])(using ctag: ClassTag[T]): JsonInput[Array[T]] =
  expect[JsonArray](_).values.map(input.read).toArray

/** Provides JSON output for `Array[T]`. */
given arrayJsonOutput[T](using output: JsonOutput[T]): JsonOutput[Array[T]] =
  _.foldLeft(JsonArrayBuilder()) {
    (builder, value) => builder.add(output.write(value))
  }.toJsonArray()

/** Provides JSON input for `Iterable[T]`. */
given iterableJsonInput[T, C[T] <: Iterable[T]](using input: JsonInput[T])(using factory: Factory[T, C[T]]): JsonInput[C[T]] =
  expect[JsonArray](_).values.foldLeft(factory.newBuilder) {
    (builder, value) => builder += input.read(value)
  }.result

/** Provides JSON output for `Iterable[T]`. */
given iterableJsonOutput[T, C[T] <: Iterable[T]](using output: JsonOutput[T]): JsonOutput[C[T]] =
  _.foldLeft(JsonArrayBuilder()) {
    (builder, value) => builder.add(output.write(value))
  }.toJsonArray()

/** Provides JSON input for `Map[String, T]`. */
given mapJsonInput[T, C[T] <: Map[String, T]](using input: JsonInput[T])(using factory: Factory[(String, T), C[T]]): JsonInput[C[T]] =
  expect[JsonObject](_).fields.foldLeft(factory.newBuilder) {
    case (builder, (key, value)) => builder += key -> input.read(value)
  }.result

/** Provides JSON output for `Map[String, T]`. */
given mapJsonOutput[T, C[T] <: Map[String, T]](using output: JsonOutput[T]): JsonOutput[C[T]] =
  _.foldLeft(JsonObjectBuilder()) {
    case (builder, (key, value)) => builder.add(key, output.write(value))
  }.toJsonObject()

/** Applies JSON input conversion. */
given jsonInputConversion[T](using input: JsonInput[T]): Conversion[JsonValue, T] =
  input.read(_)

/** Applies JSON output conversion. */
given jsonOutputConversion[T](using output: JsonOutput[T]): Conversion[T, JsonValue] =
  output.write(_)

/** Applies JSON output conversion to field. */
given fieldsJsonOutputConversion[T](using output: JsonOutput[T]): Conversion[(String, T), (String, JsonValue)] =
  _ -> output.write(_)

/** Converts JSON value to `JsonStructureFacade`. */
given jsonStructureFacadeConversion: Conversion[JsonValue, JsonStructureFacade] =
  json => JsonStructureFacade(expect(json))
