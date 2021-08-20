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

import Implicits.given

class JsonNumberSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "create JsonNumber with Int value" in {
    val n = JsonNumber(42)
    assert(n.shortValue == 42)
    assert(n.intValue == 42)
    assert(n.longValue == 42L)
    assert(n.floatValue == 42.0f)
    assert(n.doubleValue == 42.0)
    assert(n.bigIntValue == BigInt(42))
    assert(n.bigDecimalValue == BigDecimal(42))
    assert(n.toString == "42")
    assertThrows[ClassCastException](n.as[String])
    assert(n.as[Short] == 42)
    assert(n.as[Int] == 42)
    assert(n.as[Long] == 42L)
    assert(n.as[Float] == 42.0f)
    assert(n.as[Double] == 42.0)
    assert(n.as[BigInt] == BigInt(42))
    assert(n.as[BigDecimal] == BigDecimal(42))
    assertThrows[ClassCastException](n.as[Boolean])
  }

  it should "create JsonNumber with Long value" in {
    val n = JsonNumber(57L)
    assert(n.shortValue == 57)
    assert(n.intValue == 57)
    assert(n.longValue == 57L)
    assert(n.floatValue == 57.0f)
    assert(n.doubleValue == 57.0)
    assert(n.bigDecimalValue == BigDecimal(57))
    assert(n.bigDecimalValue == BigDecimal(57))
    assert(n.toString == "57")
    assertThrows[ClassCastException](n.as[String])
    assert(n.as[Short] == 57)
    assert(n.as[Int] == 57)
    assert(n.as[Long] == 57L)
    assert(n.as[Float] == 57.0f)
    assert(n.as[Double] == 57.0)
    assert(n.as[BigInt] == BigInt(57))
    assert(n.as[BigDecimal] == BigDecimal(57))
    assertThrows[ClassCastException](n.as[Boolean])
  }

  it should "create JsonNumber with Double value" in {
    val n = JsonNumber(0.12345)
    assertThrows[ArithmeticException](n.shortValue)
    assertThrows[ArithmeticException](n.intValue)
    assertThrows[ArithmeticException](n.longValue)
    assert(n.floatValue == 0.12345f)
    assert(n.doubleValue == 0.12345)
    assertThrows[ArithmeticException](n.bigIntValue)
    assert(n.bigDecimalValue == BigDecimal(0.12345))
    assert(n.toString == "0.12345")
    assertThrows[ClassCastException](n.as[String])
    assertThrows[ArithmeticException](n.as[Short])
    assertThrows[ArithmeticException](n.as[Int])
    assertThrows[ArithmeticException](n.as[Long])
    assert(n.as[Float] == 0.12345f)
    assert(n.as[Double] == 0.12345)
    assertThrows[ArithmeticException](n.as[BigInt])
    assert(n.as[BigDecimal] == 0.12345)
    assertThrows[ClassCastException](n.as[Boolean])
  }

  it should "create JsonNumber with BigDecimal value" in {
    val n1 = JsonNumber(BigDecimal("9876543210"))
    assertThrows[ArithmeticException](n1.shortValue)
    assertThrows[ArithmeticException](n1.intValue)
    assert(n1.longValue == 9876543210L)
    assert(n1.floatValue == 9876543210.0f)
    assert(n1.doubleValue == 9876543210.0)
    assert(n1.bigIntValue == BigInt("9876543210"))
    assert(n1.bigDecimalValue == BigDecimal("9876543210"))
    assert(n1.toString == "9876543210")
    assertThrows[ClassCastException](n1.as[String])
    assertThrows[ArithmeticException](n1.as[Short])
    assertThrows[ArithmeticException](n1.as[Int])
    assert(n1.as[Long] == 9876543210L)
    assert(n1.as[Float] == 9876543210.0f)
    assert(n1.as[Double] == 9876543210.0)
    assert(n1.as[BigInt] == BigInt("9876543210"))
    assert(n1.as[BigDecimal] == BigDecimal("9876543210"))
    assertThrows[ClassCastException](n1.as[Boolean])

    val n2 = JsonNumber(BigDecimal("9876543210123456789"))
    assertThrows[ArithmeticException](n2.shortValue)
    assertThrows[ArithmeticException](n2.intValue)
    assertThrows[ArithmeticException](n2.longValue)
    assertThrows[ArithmeticException](n2.shortValue)
    assert(n2.doubleValue == 9876543210123456789.0)
    assert(n2.bigIntValue == BigInt("9876543210123456789"))
    assert(n2.bigDecimalValue == BigDecimal("9876543210123456789"))
    assert(n2.toString == "9876543210123456789")
    assertThrows[ClassCastException](n2.as[String])
    assertThrows[ArithmeticException](n2.as[Short])
    assertThrows[ArithmeticException](n2.as[Int])
    assertThrows[ArithmeticException](n2.as[Long])
    assertThrows[ArithmeticException](n2.as[Short])
    assert(n2.as[Double] == 9876543210123456789.0)
    assert(n2.as[BigInt] == BigInt("9876543210123456789"))
    assert(n2.as[BigDecimal] == BigDecimal("9876543210123456789"))
    assertThrows[ClassCastException](n2.as[Boolean])
  }

  it should "create JsonNumber with String value" in {
    val n = JsonNumber("12345")
    assert(n.shortValue == 12345)
    assert(n.intValue == 12345)
    assert(n.longValue == 12345L)
    assert(n.shortValue == 12345.0f)
    assert(n.doubleValue == 12345.0)
    assert(n.bigIntValue == BigInt(12345))
    assert(n.bigDecimalValue == BigDecimal(12345))
    assert(n.toString == "12345")
    assertThrows[ClassCastException](n.as[String])
    assert(n.as[Short] == 12345)
    assert(n.as[Int] == 12345)
    assert(n.as[Long] == 12345L)
    assert(n.as[Float] == 12345.0f)
    assert(n.as[Double] == 12345.0)
    assert(n.as[BigInt] == BigInt("12345"))
    assert(n.as[BigDecimal] == BigDecimal("12345"))
    assertThrows[ClassCastException](n.as[Boolean])
  }

  it should "compare JsonNumbers" in {
    assert(JsonNumber(123) == JsonNumber(123))
    assert(JsonNumber(123) == JsonNumber(123L))
    assert(JsonNumber(123) == JsonNumber(123.0f))
    assert(JsonNumber(123) == JsonNumber(123.0))
    assert(JsonNumber(123) == JsonNumber(BigInt(123)))
    assert(JsonNumber(123) == JsonNumber(BigDecimal(123)))
  }

  it should "destructure JsonNumber" in {
    val n = JsonNumber(123)

    assert {
      n match
        case JsonNumber(456) => false
        case JsonNumber(num) => num == 123
        case _               => false
    }

    assert {
      n match
        case JsonNumber(456) => false
        case JsonNumber(123) => true
        case _               => false
    }
  }
