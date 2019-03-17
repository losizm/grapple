/*
 * Copyright 2018 Carlos Conyers
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

import java.math.{ BigDecimal => JBigDecimal, BigInteger => JBigInteger }

import javax.json.{ JsonNumber, JsonValue }

import scala.util.Try

private case class JsonNumberImpl(value: JBigDecimal) extends JsonNumber {
  val getValueType: JsonValue.ValueType = JsonValue.ValueType.NUMBER
  def intValue: Int = value.intValue
  def intValueExact: Int = value.intValueExact
  def longValue: Long = value.longValue
  def longValueExact: Long = value.longValueExact
  def doubleValue: Double = value.doubleValue
  def bigIntegerValue: JBigInteger = value.toBigInteger
  def bigIntegerValueExact: JBigInteger = value.toBigIntegerExact
  def bigDecimalValue: JBigDecimal = value
  lazy val isIntegral: Boolean = Try(bigIntegerValueExact).isSuccess
  override lazy val toString: String = value.toPlainString
}
