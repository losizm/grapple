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

import DataType.*

/** Provides JSON input for `JsonSchema`. */
given jsonSchemaJsonInput: JsonInput[JsonSchema] =
  case bool: JsonBoolean => BooleanJsonSchema(bool.value)
  case obj: JsonObject   =>
    ObjectJsonSchema(
      $schema               = obj.readOption("$schema"),
      $id                   = obj.readOption("$id"),
      $dynamicRef           = obj.readOption("$dynamicRef"),
      $dynamicAnchor        = obj.readOption("$dynamicAnchor"),
      $vocabulary           = obj.readOrElse("$vocabulary", Map.empty),
      $ref                  = obj.readOption("$ref"),
      $anchor               = obj.readOption("$anchor"),
      $comments             = obj.readOption("$comments"),
      title                 = obj.readOption("title"),
      description           = obj.readOption("description"),

      kind = obj.readOption[Either[Seq[DataType], DataType]]("type").map {
        case Right(kind) => Seq(kind)
        case Left(kind)  => kind
      }.getOrElse(Nil),

      defaultValue          = obj.get("default"),
      constValue            = obj.get("const"),
      enumValues            = obj.readOption[Seq[JsonValue]]("enum"),

      minLength             = obj.readOption("minLength"),
      maxLength             = obj.readOption("maxLength"),
      format                = obj.readOption("format"),
      pattern               = obj.readOption("pattern"),
      contentMediaType      = obj.readOption("contentMediaType"),
      contentMediaEncoding  = obj.readOption("contentMediaEncoding"),
      contentSchema         = obj.readOption("contentSchema"),

      minimum               = obj.readOption("minimum"),
      exclusiveMinimum      = obj.readOption("exclusiveMinimum"),
      maximum               = obj.readOption("maximum"),
      exclusiveMaximum      = obj.readOption("exclusiveMaximum"),
      multipleOf            = obj.readOption("multipleOf"),

      prefixItems           = obj.readOption("prefixItems"),
      items                 = obj.readOption("items"),
      minItems              = obj.readOption("minItems"),
      maxItems              = obj.readOption("maxItems"),
      contains              = obj.readOption("contains"),
      minContains           = obj.readOption("minContains"),
      maxContains           = obj.readOption("maxContains"),
      uniqueItems           = obj.readOption("uniqueItems"),
      additionalItems       = obj.readOption("additionalItems"),
      unevaluatedItems      = obj.readOption("unevaluatedItems"),

      propertyNames         = obj.readOption("propertyNames"),
      patternProperties     = obj.readOrElse("patternProperties", Map.empty),
      properties            = obj.readOrElse("properties", Map.empty),
      minProperties         = obj.readOption("minProperties"),
      maxProperties         = obj.readOption("maxProperties"),
      required              = obj.readOption("required"),
      dependentRequired     = obj.readOrElse("dependentRequired", Map.empty),
      dependentSchemas      = obj.readOrElse("dependentSchemas", Map.empty),
      additionalProperties  = obj.readOption("additionalProperties"),
      unevaluatedProperties = obj.readOption("unevaluatedProperties"),

      allOf                 = obj.readOption("allOf"),
      anyOf                 = obj.readOption("anyOf"),
      oneOf                 = obj.readOption("oneOf"),
      not                   = obj.readOption("not"),
      ifApply               = obj.readOption("if"),
      thenApply             = obj.readOption("then"),
      elseApply             = obj.readOption("else"),
      exampleValues         = obj.readOrElse("examples", Seq.empty[JsonValue]),
      readOnly              = obj.readOption("readOnly"),
      writeOnly             = obj.readOption("writeOnly"),
      deprecated            = obj.readOption("deprecated"),
      $defs                 = obj.readOrElse("$defs", Map.empty)
    )

  case value: JsonValue => throw JsonSchemaException(s"Expected JsonObject or JsonBoolean for JsonSchema instead of ${JsonValueName(value)}")

