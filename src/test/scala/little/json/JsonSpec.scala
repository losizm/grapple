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

import javax.json.{ JsonArray, JsonObject }

import scala.util.{ Failure, Success, Try }

import org.scalatest.FlatSpec

import Implicits._
import Test._

class JsonSpec extends FlatSpec {
  val root = User(0, "root")
  val guest = User(500, "guest", false)

  "JSON array" should "be parsed" in {
    val arr = Json.parse("""[0, "root", true]""").asArray
    assert(arr.getInt(0) == 0)
    assert(arr.getString(1) == "root")
    assert(arr.getBoolean(2))
  }

  it should "provide access to number value" in {
    val arr = Json.parse(s"""[${Long.MinValue}, ${Long.MaxValue}, -123.456, 123.456]""").asArray
    assert(arr.getLong(0) == Long.MinValue)
    assert(arr.getLong(1) == Long.MaxValue)
    assert(arr.getDouble(2) == -123.456)
    assert(arr.getDouble(3) == 123.456)
    assert(arr.getBigInt(0) == BigInt(Long.MinValue))
    assert(arr.getBigInt(1) == BigInt(Long.MaxValue))
    assert(arr.getBigDecimal(2) == BigDecimal(-123.456))
    assert(arr.getBigDecimal(3) == BigDecimal(123.456))
    assert(arr.isIntegral(0))
    assert(arr.isIntegral(1))
    assert(!arr.isIntegral(2))
    assert(!arr.isIntegral(3))
  }

  it should "provide access to number value with default" in {
    val arr = Json.parse("""["Not a number"]""").asArray
    assert(arr.getLong(0, 1) == 1L)
    assert(arr.getDouble(0, 1.1) == 1.1)
    assert(arr.getBigInt(0, BigInt(1)) == BigInt(1))
    assert(arr.getBigDecimal(0, BigDecimal(1.0)) == BigDecimal(1.0))
    assert(arr.getLong(1, 1) == 1L)
    assert(arr.getDouble(1, 1.1) == 1.1)
    assert(arr.getBigInt(1, BigInt(1)) == BigInt(1))
    assert(arr.getBigDecimal(1, BigDecimal(1.0)) == BigDecimal(1.0))
  }

  it should "provide access to exact number value" in {
    val arr = Json.parse("[123]")
    assert(arr.get(0).as[Int] == 123)
    assert(arr.get(0).as[Long] == 123L)
    assert(arr.get(0).as[BigInt] == BigInt(123))
  }

  it should "provide access to exact number value with default" in {
    val arr = Json.parse("""["Not a number"]""").asArray
    assert(arr.getOrElse(0, 1) == 1)
    assert(arr.getOrElse(0, 1L) == 1L)
    assert(arr.getOrElse(0, BigInt(1)) == BigInt(1))
    assert(arr.getOrElse(1, 1) == 1)
    assert(arr.getOrElse(1, 1L) == 1L)
    assert(arr.getOrElse(1, BigInt(1)) == BigInt(1))
  }

  it should "be converted to and from collection" in {
    val users = Seq(root, guest)
    val json = Json.toJson(users)

    assert(json.as[Array[User]].sameElements(users))
    assert(json.as[IndexedSeq[User]].sameElements(users))
    assert(json.as[Iterable[User]] == users)
    assert(json.as[Iterator[User]].corresponds(users)(_ == _))
    assert(json.as[List[User]].sameElements(users))
    assert(json.as[Seq[User]] == users)
    assert(json.as[Set[User]] == users.toSet)
    assert(json.as[Stream[User]].sameElements(users))
    assert(json.as[Vector[User]].sameElements(users))
    assert(json.as[Traversable[User]] == users)
  }

