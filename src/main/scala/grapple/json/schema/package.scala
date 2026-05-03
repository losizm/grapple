/*
 * Copyright 2026 Carlos Conyers
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
package schema

import java.net.URI

import scala.reflect.ClassTag

/** Defines type alias for nullable. */
type Nullable[T] = T | Null

/** Defines type alias for schema dialect parameter. */
type URIParam = URI | String

/** Defines type alias for schema dialect parameter. */
type DialectParam = Dialect | URI | String

/** Defines type alias for data type parameter. */
type DataTypeParam = DataType | String

/** Defines type alias for data type list parameter. */
type DataTypeListParam = DataTypeParam | Seq[DataTypeParam]

/** Defines type alias for content encoding parameter. */
type EncodingParam = Encoding | String

/** Defines type alias for JSON schema parameter. */
type JsonSchemaParam = JsonSchema | DataType | String | JsonBoolean | Boolean

private def safeAccept[T, U](value: Nullable[T])(f: T => U)(using ClassTag[T]): Unit =
  value match
    case x: T => f(x)
    case _    => ()

private def safeOption[T](value: Nullable[T])(using ClassTag[T]): Option[T] =
  value match
    case x: T => Option(x)
    case _    => None

private def ToURI: PartialFunction[URIParam, URI] =
  case value: URI    => value
  case value: String => URI(value)

private def ToDialect: PartialFunction[DialectParam, Dialect] =
  case value: Dialect => value
  case value: URI     => Dialect(value)
  case value: String  => Dialect(value)

private def ToDataType: PartialFunction[DataTypeParam, DataType] =
  case value: DataType => value
  case value: String   => DataType(value)

private def ToEncoding: PartialFunction[EncodingParam, Encoding] =
  case value: Encoding => value
  case value: String   => Encoding(value)

private def ToJsonSchema: PartialFunction[JsonSchemaParam, JsonSchema] =
  case value: JsonSchema  => value
  case value: DataType    => ObjectJsonSchema(kind = Seq(value))
  case value: String      => ObjectJsonSchema(kind = Seq(DataType(value)))
  case value: JsonBoolean => BooleanJsonSchema(value.value)
  case value: Boolean     => BooleanJsonSchema(value)