/** Provides JSON output for `JsonSchema`. */
given jsonSchemaJsonOutput: JsonOutput[JsonSchema] =
  case schema: BooleanJsonSchema => JsonBoolean(schema.value)
  case schema: ObjectJsonSchema  =>
    val obj = JsonObjectBuilder()
    schema.$schema.foreach         { value => obj.add("$schema", value) }
    schema.$id.foreach             { value => obj.add("$id", value) }
    schema.$dynamicRef.foreach     { value => obj.add("$dynamicRef", value) }
    schema.$dynamicAnchor.foreach  { value => obj.add("$dynamicAnchor", value) }
    if schema.$vocabulary.nonEmpty then obj.add("$vocabulary", schema.$vocabulary)
    schema.$ref.foreach            { value => obj.add("$ref", value) }
    schema.$anchor.foreach         { value => obj.add("$anchor", value) }
    schema.$comments.foreach       { value => obj.add("$comments", value) }
    schema.title.foreach           { value => obj.add("title", value) }
    schema.description.foreach     { value => obj.add("description", value) }

    if      schema.kind.size == 1 then obj.add("type", schema.kind(0))
    else if schema.kind.size == 2 then obj.add("type", schema.kind)

    schema.defaultValue.foreach { value => obj.add("default", value) }
    schema.constValue.foreach   { value => obj.add("const", value) }
    schema.enumValues.foreach   { value => obj.add("enum", value) }

    // string specific keywords
    schema.minLength.foreach            { value => obj.add("minLength", value) }
    schema.maxLength.foreach            { value => obj.add("maxLength", value) }
    schema.format.foreach               { value => obj.add("format", value) }
    schema.pattern.foreach              { value => obj.add("pattern", value) }
    schema.contentMediaType.foreach     { value => obj.add("contentMediaType", value) }
    schema.contentMediaEncoding.foreach { value => obj.add("contentMediaEncoding", value) }
    schema.contentSchema.foreach        { value => obj.add("contentSchema", value) }

    // number specific keywords
    schema.minimum.foreach          { value => obj.add("minimum", value) }
    schema.exclusiveMinimum.foreach { value => obj.add("exclusiveMinimum", value) }
    schema.maximum.foreach          { value => obj.add("maximum", value) }
    schema.exclusiveMaximum.foreach { value => obj.add("exclusiveMaximum", value) }
    schema.multipleOf.foreach       { value => obj.add("multipleOf", value) }

    // array specific keywords
    schema.prefixItems.foreach      { value =>  obj.add("prefixItems", value) }
    schema.items.foreach            { value => obj.add("items", value) }
    schema.minItems.foreach         { value => obj.add("minItems", value) }
    schema.maxItems.foreach         { value => obj.add("maxItems", value) }
    schema.contains.foreach         { value => obj.add("contains", value) }
    schema.minContains.foreach      { value => obj.add("minContains", value) }
    schema.maxContains.foreach      { value => obj.add("maxContains", value) }
    schema.uniqueItems.foreach      { value => obj.add("uniqueItems", value) }
    schema.additionalItems.foreach  { value => obj.add("additionalItems", value) }
    schema.unevaluatedItems.foreach { value => obj.add("unevaluatedItems", value) }

    // property specific keywords
    schema.propertyNames.foreach         { value => obj.add("propertyNames", value) }
    if schema.patternProperties.nonEmpty then obj.add("patternProperties", schema.patternProperties)
    if schema.properties.nonEmpty        then obj.add("properties", schema.properties)
    schema.minProperties.foreach         { value => obj.add("minProperties", value) }
    schema.maxProperties.foreach         { value => obj.add("maxProperties", value) }
    schema.required.foreach              { value => obj.add("required", value) }
    if schema.dependentRequired.nonEmpty then obj.add("dependentRequired", schema.dependentRequired)
    if schema.dependentSchemas.nonEmpty  then obj.add("dependentSchemas", schema.dependentSchemas)
    schema.additionalProperties.foreach  { value => obj.add("additionalProperties", value) }
    schema.unevaluatedProperties.foreach { value => obj.add("unevaluatedProperties", value) }

    schema.allOf.foreach             { value => obj.add("allOf", value) }
    schema.anyOf.foreach             { value => obj.add("anyOf", value) }
    schema.oneOf.foreach             { value => obj.add("oneOf", value) }
    schema.not.foreach               { value => obj.add("not", value) }
    schema.ifApply.foreach           { value => obj.add("if", value) }
    schema.thenApply.foreach         { value => obj.add("then", value) }
    schema.elseApply.foreach         { value => obj.add("else", value) }
    if schema.exampleValues.nonEmpty then obj.add("examples", schema.exampleValues)
    schema.readOnly.foreach          { value => obj.add("readOnly", value) }
    schema.writeOnly.foreach         { value => obj.add("writeOnly", value) }
    schema.deprecated.foreach        { value => obj.add("deprecated", value) }
    if schema.$defs.nonEmpty         then obj.add("$defs", schema.$defs)

    obj.toJsonObject()

////////////////////////////////////////////////////////////////////////////////
// URI
////////////////////////////////////////////////////////////////////////////////
private given JsonInput[URI] =
  case str: JsonString  => URI(str.value)
  case value: JsonValue => throw JsonSchemaException(s"Expected JsonString for URI instead of ${JsonValueName(value)}")

private given JsonOutput[URI] =
  uri => JsonString(uri.toString)

////////////////////////////////////////////////////////////////////////////////
// Dialect
////////////////////////////////////////////////////////////////////////////////
private given JsonInput[Dialect] =
  case str: JsonString  => Dialect(str.value)
  case value: JsonValue => throw JsonSchemaException(s"Expected JsonString for Dialect instead of ${JsonValueName(value)}")

private given JsonOutput[Dialect] =
  dialect => Json.toJson(dialect.id)

////////////////////////////////////////////////////////////////////////////////
// DataType
////////////////////////////////////////////////////////////////////////////////
private given JsonInput[DataType] =
  case str: JsonString  => DataType(str.value)
  case value: JsonValue => throw JsonSchemaException(s"Expected JsonString for DataType instead of ${JsonValueName(value)}")

private given JsonOutput[DataType] =
  kind => JsonString(kind.name)

////////////////////////////////////////////////////////////////////////////////
// Encoding
////////////////////////////////////////////////////////////////////////////////
private given JsonInput[Encoding] =
  case str: JsonString  => Encoding(str.value)
  case value: JsonValue => throw JsonSchemaException(s"Expected JsonString for Encoding instead of ${JsonValueName(value)}")

private given JsonOutput[Encoding] =
  encoding => JsonString(encoding.name)
