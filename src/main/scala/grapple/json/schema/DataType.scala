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

/**
 * Defines data type enumeration.
 *
 * @param name data type name
 */
enum DataType(val name: String):
  /**
   * Defines null data type.
   *
   * @note `DataType("null")`
   */
  case NullType extends DataType("null")

  /**
   * Defines boolean data type.
   *
   * @note `DataType("boolean")`
   */
  case BooleanType extends DataType("boolean")

  /**
   * Defines number data type.
   *
   * @note `DataType("number")`
   */
  case NumberType extends DataType("number")

  /**
   * Defines integer data type.
   *
   * @note `DataType("integer")`
   */
  case IntegerType extends DataType("integer")

  /**
   * Defines string data type.
   *
   * @note `DataType("string")`
   */
  case StringType extends DataType("string")

  /**
   * Defines array data type.
   *
   * @note `DataType("array")`
   */
  case ArrayType extends DataType("array")

  /**
   * Defines object data type.
   *
   * @note `DataType("object")`
   */
  case ObjectType extends DataType("object")

/** Provides data type lookup by name. */
object DataType:
  /**
   * Gets data type by name.
   *
   * @param name data type name
   *
   * @return data type
   */
  def apply(name: String): DataType =
    name match
      case NullType.name    => NullType
      case BooleanType.name => BooleanType
      case NumberType.name  => NumberType
      case IntegerType.name => IntegerType
      case StringType.name  => StringType
      case ArrayType.name   => ArrayType
      case ObjectType.name  => ObjectType
      case _                => throw JsonSchemaException(s"Unrecognized data type: $name")
