# Grapple

[![Maven Central](https://img.shields.io/maven-central/v/com.github.losizm/grapple_3.svg?label=Maven%20Central)](https://central.sonatype.com/search?q=g:com.github.losizm%20a:grapple_3)

The JSON library for Scala.

## Getting Started
To get started, add **Grapple** to your project:

```scala
libraryDependencies += "com.github.losizm" %% "grapple" % "17.0.0"
```

## Let's Grapple!
**Grapple** is for reading and writing JSON content. It provides utilities for
mapping instances of your classes to JSON values.

### Usual Suspects
To model standard JSON values, the library includes the usual suspects:
`JsonObject`, `JsonArray`, `JsonString`, `JsonNumber`, `JsonBoolean`, and
`JsonNull`. However, when using implicit conversions, you're not required to
deal with these classes directly.

### Reading and Writing
Reading and writing are powered by `JsonInput` and `JsonOutput`. They convert
values to and from JSON, and library-provided implementations are included for
working with standard types like `String`, `Int`, etc. You must provide custom
implementations for converting to and from instances of your classes.

```scala
import scala.language.implicitConversions

import grapple.json.{ *, given }

case class User(id: Int, name: String)

// Define how to convert JsonValue to User
given JsonInput[User] with
  def read(json: JsonValue) = User(json("id"), json("name"))

val json = Json.parse("""{ "id": 1000, "name": "lupita" }""")

// Read JsonValue as User
val user = json.as[User]
assert { user.id == 1000 }
assert { user.name == "lupita" }

// Define how to convert User to JsonValue
given JsonOutput[User] with
  def write(u: User) = Json.obj("id" -> u.id, "name" -> u.name)

// Write User to JsonValue
val dupe = Json.toJson(user)
assert { dupe("id").as[Int] == 1000 }
assert { dupe("name").as[String] == "lupita" }
```

Special implementations are available for working with collections. For example,
if you define `JsonInput[User]`, you automatically get `JsonInput[Seq[User]]`.
The same applies to `JsonOutput[User]`, where `JsonOutput[Seq[User]]` can be
inferred.

```scala
val json = Json.parse("""[
  { "id": 0,    "name": "root"   },
  { "id": 1000, "name": "lupita" }
]""")

// Read JsonArray as Seq[User]
val users = json.as[Seq[User]]
assert { users(0) == User(0, "root") }
assert { users(1) == User(1000, "lupita")  }

// Or as other Iterables
val userList = json.as[List[User]]
val userIter = json.as[Iterator[User]]
val userSet  = json.as[Set[User]]

// Even as an Array
val userArray = json.as[Array[User]]

// Write Seq[User] to JsonArray
val jsonUsers = Json.toJson(users)
assert { jsonUsers(0) == Json.obj("id" -> 0, "name" -> "root") }
assert { jsonUsers(1) == Json.obj("id" -> 1000, "name" -> "lupita") }
```

### Extracting Values
You can traverse `JsonObject` and `JsonArray` to extract nested values. The `\`
extension method makes this clean and easy.

```scala
import scala.language.implicitConversions

import grapple.json.{ *, given }

case class User(id: Int, name: String)

// Define how to convert JsonValue to User
given JsonInput[User] =
  json => User(json("id"), json("name"))

val json = Json.parse("""{
  "node": {
    "name": "localhost",
    "users": [
      { "id": 0,    "name": "root"   },
      { "id": 1000, "name": "lupita" }
    ]
  }
}""")

// Get users array from node object
val users = (json \ "node" \ "users").as[Seq[User]]

// Get first user (at index 0) in users array
val user = (json \ "node" \ "users" \ 0).as[User]

// Get name of second user (at index 1) in users array
val name = (json \ "node" \ "users" \ 1 \ "name").as[String]
```

And, just as easy, you can do a recursive lookup with `\\` to collect field
values by name.

```scala
// Get all "name" values
val names = (json \\ "name").map(_.as[String])
assert { names == Seq("localhost", "root", "lupita") }
```

### Generating and Parsing
`JsonGenerator` and `JsonParser` are used for generating and parsing
potentially large JSON structures.

The generator incrementally writes JSON values to a stream instead of managing
the entire structure in memory.

```scala
import java.io.StringWriter

import scala.language.implicitConversions

import grapple.json.{ *, given }

val buf = StringWriter()
val out = JsonGenerator(buf)

try
  out.writeStartObject()          // start root object
  out.write("id", 1000)
  out.write("name", "lupita")
  out.writeStartArray("groups")   // start nested array
  out.write("lupita")
  out.write("admin")
  out.write("sudoer")
  out.writeEnd()                  // end nested array
  out.writeStartObject("info")    // start nested object
  out.write("home", "/home/lupita")
  out.write("storage", "8 GiB")
  out.writeEnd()                  // end nested object
  out.writeEnd()                  // end root object
  out.flush()

  val json = Json.parse(buf.toString)
  assert { json("id") == JsonNumber(1000) }
  assert { json("name") == JsonString("lupita") }
  assert { json("groups") == Json.arr("lupita", "admin", "sudoer") }
  assert { json("info") == Json.obj("home" -> "/home/lupita", "storage" -> "8 GiB") }
finally
  out.close()
```

The parser iterates events as it chews through data from an underlying stream.

```scala
import JsonParser.Event

val parser = JsonParser("""{ "id": 1000, "name": "lupita", "groups": ["lupita", "admin"] }""")

try
  // Get first event (start root object)
  assert { parser.next() == Event.StartObject }

  // Get key and value
  assert { parser.next() == Event.Key("id") }
  assert { parser.next() == Event.Value(1000) }

  // Get key and value
  assert { parser.next() == Event.Key("name") }
  assert { parser.next() == Event.Value("lupita") }

  // Get key and value
  assert { parser.next() == Event.Key("groups") }
  assert { parser.next() == Event.StartArray } // start nested array
  assert { parser.next() == Event.Value("lupita") }
  assert { parser.next() == Event.Value("admin") }
  assert { parser.next() == Event.EndArray }   // end nested array

  // Get final event (end root object)
  assert { parser.next() == Event.EndObject }

  // No more events
  assert { !parser.hasNext }
finally
  parser.close()

```

See also [JsonReader](https://losizm.github.io/grapple/latest/api/grapple/json/JsonReader.html)
and [JsonWriter](https://losizm.github.io/grapple/latest/api/grapple/json/JsonWriter.html).

## JSON-RPC 2.0 Specification
The library provides an API for [JSON-RPC 2.0 Specification](https://www.jsonrpc.org/specification),
which is a protocol for JSON-based remote procedure calls.

`JsonRpcRequest` and `JsonRpcResponse` model the RPC message types, and you can
incrementally build them with message builders.

```scala
import java.io.StringWriter

import scala.language.implicitConversions

import grapple.json.{ *, given }
import grapple.json.rpc.{ *, given }

case class Params(values: Int*):
  def sum = values.sum

// Define params converters
given JsonInput[Params]  = json   => Params(json.as[Seq[Int]]*)
given JsonOutput[Params] = params => Json.toJson(params.values)

// Create request with builder
val request = JsonRpcRequestBuilder()
  .version("2.0")
  .id("590d24ae-500a-486c-8d73-8035e78529bd")
  .method("sum")
  .params(Params(1, 2, 3))
  .toJsonRpcRequest()

// Create response with builder
val response = JsonRpcResponseBuilder()
  .version(request.version)
  .id(request.id)
  .resultOrError {
    request.method match
      case "sum" => request.params.get.as[Params].sum
      case name  => MethodNotFound(name)
  }.toJsonRpcResponse()
```

And you can parse them using the library-provided magic.

```scala
val request = Json.parse("""{
  "jsonrpc": "2.0",
  "id":      "590d24ae-500a-486c-8d73-8035e78529bd",
  "method":  "sum",
  "params":  [1, 2, 3]
}""").as[JsonRpcRequest]

assert { request.version == "2.0" }
assert { request.id.string == "590d24ae-500a-486c-8d73-8035e78529bd" }
assert { request.method == "sum" }
assert { request.params.exists(_.as[Params] == Params(1, 2, 3)) }

val response = Json.parse("""{
  "jsonrpc": "2.0",
  "id":      "590d24ae-500a-486c-8d73-8035e78529bd",
  "result":  6
}""").as[JsonRpcResponse]

assert { response.version == "2.0" }
assert { response.id.string == "590d24ae-500a-486c-8d73-8035e78529bd" }
assert { response.result.as[Int] == 6 }
```

## API Documentation
See [scaladoc](https://losizm.github.io/grapple/latest/api/index.html)
for additional details.

## License
**Grapple** is licensed under the Apache License, Version 2. See [LICENSE](LICENSE)
for more information.
