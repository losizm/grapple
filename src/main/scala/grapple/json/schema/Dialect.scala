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

/**
 * Defines schema dialect enumeration.
 *
 * @param id schema identifier
 */
enum Dialect(val id: URI):
  /**
   * Defines Draft 4 schema dialect.
   *
   * @note `Dialect(URI("http://json-schema.org/draft-04/schema#"))`
   */
  case Draft4 extends Dialect(URI("http://json-schema.org/draft-04/schema#"))

  /**
   * Defines Draft 6 schema dialect.
   *
   * @note `Dialect(URI("http://json-schema.org/draft-06/schema#"))`
   */
  case Draft6 extends Dialect(URI("http://json-schema.org/draft-06/schema#"))

  /**
   * Defines Draft 7 schema dialect.
   *
   * @note `Dialect(URI("http://json-schema.org/draft-07/schema#"))`
   */
  case Draft7 extends Dialect(URI("http://json-schema.org/draft-07/schema#"))

  /**
   * Defines Draft 2019-09 schema dialect.
   *
   * @note `Dialect(URI("https://json-schema.org/draft/2019-09/schema"))`
   */
  case Draft201909 extends Dialect(URI("https://json-schema.org/draft/2019-09/schema"))

  /**
   * Defines Draft 2020-12 schema dialect.
   *
   * @note `Dialect(URI("https://json-schema.org/draft/2020-12/schema"))`
   */
  case Draft202012 extends Dialect(URI("https://json-schema.org/draft/2020-12/schema"))

/** Provides schema dialect lookup by identifier. */
object Dialect:
  /**
   * Gets schema dialect by identifier.
   *
   * @param id dialect identifier
   *
   * @return schema dialect
   */
  def apply(id: URIParam): Dialect =
    ToURI(id) match
      case Draft4.id      => Draft4
      case Draft6.id      => Draft6
      case Draft7.id      => Draft7
      case Draft201909.id => Draft201909
      case Draft202012.id => Draft202012
      case _              => throw JsonSchemaException(s"Unrecognized schema dialect: $id")
