# little-json &ndash; Scala library for javax.json

**little-json** is a Scala library that provides extension methods to `javax.json`.

Here's a taste of what the library offers.

```scala
import javax.json.{ JsonArray, JsonObject, JsonException }
import little.json.{ Json, FromJson, ToJson }
import little.json.Implicits._

case class User(id: Int, name: String)

// Converts User to JsonObject
implicit val userToJson: ToJson[User] = { user =>
  Json.createObjectBuilder()
    .add("id", user.id)
    .add("name", user.name)
    .build()
}

// Converts JsonObject to User
implicit val jsonToUser: FromJson[User] = {
  case json: JsonObject => User(json.getInt("id"), json.getString("name"))
  case json => throw new JsonException(s"Expected a JSON object")
}

// Parses text to JSON array
val json = Json.parse[JsonArray]("""
  [{ "id": 0, "name": "root" }, { "id": 500, "name": "guest" }]
""")

// Converts JSON array to User collection
val users = json.as[Seq[User]]

// Converts JSON object at index 0 to User
val user = json.get(0).as[User]

// Gets name of user at index 1
val name = (json \ 1 \ "name").as[String]

// Converts users back to JSON array
val otherJson = Json.toJson(users)
```
