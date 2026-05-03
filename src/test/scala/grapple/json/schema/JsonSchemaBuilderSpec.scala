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

import DataType.*

class JsonSchemaBuilderSpec extends org.scalatest.flatspec.AnyFlatSpec with ProductSchemaAssertions:
  it should "build JSON schema" in {
    val productIdSchema = JsonSchemaBuilder()
      .setKind(IntegerType)
      .setDescription("Unique identifier for product")
      .setMultipleOf(5)
      .toJsonSchema()

    val productNameSchema = JsonSchemaBuilder()
      .setKind(StringType)
      .setDescription("Name of product")
      .setMinLength(3)
      .setMaxLength(40)
      .toJsonSchema()

    val priceSchema = JsonSchemaBuilder()
      .setKind(NumberType)
      .setDescription("Price of product")
      .setDefaultValue(1.99)
      .setExclusiveMinimum(0)
      .setMultipleOf(0.05)
      .toJsonSchema()

    val tagsSchema = JsonSchemaBuilder()
      .setKind(ArrayType)
      .setDescription("Tags for product")
      .setDefaultValue(Json.arr(JsonString("books"), JsonString("electronics")))
      .setItems(JsonSchemaBuilder().setKind(StringType).setEnumValues("books", "clothing", "electronics").toJsonSchema())
      .setMinItems(1)
      .setUniqueItems(true)
      .toJsonSchema()

    val dimensionsSchema = JsonSchemaBuilder()
      .setKind(ObjectType)
      .setProperties("length" -> NumberType, "width" -> NumberType, "height" -> NumberType)
      .setRequired("length", "width", "height")
      .toJsonSchema()

    val schema = JsonSchemaBuilder()
      .setTitle("Product")
      .setDescription("A product from Acme catalog")
      .setKind(ObjectType)
      .addProperties("productId"   -> productIdSchema)
      .addProperties("productName" -> productNameSchema)
      .addProperties("price"       -> priceSchema)
      .addProperties("tags"        -> tagsSchema)
      .addProperties("dimensions"  -> dimensionsSchema)
      .setRequired("productId", "productName", "price")
      .toJsonSchema()

    info(s"schema: ${schema.dump()}")
    info("verifying JSON schema")
    verify(schema)

    info("verifying JSON value")
    assert { !schema.isBoolean }
    assert { !schema.isTrue }
    assert { !schema.isFalse }
    verify(schema.toJsonValue.as[JsonObject])

    val otherSchema = JsonSchemaBuilder(schema)
      .addKind("string")
      .addProperties("productId" -> JsonSchema(kind = Seq("integer", "string")))
      .addProperties("productDescription" -> "string")
      .setRequired("productId", "price")
      .setAdditionalProperties("object")
      .toJsonSchema()

    assert { otherSchema.$schema == schema.$schema }
    assert { otherSchema.$id == schema.$id }
    assert { otherSchema.$dynamicRef == schema.$dynamicRef }
    assert { otherSchema.$dynamicAnchor == schema.$dynamicAnchor }
    assert { otherSchema.$vocabulary == schema.$vocabulary }
    assert { otherSchema.$ref == schema.$ref }
    assert { otherSchema.$anchor == schema.$anchor }
    assert { otherSchema.$comments == schema.$comments }
    assert { otherSchema.$defs == schema.$defs }
    assert { otherSchema.title == schema.title }
    assert { otherSchema.description == schema.description }
    assert { otherSchema.kind == schema.kind :+ StringType }
    assert { otherSchema.defaultValue == schema.defaultValue }
    assert { otherSchema.constValue == schema.constValue }
    assert { otherSchema.enumValues == schema.enumValues }
    assert { otherSchema.minLength == schema.minLength }
    assert { otherSchema.maxLength == schema.maxLength }
    assert { otherSchema.format == schema.format }
    assert { otherSchema.pattern == schema.pattern }
    assert { otherSchema.contentMediaType == schema.contentMediaType }
    assert { otherSchema.contentMediaEncoding == schema.contentMediaEncoding }
    assert { otherSchema.contentSchema == schema.contentSchema }
    assert { otherSchema.minimum == schema.minimum }
    assert { otherSchema.exclusiveMinimum == schema.exclusiveMinimum }
    assert { otherSchema.maximum == schema.maximum }
    assert { otherSchema.exclusiveMaximum == schema.exclusiveMaximum }
    assert { otherSchema.multipleOf == schema.multipleOf }
    assert { otherSchema.prefixItems == schema.prefixItems }
    assert { otherSchema.items == schema.items }
    assert { otherSchema.minItems == schema.minItems }
    assert { otherSchema.maxItems == schema.maxItems }
    assert { otherSchema.contains == schema.contains }
    assert { otherSchema.minContains == schema.minContains }
    assert { otherSchema.maxContains == schema.maxContains }
    assert { otherSchema.uniqueItems == schema.uniqueItems }
    assert { otherSchema.additionalItems == schema.additionalItems }
    assert { otherSchema.unevaluatedItems == schema.unevaluatedItems }
    assert { otherSchema.propertyNames == schema.propertyNames }
    assert { otherSchema.patternProperties == schema.patternProperties }
    assert { otherSchema.properties.size == 6 }
    assert { otherSchema.properties("productId") == JsonSchema(kind = Seq("integer", "string")) }
    assert { otherSchema.properties("productName") == productNameSchema }
    assert { otherSchema.properties("productDescription") == JsonSchema(kind = "string") }
    assert { otherSchema.properties("price") == priceSchema }
    assert { otherSchema.properties("tags") == tagsSchema }
    assert { otherSchema.properties("dimensions") == dimensionsSchema }
    assert { otherSchema.minProperties == schema.minProperties }
    assert { otherSchema.maxProperties == schema.maxProperties }
    assert { otherSchema.required.get == Seq("productId", "price") }
    assert { otherSchema.dependentRequired == schema.dependentRequired }
    assert { otherSchema.dependentSchemas == schema.dependentSchemas }
    assert { otherSchema.additionalProperties.get == JsonSchema(kind = "object") }
    assert { otherSchema.unevaluatedProperties == schema.unevaluatedProperties }
    assert { otherSchema.allOf == schema.allOf }
    assert { otherSchema.anyOf == schema.anyOf }
    assert { otherSchema.oneOf == schema.oneOf }
    assert { otherSchema.not == schema.not }
    assert { otherSchema.ifApply == schema.ifApply }
    assert { otherSchema.thenApply == schema.thenApply }
    assert { otherSchema.elseApply == schema.elseApply }
    assert { otherSchema.exampleValues == schema.exampleValues }
    assert { otherSchema.readOnly == schema.readOnly }
    assert { otherSchema.writeOnly == schema.writeOnly }
    assert { otherSchema.deprecated == schema.deprecated }
  }
