package little.json

import javax.json.{ JsonException, JsonObject, JsonValue }

import scala.util.Success

object Test {
  case class User(id: Int, name: String, enabled: Boolean)

  implicit val UserToJson: ToJson[User] = { user =>
    val obj = Json.createObjectBuilder()
    obj.add("id", user.id)
    obj.add("name", user.name)
    obj.add("enabled", user.enabled)
    obj.build()
  }

  implicit val JsonToUser: FromJson[User] = {
    case obj: JsonObject => User(obj.getInt("id"), obj.getString("name"), obj.getBoolean("enabled"))
    case other => throw new JsonException(s"""required JsonObject / found ${other.getValueType}""")
  }
}
