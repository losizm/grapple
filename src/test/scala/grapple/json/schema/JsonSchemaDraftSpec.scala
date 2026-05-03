
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

class JsonSchemaDraftSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "load JSON Schema Draft 2020-12" in {
    val schema = JsonSchema.load(resource("schema/json-schema-draft-202012.json"))
    info(s"schema: ${schema.dump()}")
  }

  it should "load JSON Schema Draft 2019-09" in {
    val schema = JsonSchema.load(resource("schema/json-schema-draft-201909.json"))
    info(s"schema: ${schema.dump()}")
  }

  it should "load JSON Schema Draft 7" in {
    val schema = JsonSchema.load(resource("schema/json-schema-draft-7.json"))
    info(s"schema: ${schema.dump()}")
  }

  private def resource(uri: String): String =
    val in = Source.fromResource(uri)
    try in.mkString
    finally in.close()
