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

import java.io.StringWriter
import scala.language.implicitConversions
import Implicits.given

class JsonWriterSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "write JSON object" in {
    val json = Json.obj(
      "id"     -> 1000,
      "name"   -> "lupita",
      "groups" -> Seq("lupita", "admin", "sudoer")
    )

    val buf = StringWriter()
    val out = JsonWriter(buf)

    try
      out.write(json)

      val copy = Json.parse(buf.toString)
      assert(copy == json)
    finally
      out.close()
  }

  it should "write JSON array" in {
    val json = Json.arr(
      1000,
      "lupita",
      Seq("lupita", "admin", "sudoer")
    )

    val buf = StringWriter()
    val out = JsonWriter(buf)

    try
      out.write(json)

      val copy = Json.parse(buf.toString)
      assert(copy == json)
    finally
      out.close()
  }
