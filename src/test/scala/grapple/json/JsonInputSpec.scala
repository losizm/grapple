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

import java.time.LocalDate

import scala.collection.immutable.{ ListMap, TreeMap }
import scala.language.implicitConversions
import scala.util.Try

class JsonInputSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "use default JsonInputs" in {
    val test = Json.arr(
      "hello",
      -128,
      123456789L,
      123.456,
      999.000999,
      true,
      false,
      Json.arr(1, 2, 3),
      Json.arr("a", "b", "c"),
      JsonNull
    )

    assert(test(0).as[String] == "hello")
    assert(test(1).as[Int] == -128)
    assert(test(2).as[Long] == 123456789L)
    assert(test(3).as[Double] == 123.456)
    assert(test(4).as[BigDecimal] == BigDecimal(999.000999))
    assert(test(5).as[Boolean])
    assert(!test(6).as[Boolean])
    assert(test(7).as[Array[Int]] sameElements Array(1, 2, 3))
    assert(test(7).as[Seq[Int]] sameElements Seq(1, 2, 3))
    assert(test(7).as[Set[Int]] == Set(1, 2, 3))
    assert(test(7).as[List[Int]] == List(1, 2, 3))
    assert(test(8).as[Array[String]] sameElements Array("a", "b", "c"))
    assert(test(8).as[Seq[String]] sameElements Seq("a", "b", "c"))
    assert(test(8).as[Set[String]] == Set("a", "b", "c"))
    assert(test(8).as[List[String]] == List("a", "b", "c"))

    assert(test(0).as[Option[String]] contains "hello")
    assert(test(1).as[Option[Int]] contains -128)
    assert(test(2).as[Option[Long]] contains 123456789L)
    assert(test(3).as[Option[Double]] contains 123.456)
    assert(test(4).as[Option[BigDecimal]] contains BigDecimal(999.000999))
    assert(test(5).as[Option[Boolean]] contains true)
    assert(test(6).as[Option[Boolean]] contains false)
    assert(test(7).as[Option[Set[Int]]] contains Set(1, 2, 3))
    assert(test(8).as[Option[Set[String]]] contains Set("a", "b", "c"))
    assert(test(9).as[Option[String]].isEmpty)

    assert(test(0).as[Either[Int, String]] == Right("hello"))
    assert(test(0).as[Either[String, Int]] == Left("hello"))

    assert(test(1).as[Either[String, Int]] == Right(-128))
    assert(test(1).as[Either[Int, String]] == Left(-128))

    assert(test(7).as[Either[Int, Seq[Int]]] == Right(Seq(1, 2, 3)))
    assert(test(7).as[Either[Seq[Int], Int]] == Left(Seq(1, 2, 3)))

    assert(test(8).as[Either[Seq[Int], Seq[String]]] == Right(Seq("a", "b", "c")))
    assert(test(8).as[Either[Seq[String], Seq[Int]]] == Left(Seq("a", "b", "c")))

    assert(test(9).as[Either[Int, Option[Int]]] == Right(None))
    assert(test(9).as[Either[Option[Int], Int]] == Left(None))

    assert(test(0).as[Try[String]].get == "hello")
    assertThrows[ClassCastException](test(0).as[Try[Int]].get)
  }

  it should "read JsonObject as Map" in {
    given JsonInput[LocalDate] = json => LocalDate.parse(json.as[String])

    val json = Json.obj(
      "lupita" -> "1983-03-01",
      "denzel" -> "1954-12-28",
      "wesley" -> "1962-07-31",
      "kerry"  -> "1977-01-31"
    )

    val dob1 = json.as[ListMap[String, LocalDate]]
    assert(dob1.size == 4)
    assert(dob1.keys.toSeq == Seq("lupita", "denzel", "wesley", "kerry"))
    assert(dob1("lupita") == LocalDate.parse("1983-03-01"))
    assert(dob1("denzel") == LocalDate.parse("1954-12-28"))
    assert(dob1("wesley") == LocalDate.parse("1962-07-31"))
    assert(dob1("kerry")  == LocalDate.parse("1977-01-31"))

    val dob2 = json.as[TreeMap[String, LocalDate]]
    assert(dob2.size == 4)
    assert(dob2.keys.toSeq == Seq("denzel", "kerry", "lupita", "wesley"))
    assert(dob2("lupita") == LocalDate.parse("1983-03-01"))
    assert(dob2("denzel") == LocalDate.parse("1954-12-28"))
    assert(dob2("wesley") == LocalDate.parse("1962-07-31"))
    assert(dob2("kerry")  == LocalDate.parse("1977-01-31"))
  }

  it should "use custom JsonInput" in {
    case class User(id: Int, name: String, groups: Set[String])

    given JsonInput[User] = { json =>
      User(
        id     = (json \ "id").as[Int],
        name   = (json \ "name").as[String],
        groups = (json \ "groups").as[Set[String]]
      )
    }

    val root  = Json.obj("id" -> 0,    "name" -> "root",  "groups" -> Json.arr("root", "admin"))
    val guest = Json.obj("id" -> 1000, "name" -> "guest", "groups" -> Json.arr("guest"))
    val other = Json.obj("id" -> 1001, "name" -> "other", "groups" -> Json.arr("other"))
    val users = Json.arr(root, guest, other)

    assert(root.as[User]  == User(0,    "root",  Set("root", "admin")))
    assert(guest.as[User] == User(1000, "guest", Set("guest")))
    assert(other.as[User] == User(1001, "other", Set("other")))

    assert(root.as[Either[Int, User]] == Right(User(0, "root", Set("root", "admin"))))
    assert(root.as[Either[User, Int]] == Left(User(0, "root", Set("root", "admin"))))
    assert(root.as[Option[User]] == Some(User(0, "root", Set("root", "admin"))))

    val seq = users.as[Seq[User]]
    assert(seq.size == 3)
    assert(seq(0) == User(0,    "root",  Set("root", "admin")))
    assert(seq(1) == User(1000, "guest", Set("guest")))
    assert(seq(2) == User(1001, "other", Set("other")))

    val set = users.as[Set[User]]
    assert(seq.size == 3)
    assert(set.contains(User(0,    "root",  Set("root", "admin"))))
    assert(set.contains(User(1000, "guest", Set("guest"))))
    assert(set.contains(User(1001, "other", Set("other"))))

    val array = users.as[Array[User]]
    assert(array.size == 3)
    assert(array(0) == User(0,    "root",  Set("root", "admin")))
    assert(array(1) == User(1000, "guest", Set("guest")))
    assert(array(2) == User(1001, "other", Set("other")))
  }
