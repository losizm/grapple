/*
 * Copyright 2021 Carlos Conyers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grapple.json

import java.io.InputStream

class JsonParserArraySpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "parse JSON array (includes hasNext test)" in withResource("/JsonParserTestArray.json") { in =>
    val parser = JsonParser(in)
    try testParser(parser, true)
    finally parser.close()
  }

  it should "parse JSON array (excludes hasNext test)" in withResource("/JsonParserTestArray.json") { in =>
    val parser = JsonParser(in)
    try testParser(parser, false)
    finally parser.close()
  }

  it should "get JSON array" in withResource("/JsonParserTestArray.json") { in =>
    import scala.language.implicitConversions

    val parser = JsonParser("""[0, 1, [2, 3], {"x": 4, "y": 5}, [], {}]""")
    try
      assert(parser.next() == JsonParser.Event.StartArray)
      assert(parser.getArray() == Json.arr(0, 1, Json.arr(2, 3), Json.obj("x" -> 4, "y" -> 5), Json.arr(), Json.obj()))
    finally
      parser.close()
  }

  it should "not get JSON object" in withResource("/JsonParserTestArray.json") { in =>
    val parser = JsonParser("""[0, 1, [2, 3], {"x": 4, "y": 5}, [], {}]""")
    try
      assert(parser.next() == JsonParser.Event.StartArray)
      assertThrows[IllegalStateException](parser.getObject())
    finally
      parser.close()
  }

  it should "get empty JSON array" in{
    val parser = JsonParser("[]")
    try
      assert(parser.next() == JsonParser.Event.StartArray)
      parser.getArray().isEmpty
    finally
      parser.close()
  }

  private def withResource[T](name: String)(f: InputStream => T): Unit =
    val resource = getClass.getResourceAsStream(name)
    try f(resource)
    finally resource.close()

  private def testParser(parser: JsonParser, testHasNext: Boolean): Unit =
    events.foreach { event =>
      if testHasNext then
        assert(parser.hasNext)

      assert(parser.next() == event)
    }

    if testHasNext then
      assert(!parser.hasNext)

    assertThrows[NoSuchElementException](parser.next())

  private val events = Seq(
    JsonParser.Event.StartArray,
    JsonParser.Event.Value(JsonString("test")),
    JsonParser.Event.Value(JsonString("")),
    JsonParser.Event.Value(JsonString(" ")),
    JsonParser.Event.Value(JsonString("a\tb\tc")),
    JsonParser.Event.Value(JsonNumber(0)),
    JsonParser.Event.Value(JsonNumber(0.0)),
    JsonParser.Event.Value(JsonNumber(0.123)),
    JsonParser.Event.Value(JsonNumber(123)),
    JsonParser.Event.Value(JsonNumber(123.456)),
    JsonParser.Event.Value(JsonNumber(45e12)),
    JsonParser.Event.Value(JsonNumber(45e+12)),
    JsonParser.Event.Value(JsonNumber(45e-12)),
    JsonParser.Event.Value(JsonNumber(9876543210L)),
    JsonParser.Event.Value(JsonBoolean(true)),
    JsonParser.Event.Value(JsonBoolean(false)),
    JsonParser.Event.Value(JsonNull),
    JsonParser.Event.StartObject,
    JsonParser.Event.EndObject,
    JsonParser.Event.StartArray,
    JsonParser.Event.EndArray,
    JsonParser.Event.StartObject,
    JsonParser.Event.Key("string1"),
    JsonParser.Event.Value(JsonString("test")),
    JsonParser.Event.Key("string2"),
    JsonParser.Event.Value(JsonString("")),
    JsonParser.Event.Key("string3"),
    JsonParser.Event.Value(JsonString(" ")),
    JsonParser.Event.Key("string4"),
    JsonParser.Event.Value(JsonString("a\tb\tc")),
    JsonParser.Event.Key("number1"),
    JsonParser.Event.Value(JsonNumber(0)),
    JsonParser.Event.Key("number2"),
    JsonParser.Event.Value(JsonNumber(0.0)),
    JsonParser.Event.Key("number3"),
    JsonParser.Event.Value(JsonNumber(0.123)),
    JsonParser.Event.Key("number4"),
    JsonParser.Event.Value(JsonNumber(123)),
    JsonParser.Event.Key("number5"),
    JsonParser.Event.Value(JsonNumber(123.456)),
    JsonParser.Event.Key("number6"),
    JsonParser.Event.Value(JsonNumber(45e12)),
    JsonParser.Event.Key("number7"),
    JsonParser.Event.Value(JsonNumber(45e+12)),
    JsonParser.Event.Key("number8"),
    JsonParser.Event.Value(JsonNumber(45e-12)),
    JsonParser.Event.Key("number9"),
    JsonParser.Event.Value(JsonNumber(9876543210L)),
    JsonParser.Event.Key("boolean1"),
    JsonParser.Event.Value(JsonBoolean(true)),
    JsonParser.Event.Key("boolean2"),
    JsonParser.Event.Value(JsonBoolean(false)),
    JsonParser.Event.Key("null"),
    JsonParser.Event.Value(JsonNull),
    JsonParser.Event.Key("object1"),
    JsonParser.Event.StartObject,
    JsonParser.Event.EndObject,
    JsonParser.Event.Key("object2"),
    JsonParser.Event.StartObject,
    JsonParser.Event.Key("a"),
    JsonParser.Event.Value(JsonString("One")),
    JsonParser.Event.Key("b"),
    JsonParser.Event.Value(JsonNumber(2)),
    JsonParser.Event.Key("c"),
    JsonParser.Event.Value(JsonBoolean(true)),
    JsonParser.Event.Key("d"),
    JsonParser.Event.Value(JsonNull),
    JsonParser.Event.EndObject,
    JsonParser.Event.Key("array1"),
    JsonParser.Event.StartArray,
    JsonParser.Event.EndArray,
    JsonParser.Event.Key("array2"),
    JsonParser.Event.StartArray,
    JsonParser.Event.Value(JsonString("One")),
    JsonParser.Event.Value(JsonNumber(2)),
    JsonParser.Event.Value(JsonBoolean(true)),
    JsonParser.Event.Value(JsonNull),
    JsonParser.Event.EndArray,
    JsonParser.Event.EndObject,
    JsonParser.Event.StartArray,
    JsonParser.Event.Value(JsonString("test")),
    JsonParser.Event.Value(JsonString("")),
    JsonParser.Event.Value(JsonString(" ")),
    JsonParser.Event.Value(JsonString("a\tb\tc")),
    JsonParser.Event.Value(JsonNumber(0)),
    JsonParser.Event.Value(JsonNumber(0.0)),
    JsonParser.Event.Value(JsonNumber(0.123)),
    JsonParser.Event.Value(JsonNumber(123)),
    JsonParser.Event.Value(JsonNumber(123.456)),
    JsonParser.Event.Value(JsonNumber(45e12)),
    JsonParser.Event.Value(JsonNumber(45e+12)),
    JsonParser.Event.Value(JsonNumber(45e-12)),
    JsonParser.Event.Value(JsonNumber(9876543210L)),
    JsonParser.Event.Value(JsonBoolean(true)),
    JsonParser.Event.Value(JsonBoolean(false)),
    JsonParser.Event.Value(JsonNull),
    JsonParser.Event.StartObject,
    JsonParser.Event.EndObject,
    JsonParser.Event.StartObject,
    JsonParser.Event.Key("a"),
    JsonParser.Event.Value(JsonString("One")),
    JsonParser.Event.Key("b"),
    JsonParser.Event.Value(JsonNumber(2)),
    JsonParser.Event.Key("c"),
    JsonParser.Event.Value(JsonBoolean(true)),
    JsonParser.Event.Key("d"),
    JsonParser.Event.Value(JsonNull),
    JsonParser.Event.EndObject,
    JsonParser.Event.StartArray,
    JsonParser.Event.EndArray,
    JsonParser.Event.StartArray,
    JsonParser.Event.Value(JsonString("One")),
    JsonParser.Event.Value(JsonNumber(2)),
    JsonParser.Event.Value(JsonBoolean(true)),
    JsonParser.Event.Value(JsonNull),
    JsonParser.Event.EndArray,
    JsonParser.Event.EndArray,
    JsonParser.Event.EndArray
  )

