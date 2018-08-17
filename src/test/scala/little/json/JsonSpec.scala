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

  "JSON object" should "be parsed" in {
    val obj = Json.parse[JsonObject]("""{ "id": 0, "name": "root", "isRoot": true }""")
    assert(obj.getInt("id") == 0)
    assert(obj.getString("name") == "root")
    assert(obj.getBoolean("isRoot"))
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
    assert(json.asOpt[User] == Some(user))
    assert(json.asTry[User] == Success(user))
  }
}
