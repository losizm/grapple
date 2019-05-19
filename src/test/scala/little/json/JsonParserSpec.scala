/*
 * Copyright 2019 Carlos Conyers
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
package little.json

import java.io.StringReader
import javax.json.stream.JsonParser.Event._

import org.scalatest.FlatSpec

import Implicits._
import Test._

class JsonParserSpec extends FlatSpec {
  val user = User(0, "root", true)

  "JSON parser" should "parse array" in {
    val source = new StringReader("[0, 1, 2]")
    val parser = Json.createParser(source)
    val json = parser.nextArray()

    assert(json.as[Seq[Int]] == Seq(0, 1, 2))
    parser.close()
  }

  it should "parse object" in {
    val source = new StringReader("""{ "id": 0, "name": "root" }""")
    val parser = Json.createParser(source)
    val json = parser.nextObject()

    assert(json.as[User] == User(0, "root"))
    parser.close()
  }

  it should "parse array of objects" in {
    val source = new StringReader("""[{ "id": 0, "name": "root" }, { "id": 500, "name": "guest", "enabled": false }]""")
    val parser = Json.createParser(source)

    assert(parser.next() == START_ARRAY)
    assert(parser.nextObject().as[User] == User(0, "root"))
    assert(parser.nextObject().as[User] == User(500, "guest", false))
    assert(parser.next() == END_ARRAY)
    parser.close()
  }
}
