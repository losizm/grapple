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

private inline def expect[T <: JsonValue](value: JsonValue)(using ctag: ClassTag[T]): T =
  try
    value.asInstanceOf[T]
  catch case _: ClassCastException =>
    throw JsonExpectationError(ctag.runtimeClass, jsonValueType(value))

private def jsonValueType[T <: JsonValue](value: JsonValue): Class[_] =
  value match
    case JsonNull               => classOf[JsonNull.type]
    case _: JsonString          => classOf[JsonString]
    case _: JsonNumber          => classOf[JsonNumber]
    case _: JsonBoolean         => classOf[JsonBoolean]
    case f: JsonStructureFacade => jsonValueType(f.unwrap)
    case _: JsonObject          => classOf[JsonObject]
    case _: JsonArray           => classOf[JsonArray]
