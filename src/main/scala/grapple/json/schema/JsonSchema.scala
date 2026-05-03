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

import java.io.{ File, InputStream, OutputStream, Reader, Writer }
import java.net.URI
import java.nio.file.{ Files, Path }

/** Defines JSON schema. */
sealed trait JsonSchema:
  /** Gets schema dialect. */
  def $schema: Option[Dialect]

  /** Gets schema identifier. */
  def $id: Option[URI]

  /** Gets dynamic schema reference. */
  def $dynamicRef: Option[URI]

  /** Gets dynamic anchor. */
  def $dynamicAnchor: Option[String]

  /** Gets vocabulary. */
  def $vocabulary: Map[String, Boolean]

  /** Gets schema reference. */
  def $ref: Option[URI]

  /** Gets anchor. */
  def $anchor: Option[String]

  /** Gets comments. */
  def $comments: Option[String]

  /** Gets schema definitions. */
  def $defs: Map[String, JsonSchema]

  /** Gets title. */
  def title: Option[String]

  /** Gets description. */
  def description: Option[String]

  /** Gets data types. */
  def kind: Seq[DataType]

  /** Gets default value. */
  def defaultValue: Option[JsonValue]

  /** Gets constant value. */
  def constValue: Option[JsonValue]

  /** Gets enum values. */
  def enumValues: Option[Seq[JsonValue]]

  /**
   * Gets minimum length.
   *
   * @note For string type.
   */
  def minLength: Option[Int]

  /**
   * Gets maximum length.
   *
   * @note For string type.
   */
  def maxLength: Option[Int]

  /**
   * Gets format.
   *
   * @note For string type.
   */
  def format: Option[String]

  /**
   * Gets pattern.
   *
   * @note For string type.
   */
  def pattern: Option[String]

  /**
   * Gets content media type.
   *
   * @note For string type.
   */
  def contentMediaType: Option[String]

  /**
   * Gets content media encoding.
   *
   * @note For string type.
   */
  def contentMediaEncoding: Option[Encoding]

  /**
   * Gets content schema.
   *
   * @note For string type.
   */
  def contentSchema: Option[JsonSchema]

  /**
   * Gets minimum value.
   *
   * @note For number type.
   */
  def minimum: Option[BigDecimal]

  /**
   * Gets exclusive minimum value.
   *
   * @note For number type.
   */
  def exclusiveMinimum: Option[BigDecimal]

  /**
   * Gets maximum value.
   *
   * @note For number type.
   */
  def maximum: Option[BigDecimal]

  /**
   * Gets exclusive maximum value.
   *
   * @note For number type.
   */
  def exclusiveMaximum: Option[BigDecimal]

  /**
   * Gets multiple-of value.
   *
   * @note For number type.
   */
  def multipleOf: Option[BigDecimal]

  /**
   * Gets prefix items.
   *
   * @note For array type.
   */
  def prefixItems: Option[Seq[JsonSchema]]

  /**
   * Gets items.
   *
   * @note For array type.
   */
  def items: Option[JsonSchema]

  /**
   * Gets maximum number of items.
   *
   * @note For array type.
   */
  def minItems: Option[Int]

  /**
   * Gets minimum numberof items.
   *
   * @note For array type.
   */
  def maxItems: Option[Int]

  /**
   * Gets contains.
   *
   * @note For array type.
   */
  def contains: Option[JsonSchema]

  /**
   * Gets minimum number of contains.
   *
   * @note For array type.
   */
  def minContains: Option[Int]

  /**
   * Gets maximum number of contains.
   *
   * @note For array type.
   */
  def maxContains: Option[Int]

  /**
   * Gets unique items indicator.
   *
   * @note For array type.
   */
  def uniqueItems: Option[Boolean]

  /**
   * Gets additional items indicator.
   *
   * @note For array type.
   */
  def additionalItems: Option[JsonSchema]

  /**
   * Gets unevaluated items indicator.
   *
   * @note For array type.
   */
  def unevaluatedItems: Option[JsonSchema]

  /**
   * Gets schema for property names.
   *
   * @note For object type.
   */
  def propertyNames: Option[JsonSchema]

  /**
   * Gets pattern properties.
   *
   * @note For object type.
   */
  def patternProperties: Map[String, JsonSchema]

  /**
   * Gets properties.
   *
   * @note For object type.
   */
  def properties: Map[String, JsonSchema]

  /**
   * Gets minimum number of properties.
   *
   * @note For object type.
   */
  def minProperties: Option[Int]

  /**
   * Gets maximum number of properties.
   *
   * @note For object type.
   */
  def maxProperties: Option[Int]

  /**
   * Gets names of required properties.
   *
   * @note For object type.
   */
  def required: Option[Seq[String]]

  /**
   * Gets dependent required properties.
   *
   * @note For object type.
   */
  def dependentRequired: Map[String, Seq[String]]

  /**
   * Gets dependent schemas.
   *
   * @note For object type.
   */
  def dependentSchemas: Map[String, Seq[JsonSchema]]

  /**
   * Gets additional properties indicator.
   *
   * @note For object type.
   */
  def additionalProperties: Option[JsonSchema]

  /**
   * Gets unevaluated properties indicator.
   *
   * @note For object type.
   */
  def unevaluatedProperties: Option[JsonSchema]

  /** Gets all-of schemas. */
  def allOf: Option[Seq[JsonSchema]]

  /** Gets any-of schemas. */
  def anyOf: Option[Seq[JsonSchema]]

  /** Gets one-of schemas. */
  def oneOf: Option[Seq[JsonSchema]]

  /** Gets not schema. */
  def not: Option[JsonSchema]

  /** Gets if conditional schema. */
  def ifApply: Option[JsonSchema]

  /** Gets then conditional schema. */
  def thenApply: Option[JsonSchema]

  /** Gets else conditional schema. */
  def elseApply: Option[JsonSchema]

  /** Gets example values. */
  def exampleValues: Seq[JsonValue]

  /** Gets read-only indicator. */
  def readOnly: Option[Boolean]

  /** Gets write-only indicator. */
  def writeOnly: Option[Boolean]

  /** Gets deprecated indicator. */
  def deprecated: Option[Boolean]

  /**
   * Gets JSON schema as JSON value.
   *
   * @return JSON value
   */
  lazy val toJsonValue = Json.toJson(this)

  /** Indicates whether schema is a boolean value. */
  lazy val isBoolean: Boolean = isTrue || isFalse

  /** Indicates whether schema is `true`. */
  lazy val isTrue: Boolean = toJsonValue == JsonTrue

  /** Indicates whether schema is `false`. */
  lazy val isFalse: Boolean = toJsonValue == JsonFalse

  /** Dumps JSON schema. */
  def dump(): String =
    Json.toPrettyPrint(toJsonValue)

  /**
   * Dumps JSON schema.
   *
   * @param out target
   *
   * @return JSON schema
   */
  def dump(out: File): Unit =
    Files.writeString(out.toPath, Json.toPrettyPrint(toJsonValue))

  /**
   * Dumps JSON schema.
   *
   * @param out target
   *
   * @return JSON schema
   */
  def dump(out: Path): Unit =
    Files.writeString(out, Json.toPrettyPrint(toJsonValue))

  /**
   * Dumps JSON schema.
   *
   * @param out target
   *
   * @return JSON schema
   */
  def dump(out: Writer): Unit =
    out.write(Json.toPrettyPrint(toJsonValue))
    out.flush()

  /**
   * Dumps JSON schema.
   *
   * @param out target
   *
   * @return JSON schema
   */
  def dump(out: OutputStream): Unit =
    out.write(Json.toPrettyPrint(toJsonValue).getBytes("UTF-8"))
    out.flush()

