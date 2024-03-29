/*
 * Copyright 2023 Carlos Conyers
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
package rpc

import scala.language.implicitConversions

class JsonRpcRequestSpec extends org.scalatest.flatspec.AnyFlatSpec:
  case class Param(name: String, value: Int)

  given JsonInput[Param] =
    case json: JsonObject => Param(json("name"), json("value"))
    case _                => throw IllegalArgumentException("Expected JSON object")

  given JsonOutput[Param] =
    param => Json.obj("name" -> param.name, "value" -> param.value)

  it should "create JsonRpcRequest" in {
    val req1 = JsonRpcRequest("2.0", JsonRpcIdentifier("abc"), "compute", None)
    assert(req1.version == "2.0")
    assert(!req1.isNotification)
    assert(req1.id == JsonRpcIdentifier("abc"))
    assert(req1.id.string == "abc")
    assert(req1.method == "compute")
    assert(req1.params.isEmpty)

    val req2 = JsonRpcRequest("2.0", JsonRpcIdentifier(123), "compute", Some(Param("x", 1)))
    assert(req2.version == "2.0")
    assert(!req2.isNotification)
    assert(req2.id == JsonRpcIdentifier(123))
    assert(req2.id.number == 123)
    assert(req2.method == "compute")
    assert(req2.params.map(_.as[Param]).contains(Param("x", 1)))

    val req3 = JsonRpcRequest("2.0", "compute", Some(Param("x", 1)))
    assert(req3.version == "2.0")
    assert(req3.isNotification)
    assertThrows[NoSuchElementException](req3.id)
    assert(req3.method == "compute")
    assert(req3.params.map(_.as[Param]).contains(Param("x", 1)))
  }

  it should "create JsonRpcRequest with attributes" in {
    val req1 = JsonRpcRequestBuilder()
      .id(123)
      .method("compute")
      .params(Seq(1, 2, 3))
      .attributes("one" -> 1, "two" -> "2")
      .toJsonRpcRequest()

    assert(req1.id.number == 123)
    assert(req1.method == "compute")
    assert(req1.params.get.as[Seq[Int]] == Seq(1, 2, 3))
    assert(req1.attributes.size == 2)
    assert(req1.getOption("one").contains(1))
    assert(req1.getOption("two").contains("2"))
    assert(req1.getOrElse("one", 0) == 1)
    assert(req1.getOrElse("two", "0") == "2")
    assert(req1.getOrElse("three", 0) == 0)
    assert(req1.get[Int]("one") == 1)
    assert(req1.get[String]("two") == "2")
    assertThrows[NoSuchElementException](req1.get[Int]("three"))

    val req2 = req1.put("one", "x").put("three", "xxx")

    assert(req2.id.number == 123)
    assert(req2.method == "compute")
    assert(req2.params.get.as[Seq[Int]] == Seq(1, 2, 3))
    assert(req2.attributes.size == 3)
    assert(req2.getOption("one").contains("x"))
    assert(req2.getOption("two").contains("2"))
    assert(req2.getOption("three").contains("xxx"))

    val req3 = req2.remove("two")
    assert(req3.id.number == 123)
    assert(req3.method == "compute")
    assert(req3.params.get.as[Seq[Int]] == Seq(1, 2, 3))
    assert(req3.attributes.size == 2)
    assert(req3.getOption("one").contains("x"))
    assert(req3.getOption("three").contains("xxx"))

    val req4 = req3.setAttributes(Map("1" -> 1, "2" -> 2))
    assert(req4.id.number == 123)
    assert(req4.method == "compute")
    assert(req4.params.get.as[Seq[Int]] == Seq(1, 2, 3))
    assert(req4.attributes.size == 2)
    assert(req4.getOption("1").contains(1))
    assert(req4.getOption("2").contains(2))
  }
