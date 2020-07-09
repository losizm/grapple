# little-json

The Scala library that provides extension methods to _javax.json_.

[![Maven Central](https://img.shields.io/maven-central/v/com.github.losizm/little-json_2.12.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.losizm%22%20AND%20a:%22little-json_2.12%22)

## Getting Started
To use **little-json**, start by adding it to your project:

```scala
libraryDependencies += "com.github.losizm" %% "little-json" % "3.3.0"
```

### Using Implementation of javax.json
**little-json** has a runtime dependency to _javax.json 1.1.x_, and you must add
an implementation to your project.

So, for example, include the following in your build to add the
Glassfish reference implementation as a dependency:

```scala
libraryDependencies += "org.glassfish" % "javax.json" % "1.1.4"
```

## A Taste of little-json
Here's a taste of what **little-json** offers.

### Reading and Writing JSON

**little-json** is powered by a pair of traits, `JsonInput` and `JsonOutput`. You
provide implementations of these to read and write JSON values.

```scala
import javax.json.{ JsonObject, JsonValue }
import little.json.{ Json, JsonInput, JsonOutput }
import little.json.Implicits._ // Unleash the power

case class User(id: Int, name: String)

// Define how to read User from JsonValue
implicit val userJsonInput: JsonInput[User] = {
  case json: JsonObject => User(json.getInt("id"), json.getString("name"))
  case json: JsonValue  => throw new IllegalArgumentException("JsonObject required")
}

// Define how to write User to JsonValue
implicit val userJsonOutput: JsonOutput[User] = {
  case User(id, name) => Json.obj("id" -> id, "name" -> name)
}

// Parse String to JsonValue
val json = Json.parse("""{ "id": 0, "name": "root" }""")

// Read User from JsonValue
val user = json.as[User]

// Write User to JsonValue
val jsonUser = Json.toJson(user)
```

A special implementation of `JsonOutput` is available for writing a collection of
objects to a `JsonArray`. So, for example, if you define `JsonOutput[User]`, you
automagically get `JsonOutput[Seq[User]]`.

The same applies to `JsonInput[User]`. You get `JsonInput[Seq[User]]` for free.

```scala
val json = Json.parse("""
  [{ "id": 0, "name": "root" }, { "id": 500, "name": "guest" }]
""")

// Read Seq[User] from JsonArray
val users = json.as[Seq[User]]

// In fact, any Traversable will do
val userList = json.as[List[User]]
val userIter = json.as[Iterator[User]]
val userSet  = json.as[Set[User]]

// Or even Array
val userArray = json.as[Array[User]]

// Write Seq[User] to JsonArray
val jsonUsers = Json.toJson(users)
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

## Working with JSON-RPC 2.0

As a bonus, an API is defined for working with [JSON-RPC 2.0](https://www.jsonrpc.org/specification).

You can build requests and response with Scala objects

```scala
import little.json.{ Json, JsonOutput }
import little.json.Implicits._
import little.json.rpc._

case class Problem(values: Int*)
case class Answer(value: Int)

// Used when creating "params" in request
implicit val problemOutput: JsonOutput[Problem] = {
  problem => Json.toJson(problem.values)
}

// Used when creating "result" in response
implicit val answerOutput: JsonOutput[Answer] = {
  answer => Json.obj("answer" -> answer.value)
}

val request = JsonRpcRequest(
  version = "2.0",
  id = "590d24ae-500a-486c-8d73-8035e78529bd",
  method = "sum",
  params = Problem(1, 2, 3) // Uses problemOutput
)

val response = JsonRpcResponse(
  version = request.version,
  id = request.id,
  result = request.method match {
    case "sum" =>
      // Sets result
      request.params
        .map(_.as[Array[Int]])
        .map(_.sum)
        .map(Answer(_))
        .map(JsonRpcResult(_)) // Uses answerOutput
        .get
    case name =>
      // Or sets error
      JsonRpcResult(MethodNotFound(name))
  }
)
```

And you can parse them from JSON text.

```scala
import javax.json.{ JsonArray, JsonObject }

import little.json.{ Json, JsonInput }
import little.json.Implicits._
import little.json.rpc._

case class Problem(values: Int*)
case class Answer(value: Int)

implicit val problemInput: JsonInput[Problem] = {
  case arr: JsonArray => Problem(arr.as[Array[Int]].toSeq : _*)
}

implicit val answerInput: JsonInput[Answer] = {
  case obj: JsonObject => Answer(obj.getInt("answer"))
}

val request = JsonRpc.parseRequest("""
  {
    "jsonrpc": "2.0",
    "id": "590d24ae-500a-486c-8d73-8035e78529bd",
    "method": "sum",
    "params": [1, 2, 3]
  }
""")

assert(request.version == "2.0")
assert(request.id.stringValue == "590d24ae-500a-486c-8d73-8035e78529bd")
assert(request.method == "sum")
assert(request.params.exists(_.as[Problem] == Problem(1, 2, 3)))

val response = JsonRpc.parseResponse("""
  {
    "jsonrpc": "2.0",
    "id": "590d24ae-500a-486c-8d73-8035e78529bd",
    "result": { "answer": 6 }
  }
""")

assert(response.version == "2.0")
assert(response.id.stringValue == "590d24ae-500a-486c-8d73-8035e78529bd")
assert(response.result.get.as[Answer] == Answer(6))
```

## API Documentation

See [scaladoc](https://losizm.github.io/little-json/latest/api/little/json/index.html)
for additional details.

## License
**little-json** is licensed under the Apache License, Version 2. See LICENSE
file for more information.
