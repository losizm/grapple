/*
 * Copyright 2021 Carlos Conyers
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

import scala.annotation.targetName
import scala.util.Try

extension (json: JsonValue)
  /**
   * Gets value in JSON object.
   *
   * @param key object key
   *
   * @throws JsonExpectationError if not JsonObject
   */
  @targetName("at")
  def \(key: String): JsonValue =
    expect[JsonObject](json)(key)

  /**
   * Gets value in JSON array.
   *
   * @param index array index
   *
   * @throws JsonExpectationError if not JsonArray
   */
  @targetName("at")
  def \(index: Int): JsonValue =
    expect[JsonArray](json)(index)

  /**
   * Collects values with given object key while traversing nested objects and
   * arrays.
   *
   * {{{
   * import grapple.json.{ Json, \\, given }
   *
   * val json = Json.parse("""{
   *   "node": {
   *     "name": "localhost",
   *     "users": [
   *       { "id": 0,    "name": "root" },
   *       { "id": 1000, "name": "lupita"  }
   *     ]
   *   }
   * }""")
   *
   * val names = (json \\ "name").map(_.as[String])
   *
   * assert { names == Seq("localhost", "root", "lupita") }
   * }}}
   *
   * @param key object key
   */
  @targetName("collect")
  def \\(key: String): Seq[JsonValue] =
    json match
      case json: JsonObject =>
        Try(json(key))
          .toOption
          .toSeq ++
        json.fields.values.flatMap(_ \\ key).toSeq

      case json: JsonArray => json.values.flatMap(_ \\ key).toSeq
      case _               => Nil
