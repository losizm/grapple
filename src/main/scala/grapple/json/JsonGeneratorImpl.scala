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

import java.io.{ BufferedWriter, Writer }

import scala.collection.mutable.Stack

private class JsonGeneratorImpl(output: Writer, printer: JsonPrinter) extends JsonGenerator:
  import JsonContext.*

  private val writer  = BufferedWriter(output, 8192)
  private var tracker = Stack[JsonContext]()

  private given BufferedWriter = writer

  def writeStartObject(name: String): this.type =
    writeStart(name, '{', ObjectContext(0))

  def writeStartArray(name: String): this.type =
    writeStart(name, '[', ArrayContext(0))

  def writeStartObject(): this.type =
    writeStart('{', ObjectContext(0))

  def writeStartArray(): this.type =
    writeStart('[', ArrayContext(0))

  def writeEnd(): this.type =
    if tracker.isEmpty then
      throw JsonGeneratorError("No context")

    tracker.pop() match
      case context: ObjectContext => printer.writeEnd('}', context.isEmpty, tracker.size)
      case context: ArrayContext  => printer.writeEnd(']', context.isEmpty, tracker.size)

    this

  def write(name: String, value: JsonValue): this.type =
    if name == null || value == null then
      throw NullPointerException()

    if tracker.isEmpty then
      throw JsonGeneratorError("No context")

    tracker.top match
      case context: ObjectContext =>
        if context.size > 0 then writer.write(',')
        printer.write(name, value, tracker.size)
        context.size += 1
        this

      case _  => throw JsonGeneratorError("Invalid context: array")

  def write(value: JsonValue): this.type =
    if value == null then
      throw NullPointerException()

    if tracker.isEmpty then
      throw JsonGeneratorError("No context")

    tracker.top match
      case context: ArrayContext =>
        if context.size > 0 then writer.write(',')
        printer.write(value, tracker.size)
        context.size += 1
        this

      case _  => throw JsonGeneratorError("Invalid context: object")

  def flush(): Unit = writer.flush()
  def close(): Unit = writer.close()

  private def writeStart(name: String, start: Char, newContext: JsonContext): this.type =
    if name == null then
      throw NullPointerException()

    if tracker.isEmpty then
      throw JsonGeneratorError("No context")

    tracker.top match
      case context: ObjectContext =>
        if context.size > 0 then writer.write(',')
        printer.writeStart(name, start, tracker.size)
        tracker.push(newContext)
        context.size += 1
        this

      case _  => throw JsonGeneratorError("Invalid context: array")

  private def writeStart(start: Char, newContext: JsonContext): this.type =
    if tracker.isEmpty then
      writer.write(start)
      tracker.push(newContext)
      this
    else
      tracker.top match
        case context: ArrayContext =>
          if context.size > 0 then writer.write(',')
          printer.writeStart(start, tracker.size)
          tracker.push(newContext)
          context.size += 1
          this

        case _  => throw JsonGeneratorError("Invalid context: object")
