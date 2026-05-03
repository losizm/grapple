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

import scala.io.Source

class JsonSchemaLoaderSpec extends org.scalatest.flatspec.AnyFlatSpec with ProductSchemaAssertions:
  it should "load JSON schema" in {
    val schema =
      val in = Source.fromResource("schema/ProductSchema.json")
      try JsonSchema.load(in.mkString)
      finally in.close()

    info(s"schema: ${schema.dump()}")
    info("verifying JSON schema")
    verify(schema)

    info("verifying JSON value")
    assert { !schema.isBoolean }
    assert { !schema.isTrue }
    assert { !schema.isFalse }
    verify(schema.toJsonValue.as[JsonObject])
  }
