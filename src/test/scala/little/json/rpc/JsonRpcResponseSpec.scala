/*
 * Copyright 2020 Carlos Conyers
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
package little.json.rpc

import javax.json.JsonObject

import little.json.{ Json, JsonInput, JsonOutput }
import little.json.Implicits._

import org.scalatest.FlatSpec

class JsonRpcResponseSpec extends FlatSpec {
  case class Result(name: String, value: Int)

  implicit val resultInput: JsonInput[Result] = {
    case value: JsonObject => Result(
      value.getString("name"),
      value.getInt("value")
    )
  }

  implicit val resultOutput: JsonOutput[Result] =
    param => Json.obj(
      "name"  -> param.name,
      "value" -> param.value
    )

  it should "create JsonRpcResponse with object result" in {
    val res = JsonRpcResponse("2.0", JsonRpcIdentifier("abc"), Result("y", 2))
    assert(res.version == "2.0")
    assert(res.id == JsonRpcIdentifier("abc"))
    assert(res.id.stringValue == "abc")
    assert(res.result.as[Result] == Result("y", 2))
  }

  it should "create JsonRpcResponse with array result" in {
    val res = JsonRpcResponse("2.0", JsonRpcIdentifier("abc"), Array(0, 1, 2))
    assert(res.version == "2.0")
    assert(res.id == JsonRpcIdentifier("abc"))
    assert(res.id.stringValue == "abc")
    assert(res.result.as[Seq[Int]] == Seq(0, 1, 2))
  }

  it should "create JsonRpcResponse with number result" in {
    val res = JsonRpcResponse("2.0", JsonRpcIdentifier("abc"), 2)
    assert(res.version == "2.0")
    assert(res.id == JsonRpcIdentifier("abc"))
    assert(res.id.stringValue == "abc")
    assert(res.result.as[Int] == 2)
  }

  it should "create JsonRpcResponse with string result" in {
    val res = JsonRpcResponse("2.0", JsonRpcIdentifier("abc"), "success")
    assert(res.version == "2.0")
    assert(res.id == JsonRpcIdentifier("abc"))
    assert(res.id.stringValue == "abc")
    assert(res.result.as[String] == "success")
  }

  it should "create JsonRpcResponse with boolean result" in {
    val res1 = JsonRpcResponse("2.0", JsonRpcIdentifier("abc"), true)
    assert(res1.version == "2.0")
    assert(res1.id == JsonRpcIdentifier("abc"))
    assert(res1.id.stringValue == "abc")
    assert(res1.result.as[Boolean])

    val res2 = JsonRpcResponse("2.0", JsonRpcIdentifier("abc"), false)
    assert(res2.version == "2.0")
    assert(res2.id == JsonRpcIdentifier("abc"))
    assert(res2.id.stringValue == "abc")
    assert(!res2.result.as[Boolean])
  }

  it should "create JsonRpcResponse with error" in {
    val res = JsonRpcResponse("2.0", JsonRpcIdentifier(123), InvalidRequest("Invalid request"))
    assert(res.version == "2.0")
    assert(res.id == JsonRpcIdentifier(123))
    assert(res.id.numberValue == 123)
    assert(res.error.isInvalidRequest)
    assert(res.error.message == "Invalid request")
  }

  it should "create JsonRpcResponse with attributes" in {
    val res1 = JsonRpcResponse.builder()
      .id(123)
      .result(6)
      .attributes(Map("one" -> 1, "two" -> "2"))
      .build()

    assert(res1.id.numberValue == 123)
    assert(res1.result.as[Int] == 6)
    assert(res1.attributes.size == 2)
    assert(res1.getAttribute("one").contains(1))
    assert(res1.getAttribute("two").contains("2"))
    assert(res1.getAttributeOrElse("one", 0) == 1)
    assert(res1.getAttributeOrElse("two", "0") == "2")
    assert(res1.getAttributeOrElse("three", 0) == 0)
    assert(res1.attribute[Int]("one") == 1)
    assert(res1.attribute[String]("two") == "2")
    assertThrows[NoSuchElementException](res1.attribute[Int]("three"))

    val res2 = res1.putAttribute("one", "x")
      .putAttribute("three", "xxx")

    assert(res2.id.numberValue == 123)
    assert(res1.result.as[Int] == 6)
    assert(res2.attributes.size == 3)
    assert(res2.getAttribute("one").contains("x"))
    assert(res2.getAttribute("two").contains("2"))
    assert(res2.getAttribute("three").contains("xxx"))

    val res3 = res2.removeAttribute("two")
    assert(res3.id.numberValue == 123)
    assert(res1.result.as[Int] == 6)
    assert(res3.attributes.size == 2)
    assert(res3.getAttribute("one").contains("x"))
    assert(res3.getAttribute("three").contains("xxx"))

    val res4 = res3.setAttributes(Map("1" -> 1, "2" -> 2))
    assert(res4.id.numberValue == 123)
    assert(res1.result.as[Int] == 6)
    assert(res4.attributes.size == 2)
    assert(res4.getAttribute("1").contains(1))
    assert(res4.getAttribute("2").contains(2))
  }
}