  it should "be created from list of values" in {
    val arr = Json.arr(
      "1",
      2,
      3L,
      4.0,
      BigDecimal(5),
      true,
      false,
      null,
      guest,
      Json.parse("""[0, "root", true]"""),
      Some("groups"),
      None.asInstanceOf[Option[User]],
      Success(root),
      Failure(new Exception).asInstanceOf[Try[User]],
      Seq(0, 1, 2),
      Seq("a", "b", "c"),
      Seq(true, false, true),
      Array(0, 1, 2),
      Array("a", "b", "c"),
      Array(true, false, true),
      Left[String, Int]("z"),
      Right[String, Int](26)
    )

    assert(arr.size == 22)
    assert(arr.getString(0) == "1")
    assert(arr.getInt(1) == 2)
    assert(arr.getLong(2) == 3L)
    assert(arr.getDouble(3) == 4.0)
    assert(arr.getBigDecimal(4) == BigDecimal(5))
    assert(arr.getBoolean(5))
    assert(!arr.getBoolean(6))
    assert(arr.isNull(7))
    assert(arr.get(8).as[User] == guest)
    assert(arr.get(9).isInstanceOf[JsonArray])
    assert(arr.getString(10) == "groups")
    assert(arr.isNull(11))
    assert(arr.get(12).as[User] == root)
    assert(arr.isNull(13))
    assert(arr.get(14).as[Seq[Int]] == Seq(0, 1, 2))
    assert(arr.get(15).as[Seq[String]] == Seq("a", "b", "c"))
    assert(arr.get(16).as[Seq[Boolean]] == Seq(true, false, true))
    assert(arr.get(17).as[Array[Int]].sameElements(Array(0, 1, 2)))
    assert(arr.get(18).as[Array[String]].sameElements(Array("a", "b", "c")))
    assert(arr.get(19).as[Array[Boolean]].sameElements(Array(true, false, true)))
    assert(arr.getString(20) == "z")
    assert(arr.getInt(21) == 26)
  }

  "JSON object" should "be parsed" in {
    val obj = Json.parse("""{ "id": 0, "name": "root", "enabled": true }""").asObject
    assert(obj.getInt("id") == 0)
    assert(obj.getString("name") == "root")
    assert(obj.getBoolean("enabled"))
  }

  it should "provide access to number value" in {
    val obj = Json.parse(s"""{ "a": ${Long.MinValue}, "b": ${Long.MaxValue}, "c": -123.456, "d": 123.456 }""").asObject
    assert(obj.getLong("a") == Long.MinValue)
    assert(obj.getLong("b") == Long.MaxValue)
    assert(obj.getDouble("c") == -123.456)
    assert(obj.getDouble("d") == 123.456)
    assert(obj.getBigInt("a") == BigInt(Long.MinValue))
    assert(obj.getBigInt("b") == BigInt(Long.MaxValue))
    assert(obj.getBigDecimal("c") == BigDecimal(-123.456))
    assert(obj.getBigDecimal("d") == BigDecimal(123.456))
    assert(obj.isIntegral("a"))
    assert(obj.isIntegral("b"))
    assert(!obj.isIntegral("c"))
    assert(!obj.isIntegral("d"))
  }

  it should "provide access to number value with default" in {
    val obj = Json.parse("""{ "a": "Not a number" }""").asObject
    assert(obj.getLong("a", 1) == 1L)
    assert(obj.getDouble("a", 1.1) == 1.1)
    assert(obj.getBigInt("a", BigInt(1)) == BigInt(1))
    assert(obj.getBigDecimal("a", BigDecimal(1.0)) == BigDecimal(1.0))
    assert(obj.getLong("b", 1) == 1L)
    assert(obj.getDouble("b", 1.1) == 1.1)
    assert(obj.getBigInt("b", BigInt(1)) == BigInt(1))
    assert(obj.getBigDecimal("b", BigDecimal(1.0)) == BigDecimal(1.0))
  }

  it should "provide access to exact number value" in {
    val obj = Json.parse("""{ "a": 123 }""")
    assert(obj.get("a").as[Int] == 123)
    assert(obj.get("a").as[Long] == 123L)
    assert(obj.get("a").as[BigInt] == BigInt(123))
  }

