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

class JsonParserObjectSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "parse JSON object (includes hasNext test)" in withResource("/JsonParserTestObject.json") { in =>
    val parser = JsonParser(in)
    try testParser(parser, true)
    finally parser.close()
  }

  it should "parse JSON object (excludes hasNext test)" in withResource("/JsonParserTestObject.json") { in =>
    val parser = JsonParser(in)
    try testParser(parser, false)
    finally parser.close()
  }

  it should "get JSON object" in withResource("/JsonParserTestArray.json") { in =>
    import scala.language.implicitConversions

    val parser = JsonParser("""{ "a": 0, "b": 1, "c": [2, 3], "d": {"x": 4, "y": 5}, "e": [],"f":  {} }""")
    try
      assert(parser.next() == JsonParser.Event.StartObject)
      assert(parser.getObject() == Json.obj("a" -> 0, "b" -> 1, "c" -> Json.arr(2, 3), "d" -> Json.obj("x" -> 4, "y" -> 5), "e" -> Json.arr(), "f" -> Json.obj()))
    finally
      parser.close()
  }

  it should "not get JSON array" in withResource("/JsonParserTestArray.json") { in =>
    val parser = JsonParser("""{ "a": 0, "b": 1, "c": [2, 3], "d": {"x": 4, "y": 5}, "e": [],"f":  {} }""")
    try
      assert(parser.next() == JsonParser.Event.StartObject)
      assertThrows[IllegalStateException](parser.getArray())
    finally
      parser.close()
  }

  it should "get empty JSON object" in{
    val parser = JsonParser("{}")
    try
      assert(parser.next() == JsonParser.Event.StartObject)
      parser.getObject().isEmpty
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
    JsonParser.Event.StartObject,
    JsonParser.Event.Key("A"),
    JsonParser.Event.Value(JsonString("test")),
    JsonParser.Event.Key("B"),
    JsonParser.Event.Value(JsonString("")),
    JsonParser.Event.Key("C"),
    JsonParser.Event.Value(JsonString(" ")),
    JsonParser.Event.Key("D"),
    JsonParser.Event.Value(JsonString("a\tb\tc")),
    JsonParser.Event.Key("E"),
    JsonParser.Event.Value(JsonNumber(0)),
    JsonParser.Event.Key("F"),
    JsonParser.Event.Value(JsonNumber(0.0)),
    JsonParser.Event.Key("G"),
    JsonParser.Event.Value(JsonNumber(0.123)),
    JsonParser.Event.Key("H"),
    JsonParser.Event.Value(JsonNumber(123)),
    JsonParser.Event.Key("I"),
    JsonParser.Event.Value(JsonNumber(123.456)),
    JsonParser.Event.Key("J"),
    JsonParser.Event.Value(JsonNumber(45e12)),
    JsonParser.Event.Key("K"),
    JsonParser.Event.Value(JsonNumber(45e+12)),
    JsonParser.Event.Key("L"),
    JsonParser.Event.Value(JsonNumber(45e-12)),
    JsonParser.Event.Key("M"),
    JsonParser.Event.Value(JsonNumber(9876543210L)),
    JsonParser.Event.Key("N"),
    JsonParser.Event.Value(JsonBoolean(true)),
    JsonParser.Event.Key("O"),
    JsonParser.Event.Value(JsonBoolean(false)),
    JsonParser.Event.Key("P"),
    JsonParser.Event.Value(JsonNull),
    JsonParser.Event.Key("Q"),
    JsonParser.Event.StartObject,
    JsonParser.Event.EndObject,
    JsonParser.Event.Key("R"),
    JsonParser.Event.StartArray,
    JsonParser.Event.EndArray,
    JsonParser.Event.Key("S"),
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
    JsonParser.Event.Key("T"),
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
    JsonParser.Event.EndObject
  )

