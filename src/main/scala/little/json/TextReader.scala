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

import java.io.{ BufferedReader, EOFException, Reader }

private class TextReader(in: Reader, bufferSize: Int = 8192) extends BufferedReader(in, bufferSize):
  private var position = 0L
  private var marker   = 0L

  def getPosition(): Long = position

  def get(): Char =
    read() match
      case -1 => throw EOFException()
      case c  => position += 1; c.toChar

  @annotation.tailrec
  final def getSkipWhitespace(): Char =
    val c = get()

    Character.isWhitespace(c) match
      case false => c
      case true  => getSkipWhitespace()

  override def read(buf: Array[Char], off: Int, len: Int) =
    super.read(buf, off, len) match
      case -1 => -1
      case n  => position += n; n

  override def mark(limit: Int) =
    super.mark(limit)
    marker = position

  override def reset() =
    super.reset()
    position = marker
