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

class JsonRpcResponseSpec extends org.scalatest.flatspec.AnyFlatSpec:
  case class Result(name: String, value: Int)

  given JsonInput[Result] =
    case json: JsonObject => Result(json("name"), json("value"))
    case _                => throw IllegalArgumentException("Expected JSON object")

  given JsonOutput[Result] =
    param => Json.obj("name" -> param.name, "value" -> param.value)

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
    assert(res.error.message == "Invalid Request")
    assert(res.error.data.get.as[String] == "Invalid request")
  }

  it should "create JsonRpcResponse with attributes" in {
    val res1 = JsonRpcResponseBuilder()
      .id(123)
      .result(6)
      .attributes("one" -> 1, "two" -> "2")
      .toJsonRpcResponse()

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

  it should "create JsonRpcResponse with either result or error" in {
    val value = 6
    val res1 = JsonRpcResponseBuilder()
      .id(123)
      .resultOrError(if value < 10 then value else InvalidParams())
      .toJsonRpcResponse()
    assert(res1.id.numberValue == 123)
    assert(res1.isResult)
    assert(!res1.isError)
    assert(res1.result.as[Int] == 6)
    assertThrows[NoSuchElementException](res1.error)

    val res2 = JsonRpcResponseBuilder()
      .id(123)
      .resultOrError(if value < 10 then InvalidParams() else value)
      .toJsonRpcResponse()
    assert(res2.id.numberValue == 123)
    assert(!res2.isResult)
    assert(res2.isError)
    assertThrows[NoSuchElementException](res2.result)
    assert(res2.error.isInvalidParams)
  }

  it should "create JsonRpcResponse with default on failure" in {
    val value = 6
    val res1 = JsonRpcResponseBuilder()
      .id(123)
      .tryResult(if value < 10 then value else throw InvalidParams())
      .toJsonRpcResponse()
    assert(res1.id.numberValue == 123)
    assert(res1.isResult)
    assert(!res1.isError)
    assert(res1.result.as[Int] == 6)
    assertThrows[NoSuchElementException](res1.error)

    val res2 = JsonRpcResponseBuilder()
      .id(123)
      .tryResult(if value < 10 then throw InvalidParams() else value)
      .toJsonRpcResponse()
    assert(res2.id.numberValue == 123)
    assert(!res2.isResult)
    assert(res2.isError)
    assertThrows[NoSuchElementException](res2.result)
    assert(res2.error.isInvalidParams)

    val res3 = JsonRpcResponseBuilder()
      .id(123)
      .tryResult(if value < 10 then value else throw IllegalArgumentException())
      .toJsonRpcResponse()
    assert(res3.id.numberValue == 123)
    assert(res3.isResult)
    assert(!res3.isError)
    assert(res3.result.as[Int] == 6)
    assertThrows[NoSuchElementException](res3.error)

    val res4 = JsonRpcResponseBuilder()
      .id(123)
      .tryResult(if value < 10 then throw IllegalArgumentException() else value)
      .toJsonRpcResponse()
    assert(res4.id.numberValue == 123)
    assert(!res4.isResult)
    assert(res4.isError)
    assertThrows[NoSuchElementException](res4.result)
    assert(res4.error.isInternalError)
  }

  it should "create JsonRpcResponse with custom on failure" in {
    given onFailure: PartialFunction[Throwable, JsonRpcError] =
      case _: IllegalArgumentException => InvalidParams()

    val value = 6
    val res1 = JsonRpcResponseBuilder()
      .id(123)
      .tryResult(if value < 10 then value else throw IllegalArgumentException())
      .toJsonRpcResponse()
    assert(res1.id.numberValue == 123)
    assert(res1.isResult)
    assert(!res1.isError)
    assert(res1.result.as[Int] == 6)
    assertThrows[NoSuchElementException](res1.error)

    val res2 = JsonRpcResponseBuilder()
      .id(123)
      .tryResult(if value < 10 then throw IllegalArgumentException() else value)
      .toJsonRpcResponse()
    assert(res2.id.numberValue == 123)
    assert(!res2.isResult)
    assert(res2.isError)
    assertThrows[NoSuchElementException](res2.result)
    assert(res2.error.isInvalidParams)

    val res3 = JsonRpcResponseBuilder()
      .id(123)
      .tryResult(if value < 10 then value else throw ArithmeticException())
      .toJsonRpcResponse()
    assert(res3.id.numberValue == 123)
    assert(res3.isResult)
    assert(!res3.isError)
    assert(res3.result.as[Int] == 6)
    assertThrows[NoSuchElementException](res3.error)

    assertThrows[ArithmeticException](
      JsonRpcResponseBuilder()
        .id(123)
        .tryResult(if value < 10 then throw ArithmeticException() else value)
        .toJsonRpcResponse()
    )

    val res4 = JsonRpcResponseBuilder()
      .id(123)
      .tryResult(if value < 10 then throw ArithmeticException() else value)(using defaultOnFailure)
      .toJsonRpcResponse()
    assert(res4.id.numberValue == 123)
    assert(!res4.isResult)
    assert(res4.isError)
    assertThrows[NoSuchElementException](res4.result)
    assert(res4.error.isInternalError)
  }