  it should "provide access to exact number value with default" in {
    val obj = Json.parse("""{ "a": "Not a number" }""").asObject
    assert(obj.getOrElse("a", 1) == 1)
    assert(obj.getOrElse("a", 1L) == 1L)
    assert(obj.getOrElse("a", BigInt(1)) == BigInt(1))
    assert(obj.getOrElse("b", 1) == 1)
    assert(obj.getOrElse("b", 1L) == 1L)
    assert(obj.getOrElse("b", BigInt(1)) == BigInt(1))
    assert(obj.getOrElse("b", BigInt(1)) == BigInt(1))
  }

  it should "be converted to and from case class" in {
    val json = Json.toJson(root).asObject

    assert(json.getInt("id") == 0)
    assert(json.getString("name") == "root")
    assert(json.getBoolean("enabled"))
    assert(json.as[User] == root)
    assert(json.asOption[User] == Some(root))
    assert(json.asTry[User] == Success(root))
    assert(json.as[Option[User]] == Some(root))
    assert(json.as[Try[User]] == Success(root))
  }

  it should "be created from list of fields" in {
    val obj = Json.obj(
      "a" -> "1",
      "b" -> 2,
      "c" -> 3L,
      "d" -> 4.0,
      "e" -> BigDecimal(5),
      "f" -> true,
      "g" -> false,
      "h" -> null,
      "i" -> guest,
      "j" -> Json.parse("""[0, "root", true]"""),
      "k" -> Some("groups"),
      "l" -> None.asInstanceOf[Option[User]],
      "m" -> Success(root),
      "n" -> Failure(new Exception).asInstanceOf[Try[User]],
      "o" -> Seq(0, 1, 2),
      "p" -> Seq("a", "b", "c"),
      "q" -> Seq(true, false, true),
      "r" -> Array(0, 1, 2),
      "s" -> Array("a", "b", "c"),
      "t" -> Array(true, false, true),
      "u" -> Left[String, Int]("z"),
      "v" -> Right[String, Int](26)
    )

    assert(obj.size == 22)
    assert(obj.getString("a") == "1")
    assert(obj.getInt("b") == 2)
    assert(obj.getLong("c") == 3L)
    assert(obj.getDouble("d") == 4.0)
    assert(obj.getBigDecimal("e") == BigDecimal(5))
    assert(obj.getBoolean("f"))
    assert(!obj.getBoolean("g"))
    assert(obj.isNull("h"))
    assert(obj.get("i").as[User] == guest)
    assert(obj.get("j").isInstanceOf[JsonArray])
    assert(obj.getString("k") == "groups")
    assert(obj.isNull("l"))
    assert(obj.get("m").as[User] == root)
    assert(obj.isNull("n"))
    assert(obj.get("o").as[Seq[Int]] == Seq(0, 1, 2))
    assert(obj.get("p").as[Seq[String]] == Seq("a", "b", "c"))
    assert(obj.get("q").as[Seq[Boolean]] == Seq(true, false, true))
    assert(obj.get("r").as[Array[Int]].sameElements(Array(0, 1, 2)))
    assert(obj.get("s").as[Array[String]].sameElements(Array("a", "b", "c")))
    assert(obj.get("t").as[Array[Boolean]].sameElements(Array(true, false, true)))
    assert(obj.getString("u") == "z")
    assert(obj.getInt("v") == 26)
  }

  it should "be traversed" in {
    val json = Json.parse("""{
      "computer": {
        "name": "localhost",
        "users": [
          { "id": 0, "name": "root" },
          { "id": 500, "name": "guest", "enabled": false }
        ]
      }
    }""")

    assert((json \ "computer" \ "users" \ 0 \ "id").as[Int] == 0)
    assert((json \ "computer" \ "users" \ 0 \ "name").as[String] == "root")
    assert((json \ "computer" \ "users" \ 1 \ "id").as[Int] == 500)
    assert((json \ "computer" \ "users" \ 1 \ "name").as[String] == "guest")
    assert(!(json \ "computer" \ "users" \ 1 \ "enabled").as[Boolean])

    val names = (json \\ "name").map(_.as[String])
    assert(names.sameElements(Seq("localhost", "root", "guest")))
  }
}
