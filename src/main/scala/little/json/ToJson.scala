package little.json

import javax.json.{ JsonArrayBuilder, JsonObjectBuilder, JsonValue }
import javax.json.stream.JsonGenerator

/**
 * Converts value of type T to JsonValue.
 *
 * @see [[FromJson]]
 */
trait ToJson[T] extends (T => JsonValue) with ContextAdder[T] with ContextWriter[T] {
  /** Converts value to JsonValue. */
  def apply(value: T): JsonValue

  /** Converts value to JsonValue and writes it in array context. */
  def add(value: T)(implicit builder: JsonArrayBuilder): JsonArrayBuilder =
    builder.add(apply(value))

  /** Converts value to JsonValue and writes it in object context. */
  def add(name: String, value: T)(implicit builder: JsonObjectBuilder): JsonObjectBuilder =
    builder.add(name, apply(value))

  /** Converts value to JsonValue and writes it in array context. */
  def write(value: T)(implicit generator: JsonGenerator): JsonGenerator =
    generator.write(apply(value))

  /** Converts value to JsonValue and writes it in object context. */
  def write(name: String, value: T)(implicit generator: JsonGenerator): JsonGenerator =
    generator.write(name, apply(value))
}
