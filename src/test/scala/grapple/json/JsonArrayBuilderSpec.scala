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

class JsonArrayBuilderSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "build JsonArray" in {
    val user = JsonArrayBuilder()
      .add(1000)
      .add("lupita")
      .add(Json.arr("lupita", "admin"))
      .add(true)
      .add(Json.obj("name" -> "lupita", "email" -> "lupita@localhost", "timeout" -> 10_000_000_000L))
      .toJsonArray()

    assert(user.nonEmpty)
    assert(user.size == 5)

    assert(user(0).as[Int] == 1000)
    assert(user(1).as[String] == "lupita")
    assert(user(2).as[Seq[String]] == Seq("lupita", "admin"))
    assert(user(3))
    assert(user(4) == Json.obj("name" -> "lupita", "email" -> "lupita@localhost", "timeout" -> 10_000_000_000L))
  }

  it should "build emtpy JsonArray" in {
    val arr = JsonArrayBuilder().toJsonArray()
    assert(arr.isEmpty)
    assert(arr.size == 0)
  }
