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

import scala.language.implicitConversions
import Implicits.given

class JsonObjectBuilderSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "build JsonObject" in {
    val user = JsonObjectBuilder()
      .add("id",      1000)
      .add("name",    "lupita")
      .add("groups" , Json.arr("lupita", "admin"))
      .add("enabled", true)
      .add("other",   Json.obj("name" -> "lupita", "email" -> "lupita@localhost", "timeout" -> 10_000_000_000L))
      .build()

    assert(!user.isEmpty)
    assert(user.names == Seq("id", "name", "groups", "enabled", "other"))

    assert(user("id").as[Int] == 1000)
    assert(user("name").as[String] == "lupita")
    assert(user("groups").as[Seq[String]] == Seq("lupita", "admin"))
    assert(user("enabled").as[Boolean])
    assert(user("other") == Json.obj("name" -> "lupita", "email" -> "lupita@localhost", "timeout" -> 10_000_000_000L))
  }

  it should "build empty JsonObject" in {
    val obj = JsonObjectBuilder().build()
    assert(obj.isEmpty)
    assert(obj.names.isEmpty)
  }
