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
import Encoding.*

class JsonSchemaKeywordSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "verify non-specific keywords" in {
    val schema = JsonSchemaBuilder()
      .setSchema(Dialect.Draft201909)
      .setId("https://localhost:8080/grapple/schema/v0")
      .setDynamicRef("https://localhost:8080/grapple/v0/d/schema")
      .setDynamicAnchor("grapple-dynamic")
      .addVocabulary("https://localhost:8080/grapple/v0/A" -> true)
      .addVocabulary("https://localhost:8080/grapple/v0/B" -> true)
      .addVocabulary("https://localhost:8080/grapple/v0/C" -> false)
      .setRef("https://localhost:8080/grapple/v0/schema")
      .setAnchor("grapple")
      .setComments("This is a comment")
      .setDefs("list" -> JsonSchema(kind = "array"), "map" -> JsonSchema(kind = "object"))
      .setTitle("Test Schema")
      .setDescription("This is a description")
      .setKind("integer")
      .setDefaultValue(100)
      .setConstValue(250)
      .setEnumValues(100, 150, 200, 250)
      .setAllOf(JsonSchema(kind = "string", defaultValue = "A"), JsonSchema(kind = "number", minimum = 1), JsonSchema(kind = "boolean", constValue = true))
      .setAnyOf(JsonSchema(kind = "string", defaultValue = "B"), JsonSchema(kind = "number", minimum = 2))
      .setOneOf(JsonSchema(kind = "string", defaultValue = "C"), JsonSchema(kind = "number", minimum = 3), JsonSchema(kind = "boolean"))
      .setNot(JsonSchema(kind = "integer", minimum = -100))
      .setIfApply(JsonSchema(kind = "integer"))
      .setThenApply(JsonSchema(kind = "array"))
      .setElseApply(JsonSchema(kind = "object"))
      .setExampleValues(1, "Hello", false, Json.arr(1, 2, 3), Json.obj("message" -> "Hello"))
      .setReadOnly(true)
      .setWriteOnly(false)
      .setDeprecated(true)
      .toJsonSchema()

    assert { schema.$schema.get == Dialect.Draft201909 }
    assert { schema.$id.get == URI("https://localhost:8080/grapple/schema/v0") }
    assert { schema.$dynamicRef.get == URI("https://localhost:8080/grapple/v0/d/schema") }
    assert { schema.$dynamicAnchor.get == "grapple-dynamic" }
    assert { schema.$vocabulary.size == 3 }
    assert { schema.$vocabulary("https://localhost:8080/grapple/v0/A") }
    assert { schema.$vocabulary("https://localhost:8080/grapple/v0/B") }
    assert { !schema.$vocabulary("https://localhost:8080/grapple/v0/C") }
    assert { schema.$ref.get == URI("https://localhost:8080/grapple/v0/schema") }
    assert { schema.$anchor.get == "grapple" }
    assert { schema.$comments.get == "This is a comment" }

    assert { schema.$defs.size == 2 }
    assert { schema.$defs("list") == JsonSchema(kind = "array") }
    assert { schema.$defs("map") == JsonSchema(kind = "object") }

    assert { schema.title.get == "Test Schema" }
    assert { schema.description.get == "This is a description" }

    assert { schema.kind.size == 1 }
    assert { schema.kind(0) == IntegerType }

    assert { schema.defaultValue.get == JsonNumber(100) }
    assert { schema.constValue.get == JsonNumber(250) }

    assert { schema.enumValues.get.size == 4 }
    assert { schema.enumValues.get(0) == JsonNumber(100) }
    assert { schema.enumValues.get(1) == JsonNumber(150) }
    assert { schema.enumValues.get(2) == JsonNumber(200) }
    assert { schema.enumValues.get(3) == JsonNumber(250) }

    assert { schema.allOf.get.size == 3 }
    assert { schema.allOf.get(0) == JsonSchema(kind = "string", defaultValue = "A") }
    assert { schema.allOf.get(1) == JsonSchema(kind = "number", minimum = 1) }
    assert { schema.allOf.get(2) == JsonSchema(kind = "boolean", constValue = true) }

    assert { schema.anyOf.get.size == 2 }
    assert { schema.anyOf.get(0) == JsonSchema(kind = "string", defaultValue = "B") }
    assert { schema.anyOf.get(1) == JsonSchema(kind = "number", minimum = 2) }

    assert { schema.oneOf.get.size == 3 }
    assert { schema.oneOf.get(0) == JsonSchema(kind = "string", defaultValue = "C") }
    assert { schema.oneOf.get(1) == JsonSchema(kind = "number", minimum = 3) }
    assert { schema.oneOf.get(2) == JsonSchema(kind = "boolean") }

    assert { schema.not.get == JsonSchema(kind = "integer", minimum = -100) }

    assert { schema.ifApply.get == JsonSchema(kind = "integer") }
    assert { schema.thenApply.get == JsonSchema(kind = "array") }
    assert { schema.elseApply.get == JsonSchema(kind = "object") }

