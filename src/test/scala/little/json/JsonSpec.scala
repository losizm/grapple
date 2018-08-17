package little.json

import javax.json.{ JsonArray, JsonObject, JsonValue }

import scala.util.Success

import org.scalatest.FlatSpec

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
  }

  it should "provide access to number value with default" in {
    val arr = Json.parse[JsonArray]("[]")
    assert(arr.getLong(0, 1) == 1L)
    assert(arr.getDouble(0, 1.1) == 1.1)
    assert(arr.getBigInt(0, BigInt(1)) == BigInt(1))
    assert(arr.getBigDecimal(0, BigDecimal(1.0)) == BigDecimal(1.0))
  }

  it should "provide access to exact number value" in {
    val arr = Json.parse[JsonArray]("[123]")
    assert(arr.getIntExact(0) == 123)
    assert(arr.getLongExact(0) == 123L)
    assert(arr.getBigIntExact(0) == BigInt(123))
  }

  it should "provide access to exact number value with default" in {
    val arr = Json.parse[JsonArray]("[]")
    assert(arr.getIntExact(0, 1) == 1)
    assert(arr.getLongExact(0, 1L) == 1L)
    assert(arr.getBigIntExact(0, BigInt(1)) == BigInt(1))
  }

  "JSON object" should "be parsed" in {
    val obj = Json.parse[JsonObject]("""{ "id": 0, "name": "root", "isRoot": true }""")
    assert(obj.getInt("id") == 0)
    assert(obj.getString("name") == "root")
    assert(obj.getBoolean("isRoot"))
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
  }

  it should "provide access to number value with default" in {
    val obj = Json.parse[JsonObject]("{}")
    assert(obj.getLong("a", 1) == 1L)
    assert(obj.getDouble("a", 1.1) == 1.1)
    assert(obj.getBigInt("a", BigInt(1)) == BigInt(1))
    assert(obj.getBigDecimal("a", BigDecimal(1.0)) == BigDecimal(1.0))
  }

  it should "provide access to exact number value" in {
    val obj = Json.parse[JsonObject]("""{ "a": 123 }""")
    assert(obj.getIntExact("a") == 123)
    assert(obj.getLongExact("a") == 123L)
    assert(obj.getBigIntExact("a") == BigInt(123))
  }

  it should "provide access to exact number value with default" in {
    val obj = Json.parse[JsonObject]("{}")
    assert(obj.getIntExact("a", 1) == 1)
    assert(obj.getLongExact("a", 1L) == 1L)
    assert(obj.getBigIntExact("a", BigInt(1)) == BigInt(1))
  }

  "JSON value" should "be converted to and from case class" in {
    case class User(id: Int, name: String, isRoot: Boolean)

    implicit val UserToJson: (User => JsonValue) = { user =>
      val obj = Json.createObjectBuilder()
      obj.add("id", user.id)
      obj.add("name", user.name)
      obj.add("isRoot", user.isRoot)
      obj.build()
    }

    implicit val JsonToUser: (JsonValue => User) = { json =>
      User(json.getInt("id"), json.getString("name"), json.getBoolean("isRoot"))
    }

    val user = User(0, "root", true)
    val json = Json.toJson(user)

    assert(json.getInt("id") == 0)
    assert(json.getString("name") == "root")
    assert(json.getBoolean("isRoot"))
    assert(json.as[User] == user)
    assert(json.asOption[User] == Some(user))
    assert(json.asTry[User] == Success(user))
  }
}