/** Provides JSON schema factory and loaders. */
object JsonSchema:
  /**
   * Creates JSON schema.
   *
   * @param $schema sets schema dialect
   * @param $id sets schema identifier
   * @param $ref sets schema reference
   * @param title sets title
   * @param description sets description
   * @param kind sets data types
   * @param defaultValue sets default value
   * @param constValue sets constant value
   * @param enumValues sets enum values
   * @param minimum sets minimum value
   * @param maximum sets maximum value
   * @param minLength sets minimum length
   * @param maxLength sets maximum length
   * @param format sets format
   * @param pattern sets pattern
   * @param items sets items
   * @param minItems sets minimum number of items
   * @param maxItems sets maximum number of items
   * @param properties sets properties
   * @param minProperties sets minimum number of properties
   * @param maxProperties sets maximum number of properties
   * @param required sets names of required properties
   *
   * @return JSON schema
   *
   * @see [[JsonSchemaBuilder]] for building schemas using full set of JSON
   * Schema keywords.
   *
   * @note All parameters have default values such that they not set in
   * constructed schema unless explicitly supplied.
   */
  def apply(
    $schema: Nullable[DialectParam]                    = null,
    $id: Nullable[URIParam]                            = null,
    $ref: Nullable[URIParam]                           = null,
    title: Nullable[String]                            = null,
    description: Nullable[String]                      = null,
    kind: Nullable[DataTypeListParam]                  = null,
    defaultValue: Nullable[JsonValueParam]             = null,
    constValue: Nullable[JsonValueParam]               = null,
    enumValues: Nullable[Seq[JsonValueParam]]          = null,
    minimum: Nullable[BigDecimal]                      = null,
    maximum: Nullable[BigDecimal]                      = null,
    minLength: Nullable[Int]                           = null,
    maxLength: Nullable[Int]                           = null,
    format: Nullable[String]                           = null,
    pattern: Nullable[String]                          = null,
    items: Nullable[JsonSchemaParam]                   = null,
    minItems: Nullable[Int]                            = null,
    maxItems: Nullable[Int]                            = null,
    properties: Nullable[Map[String, JsonSchemaParam]] = null,
    minProperties: Nullable[Int]                       = null,
    maxProperties: Nullable[Int]                       = null,
    required: Nullable[Seq[String]]                    = null
  ): JsonSchema =
    val schema = JsonSchemaBuilder()
    safeAccept($schema)       { schema.setSchema(_) }
    safeAccept($id)           { schema.setId(_) }
    safeAccept($ref)          { schema.setRef(_) }
    safeAccept(title)         { schema.setTitle(_) }
    safeAccept(description)   { schema.setDescription(_) }

    safeAccept(kind) {
      _ match
        case value: DataTypeParam      => schema.setKind(value)
        case value: Seq[DataTypeParam] => schema.setKind(value)
    }

    safeAccept(defaultValue)  { schema.setDefaultValue(_) }
    safeAccept(constValue)    { schema.setConstValue(_) }
    safeAccept(enumValues)    { schema.setEnumValues(_) }
    safeAccept(minimum)       { schema.setMinimum(_) }
    safeAccept(maximum)       { schema.setMaximum(_) }
    safeAccept(minLength)     { schema.setMinLength(_) }
    safeAccept(maxLength)     { schema.setMaxLength(_) }
    safeAccept(format)        { schema.setFormat(_) }
    safeAccept(pattern)       { schema.setPattern(_) }
    safeAccept(items)         { schema.setItems(_) }
    safeAccept(minItems)      { schema.setMinItems(_) }
    safeAccept(maxItems)      { schema.setMaxItems(_) }
    safeAccept(properties)    { schema.setProperties(_) }
    safeAccept(minProperties) { schema.setMinProperties(_) }
    safeAccept(maxProperties) { schema.setMaxProperties(_) }
    safeAccept(required)      { schema.setRequired(_) }
    schema.toJsonSchema()

  /**
   * Creates JSON schema from supplied JSON value.
   *
   * @param value JSON value
   *
   * @return JSON schema
   *
   * @note A JSON schema can be created from a boolean value:
   *
   * {{{
   * import grapple.json.JsonTrue
   * import grapple.json.schema.JsonSchema
   *
   * val schema = JsonSchema.from(true)
   *
   * assert { schema.isTrue }
   * assert { schema.toJsonValue == JsonTrue }
   * }}}
   */
  def from(value: JsonValueParam): JsonSchema =
    ToJsonValue(value).as[JsonSchema]

  /**
   * Loads JSON schema.
   *
   * @param in source
   *
   * @return JSON schema
   */
  def load(in: String): JsonSchema =
    Json.parseAny(in).as[JsonSchema]

  /**
   * Loads JSON schema.
   *
   * @param in source
   *
   * @return JSON schema
   */
  def load(in: File): JsonSchema =
    Json.parseAny(in).as[JsonSchema]

  /**
   * Loads JSON schema.
   *
   * @param in source
   *
   * @return JSON schema
   */
  def load(in: Path): JsonSchema =
    Json.parseAny(in).as[JsonSchema]

  /**
   * Loads JSON schema.
   *
   * @param in source
   *
   * @return JSON schema
   */
  def load(in: Reader): JsonSchema =
    Json.parseAny(in).as[JsonSchema]

  /**
   * Loads JSON schema.
   *
   * @param in source
   *
   * @return JSON schema
   */
  def load(in: InputStream): JsonSchema =
    Json.parseAny(in).as[JsonSchema]

  /**
   * Loads JSON schema.
   *
   * @param in source
   *
   * @return JSON schema
   */
  def load(in: URI): JsonSchema =
    Json.parseAny(in).as[JsonSchema]

