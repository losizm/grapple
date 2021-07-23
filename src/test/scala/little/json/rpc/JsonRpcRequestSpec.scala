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
package little.json
package rpc

import scala.language.implicitConversions

import little.json.Implicits.given

class JsonRpcRequestSpec extends org.scalatest.flatspec.AnyFlatSpec:
  case class Param(name: String, value: Int)

  given JsonInput[Param]  = json => Param(json("name"), json("value"))
  given JsonOutput[Param] = param => Json.obj("name" -> param.name, "value" -> param.value)

  it should "create JsonRpcRequest" in {
    val req1 = JsonRpcRequest("2.0", JsonRpcIdentifier("abc"), "compute", None)
    assert(req1.version == "2.0")
    assert(!req1.isNotification)
    assert(req1.id == JsonRpcIdentifier("abc"))
    assert(req1.id.stringValue == "abc")
    assert(req1.method == "compute")
    assert(req1.params.isEmpty)

    val req2 = JsonRpcRequest("2.0", JsonRpcIdentifier(123), "compute", Some(Param("x", 1)))
    assert(req2.version == "2.0")
    assert(!req2.isNotification)
    assert(req2.id == JsonRpcIdentifier(123))
    assert(req2.id.numberValue == 123)
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
    val req1 = JsonRpcRequest.builder()
      .id(123)
      .method("compute")
      .params(Seq(1, 2, 3))
      .attributes(Map("one" -> 1, "two" -> "2"))
      .build()

    assert(req1.id.numberValue == 123)
    assert(req1.method == "compute")
    assert(req1.params.get.as[Seq[Int]] == Seq(1, 2, 3))
    assert(req1.attributes.size == 2)
    assert(req1.getAttribute("one").contains(1))
    assert(req1.getAttribute("two").contains("2"))
    assert(req1.getAttributeOrElse("one", 0) == 1)
    assert(req1.getAttributeOrElse("two", "0") == "2")
    assert(req1.getAttributeOrElse("three", 0) == 0)
    assert(req1.attribute[Int]("one") == 1)
    assert(req1.attribute[String]("two") == "2")
    assertThrows[NoSuchElementException](req1.attribute[Int]("three"))

    val req2 = req1.putAttribute("one", "x")
      .putAttribute("three", "xxx")

    assert(req2.id.numberValue == 123)
    assert(req2.method == "compute")
    assert(req2.params.get.as[Seq[Int]] == Seq(1, 2, 3))
    assert(req2.attributes.size == 3)
    assert(req2.getAttribute("one").contains("x"))
    assert(req2.getAttribute("two").contains("2"))
    assert(req2.getAttribute("three").contains("xxx"))

    val req3 = req2.removeAttribute("two")
    assert(req3.id.numberValue == 123)
    assert(req3.method == "compute")
    assert(req3.params.get.as[Seq[Int]] == Seq(1, 2, 3))
    assert(req3.attributes.size == 2)
    assert(req3.getAttribute("one").contains("x"))
    assert(req3.getAttribute("three").contains("xxx"))

    val req4 = req3.setAttributes(Map("1" -> 1, "2" -> 2))
    assert(req4.id.numberValue == 123)
    assert(req4.method == "compute")
    assert(req4.params.get.as[Seq[Int]] == Seq(1, 2, 3))
    assert(req4.attributes.size == 2)
    assert(req4.getAttribute("1").contains(1))
    assert(req4.getAttribute("2").contains(2))
  }
