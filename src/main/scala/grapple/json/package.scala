/*
 * Copyright 2025 Carlos Conyers
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

import scala.reflect.ClassTag

/** Defines type alias for JSON value parameter. */
type JsonValueParam = JsonValue | String | Boolean | Int | Long | Float | Double | BigInt | BigDecimal

private def ToJsonValue: PartialFunction[JsonValueParam, JsonValue] =
  case value: JsonValue  => value
  case value: String     => JsonString(value)
  case value: Boolean    => JsonBoolean(value)
  case value: Int        => JsonNumber(value)
  case value: Long       => JsonNumber(value)
  case value: Float      => JsonNumber(value)
  case value: Double     => JsonNumber(value)
  case value: BigInt     => JsonNumber(value)
  case value: BigDecimal => JsonNumber(value)

private inline def expect[T <: JsonValue](value: JsonValue)(using ctag: ClassTag[T]): T =
  try
    value.asInstanceOf[T]
  catch case _: ClassCastException =>
    throw JsonExpectationError(s"Expected ${ctag.runtimeClass.getSimpleName} instead of ${JsonValueName(value)}")

private def JsonValueName[T <: JsonValue]: PartialFunction[T, String] =
  case JsonNull               => "JsonNull"
  case _: JsonString          => "JsonString"
  case _: JsonNumber          => "JsonNumber"
  case _: JsonBoolean         => "JsonBoolean"
  case _: JsonObject          => "JsonObject"
  case _: JsonArray           => "JsonArray"
