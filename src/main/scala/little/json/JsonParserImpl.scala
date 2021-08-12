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
package little.json

import java.io.Reader
import java.lang.Character.isDigit

import scala.collection.mutable.Stack
import scala.util.Try

private class JsonParserImpl(input: Reader) extends JsonParser:
  import JsonParser.Event
  import JsonContext.*

  private enum State:
    case Init extends State
    case Reset extends State
    case Next extends State
    case Done extends State

  private val hexDigits    = "0123456789ABCDEFabcdef"
  private val reader       = TextReader(input)
  private var state        = State.Init
  private var event        = null : Try[Event]
  private var tracker      = Stack[JsonContext]()
  private var fieldPending = false

  def hasNext: Boolean =
    state match
      case State.Init  => init(); hasNext
      case State.Reset => reset(); hasNext
      case State.Next  => true
      case State.Done  => false

  def next(): JsonParser.Event =
    if !hasNext then
      throw new NoSuchElementException()

    state = State.Reset;
    val success = event.get
    fieldPending = success.isInstanceOf[Event.FieldName]
    success

  def getObject(): JsonObject =
    if tracker.isEmpty || !tracker.top.isObject || fieldPending then
      throw IllegalStateException()

    val builder = JsonObjectBuilder()
    var done    = false

    while !done do
      next() match
        case Event.EndObject        => done = true
        case Event.FieldName(name)  =>
          next() match
            case Event.StartObject  => builder.add(name, getObject())
            case Event.StartArray   => builder.add(name, getArray())
            case Event.Value(value) => builder.add(name, value)
            case _                  => throw JsonException("Unexpected event")
        case _                      => throw JsonException("Unexpected event")
    builder.build()

  def getArray(): JsonArray =
    if tracker.isEmpty || !tracker.top.isArray then
      throw IllegalStateException()

    val builder = JsonArrayBuilder()
    var done    = false

    while !done do
      next() match
        case Event.EndArray     => done = true
        case Event.StartObject  => builder.add(getObject())
        case Event.StartArray   => builder.add(getArray())
        case Event.Value(value) => builder.add(value)
        case _                  => throw JsonException("Unexpected event")
    builder.build()

  def close(): Unit =
    state = State.Done
    reader.close()

  private def init(): Unit =
    event = Try(firstEvent())
    state = State.Next

  private def reset(): Unit =
    if tracker.isEmpty then
      state = State.Done
    else
      event = Try(nextEvent())
      state = State.Next

  private def firstEvent(): Event =
    reader.getSkipWhitespace() match
      case '{' =>
        tracker.push(ObjectContext(0))
        Event.StartObject

      case '[' =>
        tracker.push(ArrayContext(0))
        Event.StartArray

      case c   => unexpectedChar(c)

  private def nextEvent(): Event =
    if fieldPending then
      reader.getSkipWhitespace() match
        case ':' => getValueEvent(reader.getSkipWhitespace())
        case c   => unexpectedChar(c)
    else
      reader.getSkipWhitespace() match
        case '}' =>
          if !tracker.top.isObject then
            unexpectedChar('}')
          tracker.pop()
          Event.EndObject

        case ']' =>
          if !tracker.top.isArray then
            unexpectedChar(']')
          tracker.pop()
          Event.EndArray

        case ',' =>
          if tracker.top.isEmpty then
            unexpectedChar(',')

          tracker.top.isObject match
            case true  => getFieldNameEvent(reader.getSkipWhitespace())
            case false => getValueEvent(reader.getSkipWhitespace())

        case c =>
          if !tracker.top.isEmpty then
            unexpectedChar(c)

          tracker.top.isObject match
            case true  => getFieldNameEvent(c)
            case false => getValueEvent(c)

  private def getFieldNameEvent(first: Char): Event =
    first match
      case '"' => Event.FieldName(finishString())
      case _   => unexpectedChar(first)

  private def getValueEvent(first: Char): Event =
    first match
      case '{' =>
        tracker.top.size += 1
        tracker.push(ObjectContext(0))
        Event.StartObject

      case '[' =>
        tracker.top.size += 1
        tracker.push(ArrayContext(0))
        Event.StartArray

      case '"' =>
        val value = JsonString(finishString())
        tracker.top.size += 1
        Event.Value(value)

      case 't' =>
        val value = finishTrue()
        tracker.top.size += 1
        Event.Value(value)

      case 'f' =>
        val value = finishFalse()
        tracker.top.size += 1
        Event.Value(value)

      case 'n' =>
        val value = finishNull()
        tracker.top.size += 1
        Event.Value(value)

      case '-' =>
        val value = finishNumber('-')
        tracker.top.size += 1
        Event.Value(value)

      case c if isDigit(c) =>
        val value = finishNumber(c)
        tracker.top.size += 1
        Event.Value(value)

      case _ => unexpectedChar(first)

  private def finishString(): String =
    val word = StringBuilder()
    var done = false

    while !done do
      reader.get() match
        case '\"' => done = true
        case '\\' =>
          reader.get() match
            case '\\' => word += '\\'
            case '\"' => word += '\"'
            case '/'  => word += '/'
            case 'b'  => word += '\b'
            case 'f'  => word += '\f'
            case 'n'  => word += '\n'
            case 'r'  => word += '\r'
            case 't'  => word += '\t'
            case 'u'  => word += getUnicodeChar()
            case c    => unexpectedChar(c)

        case char => word += char

    word.toString

  private def finishTrue(): JsonBoolean =
    getExpectedChar('r')
    getExpectedChar('u')
    getExpectedChar('e')
    JsonTrue

  private def finishFalse(): JsonBoolean =
    getExpectedChar('a')
    getExpectedChar('l')
    getExpectedChar('s')
    getExpectedChar('e')
    JsonFalse

  private def finishNull(): JsonNull =
    getExpectedChar('u')
    getExpectedChar('l')
    getExpectedChar('l')
    JsonNull

  private def finishNumber(first: Char): JsonNumber =
    val number = StringBuilder() += first
    var dot    = false
    var e      = false
    var eSign  = false
    var done   = false

    if first == '-' then
      reader.get() match
        case c if isDigit(c) => number += c
        case c               => unexpectedChar(c)

    while !done do
      { reader.mark(8); reader.read() } match
        case c if isDigit(c) => number += c.toChar
        case '.'             =>
          if dot || e then unexpectedChar('.')
          number += '.'
          dot = true

        case c @ ('E' | 'e') =>
          if e then unexpectedChar(c)
          number += 'E'
          e = true

        case c @ ('-' | '+') =>
          if eSign || !e then unexpectedChar(c)
          number += c.toChar
          eSign = true

        case -1              => done = true;
        case _               => done = true; reader.reset()

    JsonNumber(number.toString)

  private def getUnicodeChar(): Char =
    val digits = new Array[Char](4)
    for i <- 0 to 3 do
      digits(i) = checkHexDigit(reader.get())
    Integer.parseInt(String(digits), 16).toChar

  private def checkHexDigit(c: Char): Char =
    hexDigits.contains(c) match
      case true  => c
      case false => unexpectedChar(c)

  private def getExpectedChar(c: Char): Char =
    reader.get() match
      case `c` => c
      case c   => unexpectedChar(c)

  private def unexpectedChar(c: Char): Nothing =
    throw JsonParserError(
      s"Unexpected character ${EncodedString(c)} at offset ${reader.getPosition() - 1}",
      reader.getPosition() - 1
    )
