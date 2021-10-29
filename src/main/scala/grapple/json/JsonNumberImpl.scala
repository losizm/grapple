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

private case class JsonNumberImpl(bigDecimalValue: BigDecimal) extends JsonNumber:
  if bigDecimalValue == null then
    throw NullPointerException()

  lazy val byteValue   = bigDecimalValue.toByteExact
  lazy val shortValue  = bigDecimalValue.toShortExact
  lazy val intValue    = bigDecimalValue.toIntExact
  lazy val longValue   = bigDecimalValue.toLongExact
  lazy val floatValue  = bigDecimalValue.floatValue
  lazy val doubleValue = bigDecimalValue.doubleValue
  lazy val bigIntValue = bigDecimalValue.toBigIntExact.getOrElse(throw ArithmeticException())

  override lazy val toString = bigDecimalValue.toString
