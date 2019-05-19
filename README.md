# little-json

The Scala library that provides extension methods to _javax.json_.

[![Maven Central](https://img.shields.io/maven-central/v/com.github.losizm/little-json_2.12.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.losizm%22%20AND%20a:%22little-json_2.12%22)

## Getting Started
To use **little-json**, start by adding it to your project:

* sbt
```scala
libraryDependencies += "com.github.losizm" %% "little-json" % "2.6.1"
```
* Gradle
```groovy
compile group: 'com.github.losizm', name: 'little-json_2.12', version: '2.6.1'
```
* Maven
```xml
<dependency>
  <groupId>com.github.losizm</groupId>
  <artifactId>little-json_2.12</artifactId>
  <version>2.6.1</version>
</dependency>
```

### Using Implementation of javax.json
**little-json** has a runtime dependency to _javax.json 1.1.4_, and you must add
an implementation to your project.

So, for example, include the following in your sbt build to add the
Glassfish reference implementation as a dependency:

```scala
libraryDependencies += "org.glassfish" % "javax.json" % "1.1.4"
```

## A Taste of little-json
Here's a taste of what **little-json** offers.

### Converting to and from JSON

**little-json** is powered by a pair of traits, `ToJson` and `FromJson`.  You
provide implementations of these to convert your objects to and from JSON.

```scala
import javax.json.JsonObject
import little.json.{ Json, FromJson, ToJson }
import little.json.Implicits._ // Unleash the power

case class User(id: Int, name: String)

// Define how to convert User to JsonObject
implicit val userToJson: ToJson[User] = {
  case User(id, name) => Json.obj("id" -> id, "name" -> name)
}

// Define how to convert JsonObject to User
implicit val userFromJson: FromJson[User] = {
  case json: JsonObject => User(json.getInt("id"), json.getString("name"))
  case json => throw new IllegalArgumentException("JsonObject required")
}

// Parse String to JsonValue
val json = Json.parse("""{ "id": 0, "name": "root" }""")

// Convert JsonValue to User
val user = json.as[User]

// Convert User back to JsonValue
val jsonToo = Json.toJson(user)
```

A special implementation of `ToJson` is available for converting a collection of
objects to a `JsonArray`. So, for example, if you define `ToJson[User]`, you
automagically get `ToJson[Seq[User]]`.

The same applies to `FromJson[User]`. You get `FromJson[Seq[User]]` for free.

```scala
val json = Json.parse("""
  [{ "id": 0, "name": "root" }, { "id": 500, "name": "guest" }]
""")

// Convert JsonArray to Seq[User]
val users = json.as[Seq[User]]

// In fact, any Traversable will do
val listOfUsers = json.as[List[User]]
val iterOfUsers = json.as[Iterator[User]]
val setOfUsers = json.as[Set[User]]

// Or even an Array
val arrayOfUsers = json.as[Array[User]]

// Convert Seq[User] back to JsonArray
val dupe = Json.toJson(users)
```

### Extracting Values from JSON Structure

You can navigate your way through a `JsonArray` or `JsonObject` to extract
values deep inside its structure.

```scala
val json = Json.parse("""
{
  "computer": {
    "name": "localhost",
    "users": [
      { "id": 0, "name": "root" }, { "id": 500, "name": "guest" }
    ]
  }
}
""")

// Get users array from computer object
val users = (json \ "computer" \ "users").as[Seq[User]]

// Get first user (at index 0) in users array
val user = (json \ "computer" \ "users" \ 0).as[User]

// Get name of second user (at index 1) in users array
val name = (json \ "computer" \ "users" \ 1 \ "name").as[String]

// Use long form
val alias = json.get("computer").get("users").get(1).get("name").as[String]
```

You can also do a recursive lookup.

```scala
// Get all "name" values
val names = (json \\ "name") // Seq[JsonValue]("localhost", "root", "guest")
```

Note, in above example, computer name (_localhost_) and user names (_root_ and
_guest_) are included in the result.

### Streaming JSON

`JsonGenerator` and `JsonParser` are defined in `javax.json.stream` for
generating and parsing potentially large JSON structures. JSON is written to and
read from streams, instead of entire structure being managed in memory.

**little-json** gives these classes a bit more power, making it easier for you
to read and write your objects in JSON.

Here's the enhanced generator in action.

```scala
import java.io.StringWriter

val users = Seq(User(0, "root"), User(500, "guest"))

val writer = new StringWriter()
val generator = Json.createGenerator(writer)

generator.writeStartObject()

// Write array of users one user at a time
// Implicitly convert each user to JsonObject before writing
generator.writeStartArray("one-by-one")
users.foreach { user => generator.write(user) }
generator.writeEnd()

// Write array of users in one swoop
// Implicitly convert users to JsonArray before writing
generator.write("all-at-once", users)

generator.writeEnd()
generator.close()
```

And the enhanced parser in action.

```scala
import java.io.StringReader
import javax.json.stream.JsonParser.{ Event => ParserEvent }

val reader = new StringReader(writer.toString)
val parser = Json.createParser(reader)

// Pop events to get to first array
assert(parser.next() == ParserEvent.START_OBJECT)
assert(parser.next() == ParserEvent.KEY_NAME)
assert(parser.getString() == "one-by-one")
assert(parser.next() == ParserEvent.START_ARRAY)

// Get both users one by one (little-json adds JsonParser.nextObject())
val root = parser.nextObject().as[User]
val guest = parser.nextObject().as[User]

// Pop events to get to second array
assert(parser.next() == ParserEvent.END_ARRAY)
assert(parser.next() == ParserEvent.KEY_NAME)
assert(parser.getString() == "all-at-once")

// Get both users all at once (little-json adds JsonParser.nextArray())
val users = parser.nextArray().as[Seq[User]]

assert(parser.next() == ParserEvent.END_OBJECT)
parser.close()
```

## API Documentation

See [scaladoc](https://losizm.github.io/little-json/latest/api/little/json/index.html)
for additional details.

## License
**little-json** is licensed under the Apache License, Version 2. See LICENSE
file for more information.
