# little-json &ndash; Scala library for javax.json

**little-json** is a Scala library that provides extension methods (i.e., _type classes_)
to _javax.json_.

## Getting Started
---
To use **little-json**, start by adding it to your project:

* sbt
```scala
libraryDependencies += "losizm" %% "little-json" % "1.1.0"
```
* Gradle
```groovy
compile group: 'losizm', name: 'little-json_2.12', version: '1.1.0'
```
* Maven
```xml
<dependency>
  <groupId>losizm</groupId>
  <artifactId>little-json_2.12</artifactId>
  <version>1.1.0</version>
</dependency>
```

### Using an implementation of javax.json
**little-json** is compiled against version 1.1.2 of _javax.json_, and you must
add an implementation of _javax.json_ to your project.

So, for example, include the following to add the Glassfish reference
implementation as a dependency to your sbt build:

```scala
libraryDependencies += "org.glassfish" % "javax.json" % "1.1.2"
```

## A Little Taste of little-json
---
Here's a taste of what **little-json** offers:

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

// Parses text to JSON
val json = Json.parse("""
  [{ "id": 0, "name": "root" }, { "id": 500, "name": "guest" }]
""")

// Converts JSON array to User collection
val users = json.as[Seq[User]]

// Converts JSON object at index 0 to User
val user = json.get(0).as[User]

// Gets name of user at index 1
val name = (json \ 1 \ "name").as[String]

// Converts User collection back to JSON array
val otherJson = Json.toJson(users)
```

See the project's scaladoc for more information.

## License
---
**little-json** is licensed under the Apache license, version 2. See LICENSE
file for more information.
