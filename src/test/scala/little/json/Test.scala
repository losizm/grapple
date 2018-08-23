package little.json

import javax.json.{ JsonException, JsonObject, JsonValue }

import scala.util.Success

object Test {
  case class User(id: Int, name: String, enabled: Boolean = true)

  implicit val userToJson: ToJson[User] = { user =>
    val json = Json.createObjectBuilder()
    json.add("id", user.id)
    json.add("name", user.name)
    json.add("enabled", user.enabled)
    json.build()
  }

  implicit val jsonToUser: FromJson[User] = {
    case json: JsonObject => User(json.getInt("id"), json.getString("name"), json.getBoolean("enabled", true))
    case json => throw new JsonException(s"""required OBJECT but found ${json.getValueType}""")
  }
}
