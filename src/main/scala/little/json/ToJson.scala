package little.json

import javax.json.JsonValue
import javax.json.stream.JsonGenerator

/**
 * Converts value of type T to JsonValue.
 *
 * @see [[FromJson]]
 */
trait ToJson[T] extends ContextWriter[T] {
  /** Converts value to JsonValue. */
  def apply(value: T): JsonValue

  /** Converts value to JsonValue and writes it in array context. */
  def write(value: T)(implicit generator: JsonGenerator): JsonGenerator =
    generator.write(this(value))

  /** Converts value to JsonValue and writes it in object context. */
  def write(name: String, value: T)(implicit generator: JsonGenerator): JsonGenerator =
    generator.write(name, this(value))
}
