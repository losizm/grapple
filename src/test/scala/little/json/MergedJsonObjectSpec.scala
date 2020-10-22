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

import org.scalatest.FlatSpec

import Implicits._
import Test._

class MergedJsonObjectSpec extends FlatSpec {
  it should "merge JSON objects" in {
    val obj = Json.obj("a" -> "a", "b" -> 2, "c" -> 3.0, "d" -> false) ++
      Json.obj("a" -> "x", "z" -> User(0, "root")) + ("groups" -> "admin,staff,cdrom")

    assert(!obj.isEmpty)
    assert(obj.size == 6)
    assert(obj.getString("a") == "x")
    assert(obj.getInt("b") == 2)
    assert(obj.getJsonNumber("c").doubleValue == 3.0)
    assert(!obj.getBoolean("d"))
    assert(obj.get("z").as[User] == User(0, "root"))
    assert(obj.getString("groups") == "admin,staff,cdrom")
  }
}