private case class ObjectJsonSchema(
  $schema: Option[Dialect] = None,
  $id: Option[URI] = None,
  $dynamicRef: Option[URI] = None,
  $dynamicAnchor: Option[String] = None,
  $vocabulary: Map[String, Boolean] = Map.empty,
  $ref: Option[URI] = None,
  $anchor: Option[String] = None,
  $comments: Option[String] = None,
  $defs: Map[String, JsonSchema] = Map.empty,
  title: Option[String] = None,
  description: Option[String] = None,
  kind: Seq[DataType] = Nil,
  defaultValue: Option[JsonValue] = None,
  constValue: Option[JsonValue] = None,
  enumValues: Option[Seq[JsonValue]] = None,
  minimum: Option[BigDecimal] = None,
  exclusiveMinimum: Option[BigDecimal] = None,
  maximum: Option[BigDecimal] = None,
  exclusiveMaximum: Option[BigDecimal] = None,
  multipleOf: Option[BigDecimal] = None,
  minLength: Option[Int] = None,
  maxLength: Option[Int] = None,
  format: Option[String] = None,
  pattern: Option[String] = None,
  contentMediaType: Option[String] = None,
  contentMediaEncoding: Option[Encoding] = None,
  contentSchema: Option[JsonSchema] = None,
  prefixItems: Option[Seq[JsonSchema]] = None,
  items: Option[JsonSchema] = None,
  minItems: Option[Int] = None,
  maxItems: Option[Int] = None,
  contains: Option[JsonSchema] = None,
  minContains: Option[Int] = None,
  maxContains: Option[Int] = None,
  uniqueItems: Option[Boolean] = None,
  additionalItems: Option[JsonSchema] = None,
  unevaluatedItems: Option[JsonSchema] = None,
  propertyNames: Option[JsonSchema] = None,
  patternProperties: Map[String, JsonSchema] = Map.empty,
  properties: Map[String, JsonSchema] = Map.empty,
  minProperties: Option[Int] = None,
  maxProperties: Option[Int] = None,
  required: Option[Seq[String]] = None,
  dependentRequired: Map[String, Seq[String]] = Map.empty,
  dependentSchemas: Map[String, Seq[JsonSchema]] = Map.empty,
  additionalProperties: Option[JsonSchema] = None,
  unevaluatedProperties: Option[JsonSchema] = None,
  allOf: Option[Seq[JsonSchema]] = None,
  anyOf: Option[Seq[JsonSchema]] = None,
  oneOf: Option[Seq[JsonSchema]] = None,
  not: Option[JsonSchema] = None,
  ifApply: Option[JsonSchema] = None,
  thenApply: Option[JsonSchema] = None,
  elseApply: Option[JsonSchema] = None,
  exampleValues: Seq[JsonValue] = Nil,
  readOnly: Option[Boolean] = None,
  writeOnly: Option[Boolean] = None,
  deprecated: Option[Boolean] = None
) extends JsonSchema

