package little.json

import javax.json.JsonValue

/**
 * Converts JsonValue to value of type T.
 *
 * @see [[ToJson]]
 */
trait FromJson[T] extends (JsonValue => T) {
  /** Converts json to T. */
  def apply(json: JsonValue): T
}