    assert { schema.exampleValues.size == 5 }
    assert { schema.exampleValues(0) == JsonNumber(1) }
    assert { schema.exampleValues(1) == JsonString("Hello") }
    assert { schema.exampleValues(2) == JsonFalse }
    assert { schema.exampleValues(3) == Json.arr(1, 2, 3) }
    assert { schema.exampleValues(4) == Json.obj("message" -> "Hello") }

    assert { schema.readOnly.get }
    assert { !schema.writeOnly.get }
    assert { schema.deprecated.get }
  }

  it should "verify string specific keywords" in {
    val schema = JsonSchemaBuilder()
      .setKind("string")
      .setMinLength(8)
      .setMaxLength(80)
      .setFormat("email")
      .setPattern("""\w+\@\w+.(com|org|net)""")
      .setContentMediaType("application/json")
      .setContentMediaEncoding("base64")
      .setContentSchema(JsonSchema(kind = "object"))
      .toJsonSchema()

    info(schema.dump())
    assert { schema.kind == Seq(StringType) }
    assert { schema.minLength.contains(8) }
    assert { schema.maxLength.contains(80) }
    assert { schema.format.contains("email") }
    assert { schema.pattern.contains("""\w+\@\w+.(com|org|net)""") }
    assert { schema.contentMediaType.contains("application/json") }
    assert { schema.contentMediaEncoding.contains(Base64) }
    assert { schema.contentSchema.contains(JsonSchema(kind = "object")) }
  }

  it should "verify number specific keywords" in {
    val schema1 = JsonSchemaBuilder()
      .setKind("integer")
      .setMinimum(6)
      .setExclusiveMaximum(21)
      .setMultipleOf(3)
      .toJsonSchema()

    info(schema1.dump())
    assert { schema1.kind == Seq(IntegerType) }
    assert { schema1.minimum.contains(6) }
    assert { schema1.maximum.isEmpty }
    assert { schema1.exclusiveMinimum.isEmpty }
    assert { schema1.exclusiveMaximum.contains(21) }
    assert { schema1.multipleOf.contains(3) }

    val schema2 = JsonSchemaBuilder()
      .setKind("number")
      .setExclusiveMinimum(0)
      .setMaximum(9.5)
      .setMultipleOf(0.5)
      .toJsonSchema()

    info(schema2.dump())
    assert { schema2.kind == Seq(NumberType) }
    assert { schema2.minimum.isEmpty }
    assert { schema2.exclusiveMinimum.contains(0) }
    assert { schema2.maximum.contains(9.5) }
    assert { schema2.exclusiveMaximum.isEmpty }
    assert { schema2.multipleOf.contains(0.5) }
  }

  it should "verify array specific keywords" in {
    val schema1 = JsonSchemaBuilder()
      .setKind("array")
      .setItems(JsonSchema(kind = Seq("integer", "string")))
      .setMinItems(2)
      .setMaxItems(8)
      .toJsonSchema()

    info(schema1.dump())
    assert { schema1.kind == Seq(ArrayType) }
    assert { schema1.prefixItems.isEmpty }
    assert { schema1.items.contains(JsonSchema(kind = Seq("integer", "string"))) }
    assert { schema1.minItems.contains(2) }
    assert { schema1.minItems.contains(2) }
    assert { schema1.maxItems.contains(8) }
    assert { schema1.contains.isEmpty }
    assert { schema1.minContains.isEmpty }
    assert { schema1.maxContains.isEmpty }
    assert { schema1.uniqueItems.isEmpty }
    assert { schema1.additionalItems.isEmpty }
    assert { schema1.unevaluatedItems.isEmpty }

    val schema2 = JsonSchemaBuilder()
      .setKind("array")
      .setContains(JsonSchema(kind = "boolean"))
      .setMinItems(10)
      .setMinContains(7)
      .setMaxContains(8)
      .toJsonSchema()

    info(schema2.dump())
    assert { schema2.kind == Seq(ArrayType) }
    assert { schema2.prefixItems.isEmpty }
    assert { schema2.items.isEmpty }
    assert { schema2.minItems.contains(10) }
    assert { schema2.maxItems.isEmpty }
    assert { schema2.contains.contains(JsonSchema(kind = "boolean")) }
    assert { schema2.minContains.contains(7) }
    assert { schema2.maxContains.contains(8) }
    assert { schema2.uniqueItems.isEmpty }
    assert { schema2.additionalItems.isEmpty }
    assert { schema2.unevaluatedItems.isEmpty }

    val schema3 = JsonSchemaBuilder()
      .setKind("array")
      .setPrefixItems(Seq("integer", "string", "string", "object", true))
      .setAdditionalItems(true)
      .toJsonSchema()

    info(schema3.dump())
    assert { schema3.kind == Seq(ArrayType) }
    assert { schema3.prefixItems.contains(Seq("integer", "string", "string", "object", true).map(ToJsonSchema)) }
    assert { schema3.items.isEmpty }
    assert { schema3.minItems.isEmpty }
    assert { schema3.maxItems.isEmpty }
    assert { schema3.contains.isEmpty }
    assert { schema3.minContains.isEmpty }
    assert { schema3.maxContains.isEmpty }
    assert { schema3.uniqueItems.isEmpty }
    assert { schema3.additionalItems.exists(_.isTrue) }
    assert { schema3.unevaluatedItems.isEmpty }

    val schema4 = JsonSchemaBuilder()
      .setKind("array")
      .setPrefixItems(Seq("integer", "string", "string", "object", true))
      .setAdditionalItems(true)
      .setUniqueItems(true)
      .setUnevaluatedItems(ObjectType)
      .toJsonSchema()

    info(schema4.dump())
    assert { schema4.kind == Seq(ArrayType) }
    assert { schema4.prefixItems.contains(Seq("integer", "string", "string", "object", true).map(ToJsonSchema)) }
    assert { schema4.items.isEmpty }
    assert { schema4.minItems.isEmpty }
    assert { schema4.maxItems.isEmpty }
    assert { schema4.contains.isEmpty }
    assert { schema4.minContains.isEmpty }
    assert { schema4.maxContains.isEmpty }
    assert { schema4.uniqueItems.contains(true) }
    assert { schema4.additionalItems.exists(_.isTrue) }
    assert { schema4.unevaluatedItems.contains(JsonSchema(kind = "object")) }
  }

  it should "verify object specific keywords" in {
    val schema1 = JsonSchemaBuilder()
      .setKind("object")
      .setProperties("id" -> "integer", "name" -> "string", "roles" -> JsonSchema(kind = "array", minItems = 1), "location" -> "object")
      .setRequired("id", "name", "roles")
      .setAdditionalProperties("string")
      .toJsonSchema()

    info(schema1.dump())
    assert { schema1.kind == Seq(ObjectType) }
    assert { schema1.propertyNames.isEmpty }
    assert { schema1.patternProperties.isEmpty }
    assert { schema1.properties("id") == JsonSchema(kind = IntegerType) }
    assert { schema1.properties("name") == JsonSchema(kind = StringType) }
    assert { schema1.properties("roles") == JsonSchema(kind = ArrayType, minItems = 1) }
    assert { schema1.properties("location") == JsonSchema(kind = ObjectType) }
    assert { schema1.minProperties.isEmpty }
    assert { schema1.maxProperties.isEmpty }
    assert { schema1.required.get == Seq("id", "name", "roles") }
    assert { schema1.dependentRequired.isEmpty }
    assert { schema1.dependentSchemas.isEmpty }
    assert { schema1.additionalProperties.contains(JsonSchema(kind = StringType)) }
    assert { schema1.unevaluatedProperties.isEmpty }

    val schema2 = JsonSchemaBuilder()
      .setKind("object")
      .setPropertyNames(JsonSchema(pattern = "prop_\\w{1,8}"))
      .setUnevaluatedProperties(JsonSchema(kind = Seq("string", "integer")))
      .toJsonSchema()

    info(schema2.dump())
    assert { schema2.kind == Seq(ObjectType) }
    assert { schema2.propertyNames.contains(JsonSchema(pattern = "prop_\\w{1,8}")) }
    assert { schema2.patternProperties.isEmpty }
    assert { schema2.properties.isEmpty }
    assert { schema2.minProperties.isEmpty }
    assert { schema2.maxProperties.isEmpty }
    assert { schema2.required.isEmpty }
    assert { schema2.dependentRequired.isEmpty }
    assert { schema2.dependentSchemas.isEmpty }
    assert { schema2.additionalProperties.isEmpty }
    assert { schema2.unevaluatedProperties.contains(JsonSchema(kind = Seq("string", "integer"))) }

    val schema3 = JsonSchemaBuilder()
      .setKind("object")
      .addPatternProperties("[a-z]+Id" -> JsonSchema(kind = "integer", minimum = 100000))
      .addPatternProperties("[a-z]+Date" -> JsonSchema(kind = "string", format = "date"))
      .setAdditionalProperties(true)
      .toJsonSchema()

    info(schema3.dump())
    assert { schema3.kind == Seq(ObjectType) }
    assert { schema3.propertyNames.isEmpty }
    assert { schema3.patternProperties("[a-z]+Id") == JsonSchema(kind = "integer", minimum = 100000) }
    assert { schema3.patternProperties("[a-z]+Date") == JsonSchema(kind = "string", format = "date") }
    assert { schema3.properties.isEmpty }
    assert { schema3.minProperties.isEmpty }
    assert { schema3.maxProperties.isEmpty }
    assert { schema3.required.isEmpty }
    assert { schema3.dependentRequired.isEmpty }
    assert { schema3.dependentSchemas.isEmpty }
    assert { schema3.additionalProperties.get == JsonSchema.from(true) }
    assert { schema3.unevaluatedProperties.isEmpty }
  }
