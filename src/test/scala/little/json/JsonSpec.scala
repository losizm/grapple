package little.json

import javax.json.{ JsonArray, JsonObject }

import scala.util.Success

import org.scalatest.FlatSpec

import Implicits._
import Test._

class JsonSpec extends FlatSpec {
  "JSON array" should "be parsed" in {
    val arr = Json.parse[JsonArray]("""[0, "root", true]""")
    assert(arr.getInt(0) == 0)
    assert(arr.getString(1) == "root")
    assert(arr.getBoolean(2))
  }

  it should "provide access to number value" in {
    val arr = Json.parse[JsonArray](s"""[${Long.MinValue}, ${Long.MaxValue}, -123.456, 123.456]""")
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
    val arr = Json.parse[JsonArray]("""["Not a number"]""")
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
    val arr = Json.parse[JsonArray]("[123]")
    assert(arr.get(0).as[Int] == 123)
    assert(arr.get(0).as[Long] == 123L)
    assert(arr.get(0).as[BigInt] == BigInt(123))
  }

  it should "provide access to exact number value with default" in {
    val arr = Json.parse[JsonArray]("""["Not a number"]""")
    assert(arr.getOrElse(0, 1) == 1)
    assert(arr.getOrElse(0, 1L) == 1L)
    assert(arr.getOrElse(0, BigInt(1)) == BigInt(1))
    assert(arr.getOrElse(1, 1) == 1)
    assert(arr.getOrElse(1, 1L) == 1L)
    assert(arr.getOrElse(1, BigInt(1)) == BigInt(1))
  }

  "JSON object" should "be parsed" in {
    val obj = Json.parse[JsonObject]("""{ "id": 0, "name": "root", "enabled": true }""")
    assert(obj.getInt("id") == 0)
    assert(obj.getString("name") == "root")
    assert(obj.getBoolean("enabled"))
  }

  it should "provide access to number value" in {
    val obj = Json.parse[JsonObject](s"""{ "a": ${Long.MinValue}, "b": ${Long.MaxValue}, "c": -123.456, "d": 123.456 }""")
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
    val obj = Json.parse[JsonObject]("""{ "a": "Not a number" }""")
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
    val obj = Json.parse[JsonObject]("""{ "a": 123 }""")
    assert(obj.get("a").as[Int] == 123)
    assert(obj.get("a").as[Long] == 123L)
    assert(obj.get("a").as[BigInt] == BigInt(123))
  }

  it should "provide access to exact number value with default" in {
    val obj = Json.parse[JsonObject]("""{ "a": "Not a number" }""")
    assert(obj.getOrElse("a", 1) == 1)
    assert(obj.getOrElse("a", 1L) == 1L)
    assert(obj.getOrElse("a", BigInt(1)) == BigInt(1))
    assert(obj.getOrElse("b", 1) == 1)
    assert(obj.getOrElse("b", 1L) == 1L)
    assert(obj.getOrElse("b", BigInt(1)) == BigInt(1))
    assert(obj.getOrElse("b", BigInt(1)) == BigInt(1))
  }

  "JSON value" should "be converted to and from case class" in {
    val user = User(0, "root", true)
    val json = Json.toJson(user).asObject

    assert(json.getInt("id") == 0)
    assert(json.getString("name") == "root")
    assert(json.getBoolean("enabled"))
    assert(json.as[User] == user)
    assert(json.asOption[User] == Some(user))
    assert(json.asTry[User] == Success(user))
  }

  "JSON array of objects" should "be converted to collection" in {
    val json = Json.parse[JsonArray]("""[{ "id": 0, "name": "root" }, { "id": 500, "name": "guest", "enabled": false }]""")
    val users = Seq(User(0, "root"), User(500, "guest", false))

    assert(json.as[Array[User]].corresponds(users)(_ == _))
    assert(json.as[IndexedSeq[User]].corresponds(users)(_ == _))
    assert(json.as[Iterable[User]] == users)
    assert(json.as[Iterator[User]].corresponds(users)(_ == _))
    assert(json.as[List[User]].corresponds(users)(_ == _))
    assert(json.as[Seq[User]] == users)
    assert(json.as[Set[User]] == users.toSet)
    assert(json.as[Stream[User]].corresponds(users)(_ == _))
    assert(json.as[Vector[User]].corresponds(users)(_ == _))
    assert(json.as[Traversable[User]] == users)
  }
}
