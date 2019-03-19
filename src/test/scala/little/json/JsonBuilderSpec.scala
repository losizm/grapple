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

import scala.util.{ Failure, Success, Try }

import org.scalatest.FlatSpec

import Implicits._
import Test._

class JsonBuilderSpec extends FlatSpec {
  val user = User(0, "root", true)

  "JSON builder" should "build array" in {
    Json.createArrayBuilder()
      .add(user)
      .addNullable(user)
      .addNullable(null.asInstanceOf[User])
      .add(Some(user))
      .add(None.asInstanceOf[Option[User]])
      .add(Success(user))
      .add(Failure(new Exception).asInstanceOf[Try[User]])
      .add(Try(Json.toJson(user)).toOption)
      .add(Try(Json.toJson(user)))
      .add("hello")
      .add(1)
      .add(1L)
      .add(1.1)
      .add(BigInt(1))
      .addNullable(BigInt(1))
      .add(Option(BigInt(1)))
      .add(BigDecimal(1.0))
      .addNullable(BigDecimal(1.0))
      .add(Some(BigDecimal(1.0)))
      .add(Seq(user, user, user))
      .add(Array(user, user, user))
      .add(Seq("hello", "hello"))
      .add(Array("hello", "hello"))
      .add(Seq(0, 1, 2))
      .add(Array(0, 1, 2))
      .add(Seq(0L, 1L, 2L))
      .add(Array(0L, 1L, 2L))
      .add(Seq(0.0, 1.0, 2.0))
      .add(Array(0.0, 1.0, 2.0))
      .add(Seq(BigInt(0), BigInt(1), BigInt(2)))
      .add(Array(BigInt(0), BigInt(1), BigInt(2)))
      .add(Seq(BigDecimal(0.0), BigDecimal(1.0), BigDecimal(2.0)))
      .add(Array(BigDecimal(0.0), BigDecimal(1.0), BigDecimal(2.0)))
      .add(Seq(true, false, false))
      .add(Array(true, false, true))
      .add(Left[String, Int]("z"))
      .add(Right[String, Int](26))
      .build()
  }

  it should "build object" in {
    Json.createObjectBuilder()
      .add("a", user)
      .addNullable("b1", user)
      .addNullable("b2", null.asInstanceOf[User])
      .add("c1", Some(user))
      .add("c2", None.asInstanceOf[Option[User]])
      .add("c3", Success(user))
      .add("c4", Failure(new Exception).asInstanceOf[Try[User]])
      .add("c5", Try(Json.toJson(user)).toOption)
      .add("c6", Try(Json.toJson(user)))
      .add("d", 1)
      .add("e", 1L)
      .add("f", 1.1)
      .add("g", BigInt(1))
      .addNullable("h", BigInt(1))
      .add("i", Option(BigInt(1)))
      .add("j", BigDecimal(1.0))
      .addNullable("k", BigDecimal(1.0))
      .add("l", Some(BigDecimal(1.0)))
      .add("m1", Seq(user, user, user))
      .add("m2", Array(user, user, user))
      .add("n1", Seq("hello", "hello"))
      .add("n2", Array("hello", "hello"))
      .add("o1", Seq(0, 1, 2))
      .add("o2", Array(0, 1, 2))
      .add("p1", Seq(0L, 1L, 2L))
      .add("p2", Array(0L, 1L, 2L))
      .add("q1", Seq(0.0, 1.0, 2.0))
      .add("q2", Array(0.0, 1.0, 2.0))
      .add("r1", Seq(BigInt(0), BigInt(1), BigInt(2)))
      .add("r2", Array(BigInt(0), BigInt(1), BigInt(2)))
      .add("s1", Seq(BigDecimal(0.0), BigDecimal(1.0), BigDecimal(2.0)))
      .add("s2", Array(BigDecimal(0.0), BigDecimal(1.0), BigDecimal(2.0)))
      .add("t1", Seq(true, false, false))
      .add("t2", Array(true, false, true))
      .add("u", Left[String, Int]("z"))
      .add("v", Right[String, Int](26))
      .build()
  }
}
