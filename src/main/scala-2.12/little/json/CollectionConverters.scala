/*
 * Copyright 2020 Carlos Conyers
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

import java.{ lang => jl }
import java.{ util => ju }

import scala.collection.convert.{ AsJavaConverters, AsScalaConverters }
import scala.collection.concurrent
import scala.collection.mutable

private object CollectionConverters extends AsScalaConverters with AsJavaConverters  {
  def asScala[A](list: ju.List[A]): mutable.Buffer[A] = asScalaBuffer(list)

  def asScala[A](set: ju.Set[A]): mutable.Set[A] = asScalaSet(set)

  def asScala[A](iter: ju.Iterator[A]): Iterator[A] = asScalaIterator(iter)

  def asScala[A](enum: ju.Enumeration[A]): Iterator[A] = enumerationAsScalaIterator(enum)

  def asScala[A](iter: jl.Iterable[A]): Iterable[A] = iterableAsScalaIterable(iter)

  def asScala[A](coll: ju.Collection[A]): Iterable[A] = collectionAsScalaIterable(coll)

  def asScala[K, V](map: ju.Map[K, V]): mutable.Map[K, V] = mapAsScalaMap(map)

  def asScala[K, V](dict: ju.Dictionary[K, V]): mutable.Map[K, V] = dictionaryAsScalaMap(dict)

  def asScala(props: ju.Properties): mutable.Map[String, String] = propertiesAsScalaMap(props)

  def asScala[K, V](map: ju.concurrent.ConcurrentMap[K, V]): concurrent.Map[K, V] = mapAsScalaConcurrentMap(map)

  def asJava[A](iter: Iterator[A]): ju.Iterator[A] = asJavaIterator(iter)

  def asJava[A](list: mutable.Buffer[A]): ju.List[A] = bufferAsJavaList(list)
}