private case class BooleanJsonSchema(value: Boolean) extends JsonSchema:
  val $schema = None
  val $id = None
  val $dynamicRef = None
  val $dynamicAnchor = None
  val $vocabulary = Map.empty
  val $ref = None
  val $anchor = None
  val $comments = None
  val $defs = Map.empty
  val title = None
  val description = None
  val kind = Nil
  val defaultValue = None
  val constValue = None
  val enumValues = None
  val minimum = None
  val exclusiveMinimum = None
  val maximum = None
  val exclusiveMaximum = None
  val multipleOf = None
  val minLength = None
  val maxLength = None
  val format = None
  val pattern = None
  val contentMediaType = None
  val contentMediaEncoding = None
  val contentSchema = None
  val prefixItems = None
  val items = None
  val minItems = None
  val maxItems = None
  val contains = None
  val minContains = None
  val maxContains = None
  val uniqueItems = None
  val additionalItems = None
  val unevaluatedItems = None
  val propertyNames = None
  val patternProperties = Map.empty
  val properties = Map.empty
  val minProperties = None
  val maxProperties = None
  val required = None
  val dependentRequired = Map.empty
  val dependentSchemas = Map.empty
  val additionalProperties = None
  val unevaluatedProperties = None
  val allOf = None
  val anyOf = None
  val oneOf = None
  val not = None
  val ifApply = None
  val thenApply = None
  val elseApply = None
  val exampleValues = Nil
  val readOnly = None
  val writeOnly = None
  val deprecated = None
