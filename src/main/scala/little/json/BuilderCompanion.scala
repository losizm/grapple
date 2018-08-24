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

import javax.json.{ JsonArrayBuilder, JsonObjectBuilder }

/**
 * Adds value of type T to array builder.
 *
 * @see [[ObjectBuilderCompanion]]
 */
trait ArrayBuilderCompanion[T] extends Any {
  /** Adds value to array builder. */
  def add(value: T)(implicit builder: JsonArrayBuilder): JsonArrayBuilder
}

/**
 * Adds value of type T to object builder.
 *
 * @see [[ArrayBuilderCompanion]]
 */
trait ObjectBuilderCompanion[T] extends Any {
  /** Adds value to object builder. */
  def add(name: String, value: T)(implicit builder: JsonObjectBuilder): JsonObjectBuilder
}

/** Adds value of type T to requested builder. */
trait BuilderCompanion[T] extends ArrayBuilderCompanion[T] with ObjectBuilderCompanion[T]
