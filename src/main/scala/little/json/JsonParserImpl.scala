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
  import JsonContext._

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

      case _ => unexpectedCharacter()

  private def nextEvent(): Event =
    if fieldPending then
      reader.getSkipWhitespace() match
        case ':' => getValueEvent(reader.getSkipWhitespace())
        case _   => unexpectedCharacter()
    else
      reader.getSkipWhitespace() match
        case '}' =>
          if !tracker.top.isInstanceOf[ObjectContext] then
            unexpectedCharacter()
          tracker.pop()
          Event.EndObject

        case ']' =>
          if !tracker.top.isInstanceOf[ArrayContext] then
            unexpectedCharacter()
          tracker.pop()
          Event.EndArray

        case ',' =>
          if tracker.top.isEmpty then
            unexpectedCharacter()

          tracker.top.isInstanceOf[ObjectContext] match
            case true  => getFieldNameEvent(reader.getSkipWhitespace())
            case false => getValueEvent(reader.getSkipWhitespace())

        case c =>
          if !tracker.top.isEmpty then
            unexpectedCharacter()

          tracker.top.isInstanceOf[ObjectContext] match
            case true  => getFieldNameEvent(c)
            case false => getValueEvent(c)

  private def getFieldNameEvent(first: Char): Event =
    first match
      case '"' => Event.FieldName(finishString())
      case _   => unexpectedCharacter()

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

      case _ => unexpectedCharacter()

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
            case 'u'  => word += getUnicodeCharacter()
            case _    => unexpectedCharacter()

        case char => word += char

    word.toString

  private def finishTrue(): JsonBoolean =
    if reader.get() != 'r' || reader.get() != 'u' || reader.get() != 'e' then
      unexpectedCharacter()
    JsonTrue

  private def finishFalse(): JsonBoolean =
    if reader.get() != 'a' || reader.get() != 'l' || reader.get() != 's' || reader.get() != 'e' then
      unexpectedCharacter()
    JsonFalse

  private def finishNull(): JsonNull =
    if reader.get() != 'u' || reader.get() != 'l' || reader.get() != 'l' then
      unexpectedCharacter()
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
        case _               => unexpectedCharacter()

    while !done do
      { reader.mark(8); reader.read() } match
        case c if isDigit(c) => number += c.toChar
        case '.'             =>
          if dot || e then unexpectedCharacter()
          number += '.'
          dot = true

        case 'E' | 'e'       =>
          if e then unexpectedCharacter()
          number += 'E'
          e = true

        case c @ ('-' | '+') =>
          if eSign || !e then unexpectedCharacter()
          number += c.toChar
          eSign = true

        case -1              => done = true;
        case _               => done = true; reader.reset()

    JsonNumber(number.toString)

  private def getUnicodeCharacter(): Char =
    val digits = new Array[Char](4)
    for i <- 0 to 3 do
      digits(i) = checkHexDigit(reader.get())
    Integer.parseInt(String(digits), 16).toChar

  private def checkHexDigit(c: Char): Char =
    hexDigits.contains(c) match
      case true  => c
      case false => unexpectedCharacter()

  private def unexpectedCharacter(): Nothing =
    throw JsonException(s"Unexpected char at offset=${reader.getPosition() - 1}")
