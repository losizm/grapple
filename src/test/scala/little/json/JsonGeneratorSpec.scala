/*
 * Copyright 2018 Carlos Conyers
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

import java.io.StringWriter

import org.scalatest.FlatSpec

import Implicits._
import Test._

class JsonGeneratorSpec extends FlatSpec {
  val user = User(0, "root", true)

  "JSON generator" should "generate array" in {
    val out = new StringWriter()

    Json.createGenerator(out)
      .writeStartArray()
      .write(user)
      .writeNullable(user)
      .writeNullable(null.asInstanceOf[User])
      .write(Some(user))
      .write(None.asInstanceOf[Option[User]])
      .write(1)
      .write(1L)
      .write(1.1)
      .write(BigInt(1))
      .writeNullable(BigInt(1))
      .write(Option(BigInt(1)))
      .write(BigDecimal(1.0))
      .writeNullable(BigDecimal(1.0))
      .write(Some(BigDecimal(1.0)))
      .write(Some(BigDecimal(1.0)))
      .write(Seq(user, user, user))
      .write(Array(user, user, user))
      .write(Seq("hello", "hello"))
      .write(Array("hello", "hello"))
      .write(Seq(0, 1, 2))
      .write(Array(0, 1, 2))
      .write(Seq(0L, 1L, 2L))
      .write(Array(0L, 1L, 2L))
      .write(Seq(0.0, 1.0, 2.0))
      .write(Array(0.0, 1.0, 2.0))
      .write(Seq(BigInt(0), BigInt(1), BigInt(2)))
      .write(Array(BigInt(0), BigInt(1), BigInt(2)))
      .write(Seq(BigDecimal(0.0), BigDecimal(1.0), BigDecimal(2.0)))
      .write(Array(BigDecimal(0.0), BigDecimal(1.0), BigDecimal(2.0)))
      .write(Seq(true, false, false))
      .write(Array(true, false, true))
      .writeEnd()
      .close()
  }

  it should "generate object" in {
    val out = new StringWriter()

    Json.createGenerator(out)
      .writeStartObject()
      .write("a", user)
      .writeNullable("b1", user)
      .writeNullable("b2", null.asInstanceOf[User])
      .write("c1", Some(user))
      .write("c2", None.asInstanceOf[Option[User]])
      .write("d", 1)
      .write("e", 1L)
      .write("f", 1.1)
      .write("g", BigInt(1))
      .writeNullable("h", BigInt(1))
      .write("i", Option(BigInt(1)))
      .write("j", BigDecimal(1.0))
      .writeNullable("k", BigDecimal(1.0))
      .write("l", Some(BigDecimal(1.0)))
      .write("m1", Seq(user, user, user))
      .write("m2", Array(user, user, user))
      .write("n1", Seq("hello", "hello"))
      .write("n2", Array("hello", "hello"))
      .write("o1", Seq(0, 1, 2))
      .write("o2", Array(0, 1, 2))
      .write("p1", Seq(0L, 1L, 2L))
      .write("p2", Array(0L, 1L, 2L))
      .write("q1", Seq(0.0, 1.0, 2.0))
      .write("q2", Array(0.0, 1.0, 2.0))
      .write("r1", Seq(BigInt(0), BigInt(1), BigInt(2)))
      .write("r2", Array(BigInt(0), BigInt(1), BigInt(2)))
      .write("s1", Seq(BigDecimal(0.0), BigDecimal(1.0), BigDecimal(2.0)))
      .write("s2", Array(BigDecimal(0.0), BigDecimal(1.0), BigDecimal(2.0)))
      .write("t1", Seq(true, false, false))
      .write("t2", Array(true, false, true))
      .writeEnd()
      .close()
  }
}
