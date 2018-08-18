package little.json

import javax.json.stream.JsonGenerator

/**
 * Writes value of type T in array context.
 *
 * @see [[ObjectContextWriter]]
 */
trait ArrayContextWriter[T] extends Any {
  /** Writes value in array context. */
  def write(value: T)(implicit generator: JsonGenerator): JsonGenerator
}

/**
 * Writes value of type T in object context.
 *
 * @see [[ArrayContextWriter]]
 */
trait ObjectContextWriter[T] extends Any {
  /** Writes value in object context. */
  def write(name: String, value: T)(implicit generator: JsonGenerator): JsonGenerator
}

/** Writes value of type T in requested context. */
trait ContextWriter[T] extends ArrayContextWriter[T] with ObjectContextWriter[T]
