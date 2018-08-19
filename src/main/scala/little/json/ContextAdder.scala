package little.json

import javax.json.{ JsonArrayBuilder, JsonObjectBuilder }

/**
 * Adds value of type T in array context.
 *
 * @see [[ObjectContextAdder]]
 */
trait ArrayContextAdder[T] extends Any {
  /** Adds value to array context. */
  def add(value: T)(implicit builder: JsonArrayBuilder): JsonArrayBuilder
}

/**
 * Adds value of type T in object context.
 *
 * @see [[ArrayContextAdder]]
 */
trait ObjectContextAdder[T] extends Any {
  /** Adds value in object context. */
  def add(name: String, value: T)(implicit builder: JsonObjectBuilder): JsonObjectBuilder
}

/** Adds value of type T in requested context. */
trait ContextAdder[T] extends ArrayContextAdder[T] with ObjectContextAdder[T]
