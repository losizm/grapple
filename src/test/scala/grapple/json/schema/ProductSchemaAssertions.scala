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

trait ProductSchemaAssertions:
  def verify(schema: JsonSchema): Unit =
    assert { schema.title.contains("Product") }
    assert { schema.description.contains("A product from Acme catalog") }
    assert { schema.kind == Seq(ObjectType) }

    assert { schema.required.get.toSet == Set("productId", "productName", "price") }
    assert { schema.properties.size == 5 }

    assert { schema.properties("productId").kind == Seq(IntegerType) }
    assert { schema.properties("productId").description.contains("Unique identifier for product") }
    assert { schema.properties("productId").defaultValue.isEmpty }
    assert { schema.properties("productId").constValue.isEmpty }
    assert { schema.properties("productId").enumValues.isEmpty }
    assert { schema.properties("productId").minimum.isEmpty }
    assert { schema.properties("productId").exclusiveMinimum.isEmpty }
    assert { schema.properties("productId").maximum.isEmpty }
    assert { schema.properties("productId").exclusiveMaximum.isEmpty }
    assert { schema.properties("productId").multipleOf.contains(5) }

    assert { schema.properties("productName").kind == Seq(StringType) }
    assert { schema.properties("productName").description.contains("Name of product") }
    assert { schema.properties("productName").defaultValue.isEmpty }
    assert { schema.properties("productName").constValue.isEmpty }
    assert { schema.properties("productName").enumValues.isEmpty }
    assert { schema.properties("productName").minLength.contains(3) }
    assert { schema.properties("productName").maxLength.contains(40) }
    assert { schema.properties("productName").format.isEmpty }
    assert { schema.properties("productName").pattern.isEmpty }

    assert { schema.properties("price").kind == Seq(NumberType) }
    assert { schema.properties("price").description.contains("Price of product") }
    assert { schema.properties("price").defaultValue.contains(JsonNumber(1.99)) }
    assert { schema.properties("price").constValue.isEmpty }
    assert { schema.properties("price").enumValues.isEmpty }
    assert { schema.properties("price").minimum.isEmpty }
    assert { schema.properties("price").exclusiveMinimum.contains(0) }
    assert { schema.properties("price").maximum.isEmpty }
    assert { schema.properties("price").exclusiveMaximum.isEmpty }
    assert { schema.properties("price").multipleOf.contains(0.05) }

    assert { schema.properties("tags").kind == Seq(ArrayType) }
    assert { schema.properties("tags").description.contains("Tags for product") }
    assert { schema.properties("tags").defaultValue.contains(Json.arr(JsonString("books"), JsonString("electronics"))) }
    assert { schema.properties("tags").constValue.isEmpty }
    assert { schema.properties("tags").enumValues.isEmpty }
    assert { schema.properties("tags").items.get.kind == Seq(StringType) }
    assert { schema.properties("tags").items.get.enumValues.get.toSet == Set(JsonString("books"), JsonString("clothing"), JsonString("electronics")) }
    assert { schema.properties("tags").minItems.contains(1) }
    assert { schema.properties("tags").maxItems.isEmpty }
    assert { schema.properties("tags").uniqueItems.contains(true) }

    assert { schema.properties("dimensions").kind == Seq(ObjectType) }
    assert { schema.properties("dimensions").description.isEmpty }
    assert { schema.properties("dimensions").defaultValue.isEmpty }
    assert { schema.properties("dimensions").constValue.isEmpty }
    assert { schema.properties("dimensions").enumValues.isEmpty }
    assert { schema.properties("dimensions").properties.size == 3 }
    assert { schema.properties("dimensions").properties("length").kind == Seq(NumberType) }
    assert { schema.properties("dimensions").properties("width").kind == Seq(NumberType) }
    assert { schema.properties("dimensions").properties("height").kind == Seq(NumberType) }
    assert { schema.properties("dimensions").required.get.toSet == Set("length", "width", "height") }

  def verify(schema: JsonObject): Unit =
    assert { schema.getString("title") == "Product" }
    assert { schema.getString("description") == "A product from Acme catalog" }
    assert { schema.getString("type") == "object" }
    assert { schema.getObject("properties").keys == Set("productId", "productName", "price", "tags", "dimensions") }
    assert { schema \ "properties" \ "productId" \ "description" == JsonString("Unique identifier for product") }
    assert { schema \ "properties" \ "productId" \ "type" == JsonString("integer") }
    assert { schema \ "properties" \ "productId" \ "multipleOf" == JsonNumber(5) }
    assert { schema \ "properties" \ "productName" \ "description" == JsonString("Name of product") }
    assert { schema \ "properties" \ "productName" \ "type" == JsonString("string") }
    assert { schema \ "properties" \ "productName" \ "minLength" == JsonNumber(3) }
    assert { schema \ "properties" \ "productName" \ "maxLength" == JsonNumber(40) }
    assert { schema \ "properties" \ "price" \ "description" == JsonString("Price of product") }
    assert { schema \ "properties" \ "price" \ "type" == JsonString("number") }
    assert { schema \ "properties" \ "price" \ "default" == JsonNumber(1.99) }
    assert { schema \ "properties" \ "price" \ "exclusiveMinimum" == JsonNumber(0) }
    assert { schema \ "properties" \ "price" \ "multipleOf" == JsonNumber(0.05) }
    assert { schema \ "properties" \ "tags" \ "description" == JsonString("Tags for product") }
    assert { schema \ "properties" \ "tags" \ "type" == JsonString("array") }
    assert { schema \ "properties" \ "tags" \ "default" == Json.arr(JsonString("books"), JsonString("electronics")) }
    assert { schema \ "properties" \ "tags" \ "items" \ "type" == JsonString("string") }
    assert { (schema \ "properties" \ "tags" \ "items" \ "enum").as[Set[String]] == Set("books", "clothing", "electronics") }
    assert { schema \ "properties" \ "tags" \ "minItems" == JsonNumber(1) }
    assert { schema \ "properties" \ "tags" \ "uniqueItems" == JsonTrue }
    assert { schema \ "properties" \ "dimensions" \ "type" == JsonString("object") }
    assert { schema \ "properties" \ "dimensions" \ "properties" \ "length" \ "type" == JsonString("number") }
    assert { schema \ "properties" \ "dimensions" \ "properties" \ "width" \ "type" == JsonString("number") }
    assert { schema \ "properties" \ "dimensions" \ "properties" \ "height" \ "type" == JsonString("number") }
    assert { (schema \ "properties" \ "dimensions" \ "required").as[Set[String]] == Set("length", "width", "height") }
