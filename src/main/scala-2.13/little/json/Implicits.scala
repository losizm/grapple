/*
 * Copyright 2019 Carlos Conyers
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

import javax.json._
import javax.json.stream.{ JsonGenerator, JsonParser }

import scala.collection.{ Factory, IterableOnce }
import scala.jdk.CollectionConverters.IterableHasAsScala
import scala.language.{ higherKinds, implicitConversions }
import scala.util.Try

import little.json.Json

/**
 * Provides type classes for `javax.json` and implicit implementations of
 * `ToJson` and `FromJson`.
 */
object Implicits {
  /** Converts JsonValue to String. */
  implicit val stringFromJson: FromJson[String] = {
    case json: JsonString => json.getString
    case json => throw new IllegalArgumentException(s"STRING required but found ${json.getValueType}")
  }

  /** Converts JsonValue to Boolean. */
  implicit val booleanFromJson: FromJson[Boolean] = {
    case JsonValue.TRUE => true
    case JsonValue.FALSE => false
    case json => throw new IllegalArgumentException(s"TRUE or FALSE required but found ${json.getValueType}")
  }

  /** Converts JsonValue to Int (exact). */
  implicit val intFromJson: FromJson[Int] = {
    case json: JsonNumber => json.intValueExact
    case json => throw new IllegalArgumentException(s"NUMBER required but found ${json.getValueType}")
  }

  /** Converts JsonValue to Long (exact). */
  implicit val longFromJson: FromJson[Long] = {
    case json: JsonNumber => json.longValueExact
    case json => throw new IllegalArgumentException(s"NUMBER required but found ${json.getValueType}")
  }

  /** Converts JsonValue to Double. */
  implicit val doubleFromJson: FromJson[Double] = {
    case json: JsonNumber => json.doubleValue
    case json => throw new IllegalArgumentException(s"NUMBER required but found ${json.getValueType}")
  }

  /** Converts JsonValue to BigInt (exact). */
  implicit val bigIntFromJson: FromJson[BigInt] = {
    case json: JsonNumber => json.bigIntegerValueExact
    case json => throw new IllegalArgumentException(s"NUMBER required but found ${json.getValueType}")
  }

  /** Converts JsonValue to BigDecimal. */
  implicit val bigDecimalFromJson: FromJson[BigDecimal] = {
    case json: JsonNumber => json.bigDecimalValue
    case json => throw new IllegalArgumentException(s"NUMBER required but found ${json.getValueType}")
  }

  /**
   * Creates FromJson for converting JsonValue to Option.
   *
   * The instance of `FromJson` returns `Some` if the value is successfully
   * converted, and `None` if the value is JSON null (i.e., `JsonValue.NULL`);
   * otherwise it throws the exception thrown by the implicit `convert`.
   *
   * <strong>Note:</strong> This behavior is different from
   * [[JsonValueType.asOption]], which returns `Some` if the value is
   * successfully converted and returns `None` if the value is JSON null or if
   * an exception was thrown during conversion.
   */
  implicit def optionFromJson[T](implicit convert: FromJson[T]) =
    new FromJson[Option[T]] {
      def apply(json: JsonValue): Option[T] =
        if (json == JsonValue.NULL)
          None
        else Some(convert(json))
    }

  /**
   * Creates FromJson for converting JsonValue to Either.
   *
   * An attempt is made to first convert the value to Right value. If that
   * attempt fails, then an attempt is made to convert it to Left value.
   */
  implicit def eitherFromJson[A, B](implicit left: FromJson[A], right: FromJson[B]) =
    new FromJson[Either[A, B]] {
      def apply(json: JsonValue): Either[A, B] = {
        val result = Try(right(json))
        Either.cond(result.isSuccess, result.get, left(json))
      }
    }

  /** Creates FromJson for converting JsonValue to Try. */
  implicit def tryFromJson[T](implicit convert: FromJson[T]) =
    new FromJson[Try[T]] {
      def apply(json: JsonValue): Try[T] = Try(convert(json))
    }

  /** Creates FromJson for converting JsonArray to collection. */
  implicit def collectionFromJson[A, M[A]](implicit convert: FromJson[A], factory: Factory[A, M[A]]) =
    new FromJson[M[A]] {
      def apply(json: JsonValue): M[A] =
        if (json.isInstanceOf[JsonArray]) json.asArray.asScala.map(convert).to(factory)
        else throw new IllegalArgumentException(s"ARRAY required but found ${json.getValueType}")
    }

  /** Converts String to JsonValue. */
  implicit val stringToJson: ToJson[String] = (value) => new JsonStringImpl(value)

  /** Converts Boolean to JsonValue. */
  implicit val booleanToJson: ToJson[Boolean] = (value) => if (value) JsonValue.TRUE else JsonValue.FALSE

  /** Converts Int to JsonValue. */
  implicit val intToJson: ToJson[Int] = (value) => JsonNumberImpl(new java.math.BigDecimal(value))

  /** Converts Long to JsonValue. */
  implicit val longToJson: ToJson[Long] = (value) => JsonNumberImpl(new java.math.BigDecimal(value))

  /** Converts Double to JsonValue. */
  implicit val doubleToJson: ToJson[Double] = (value) => JsonNumberImpl(new java.math.BigDecimal(value))

  /** Converts BigInt to JsonValue. */
  implicit val bigIntToJson: ToJson[BigInt] = (value) => JsonNumberImpl(new java.math.BigDecimal(value.bigInteger))

  /** Converts BigDecimal to JsonValue. */
  implicit val bigDecimalToJson: ToJson[BigDecimal] = (value) => JsonNumberImpl(value.bigDecimal)

  /** Creates ToJson for converting Option to JsonValue. */
  implicit def optionToJson[A, M[A] <: Option[A]](implicit convert: ToJson[A]) =
    new ToJson[M[A]] {
      def apply(value: M[A]): JsonValue =
        value.fold(JsonValue.NULL)(convert)
    }

  /** Creates ToJson for converting Either to JsonValue. */
  implicit def eitherToJson[A, B, M[A, B] <: Either[A, B]](implicit left: ToJson[A], right: ToJson[B]) =
    new ToJson[M[A, B]] {
      def apply(value: M[A, B]): JsonValue = value.fold(left, right)
    }

  /** Creates ToJson for converting Try to JsonValue. */
  implicit def tryToJson[A, M[A] <: Try[A]](implicit convert: ToJson[A]) =
    new ToJson[M[A]] {
      def apply(value: M[A]): JsonValue =
        value.fold(_ => JsonValue.NULL, convert)
    }

  /** Creates ToJson for converting IterableOnce to JsonArray. */
  implicit def iterableOnceToJson[A, M[A] <: IterableOnce[A]](implicit convert: ToJson[A]) =
    new ToJson[M[A]] {
      def apply(values: M[A]): JsonValue =
        values.iterator.foldLeft(Json.createArrayBuilder())(_.add(_)).build()
    }

  /** Creates ToJson for converting Array to JsonArray. */
  implicit def arrayToJson[T](implicit convert: ToJson[T]) =
    new ToJson[Array[T]] {
      def apply(values: Array[T]): JsonValue =
        values.foldLeft(Json.createArrayBuilder())(_.add(_)).build()
    }

  /** Converts Array[String] to JsonValue. */
  implicit def arrayOfStringAsJson(values: Array[String])(implicit convert: ToJson[Array[String]]): JsonValue =
    convert(values)

  /** Converts M[A] to JsonValue. */
  implicit def containerAsJson[A, M[A]](value: M[A])(implicit convert: ToJson[M[A]]): JsonValue =
    convert(value)

  /** Converts Either[A, B] to JsonValue. */
  implicit def eitherAsJson[A, B](value: Either[A, B])(implicit left: ToJson[A], right: ToJson[B]): JsonValue =
    value.fold(left, right)

  /**
   * Provides extension methods to `javax.json.JsonValue`.
   *
   * @see [[JsonArrayType]], [[JsonObjectType]]
   */
  implicit class JsonValueType(val json: JsonValue) extends AnyVal {
    /**
     * Gets value in JsonArray.
     *
     * Alias to `get(index)`.
     */
    def \(index: Int): JsonValue = get(index)

    /**
     * Gets value in JsonObject.
     *
     * Alias to `get(name)`.
     */
    def \(name: String): JsonValue = get(name)

    /**
     * Gets value of all fields with specified name.
     *
     * A lookup is performed in current JsonValue and all descendents.
     */
    def \\(name: String): Seq[JsonValue] =
      json match {
        case arr: JsonArray => arr.asScala.flatMap(_ \\ name).toSeq

        case obj: JsonObject =>
          Option(obj.get(name)).toSeq ++: obj.values.asScala.flatMap(_ \\ name).toSeq

        case _ => Nil
      }

    /** Gets value in JsonArray. */
    def get(index: Int): JsonValue = asArray.get(index)

    /** Gets value in JsonObject. */
    def get(name: String): JsonValue = asObject.get(name)

    /** Converts json to requested type. */
    def as[T](implicit convert: FromJson[T]): T =
      convert(json)

    /** Optionally converts json to requested type. */
    def asOption[T](implicit convert: FromJson[T]): Option[T] =
      asTry[T].toOption

    /** Tries to convert json to requested type. */
    def asTry[T](implicit convert: FromJson[T]): Try[T] =
      Try(as[T])

    /** Casts json to JsonString. */
    def asString: JsonString =
      json.asInstanceOf[JsonString]

    /** Casts json to JsonNumber. */
    def asNumber: JsonNumber =
      json.asInstanceOf[JsonNumber]

    /** Casts json to JsonStructure. */
    def asStructure: JsonStructure =
      json.asInstanceOf[JsonStructure]

    /** Casts json to JsonArray. */
    def asArray: JsonArray =
      json.asInstanceOf[JsonArray]

    /** Casts json to JsonObject. */
    def asObject: JsonObject =
      json.asInstanceOf[JsonObject]
  }

  /**
   * Provides extension methods to `javax.json.JsonArray`.
   *
   * @see [[JsonValueType]], [[JsonObjectType]]
   */
  implicit class JsonArrayType(val json: JsonArray) extends AnyVal {
    /**
     * Gets value from array and converts it to requested type or returns
     * evaluated default.
     */
    def getOrElse[T](index: Int, default: => T)(implicit convert: FromJson[T]): T =
      getTry[T](index).getOrElse(default)

    /** Optionally gets value from array and converts it to requested type. */
    def getOption[T](index: Int)(implicit convert: FromJson[T]): Option[T] =
      getTry[T](index).toOption

    /** Tries to get value from array and convert it to requested type. */
    def getTry[T](index: Int)(implicit convert: FromJson[T]): Try[T] =
      Try(json.get(index).as[T])

    /** Gets Long from array. */
    def getLong(index: Int): Long =
      json.getJsonNumber(index).longValue

    /** Gets Long from array or returns default. */
    def getLong(index: Int, default: Long): Long =
      Try(getLong(index)).getOrElse(default)

    /** Gets Double from array. */
    def getDouble(index: Int): Double =
      json.getJsonNumber(index).doubleValue

    /** Gets Double from array or returns default. */
    def getDouble(index: Int, default: Double): Double =
      Try(getDouble(index)).getOrElse(default)

    /** Gets BigInt from array. */
    def getBigInt(index: Int): BigInt =
      json.getJsonNumber(index).bigIntegerValue

    /** Gets BigInt from array or returns default. */
    def getBigInt(index: Int, default: BigInt): BigInt =
      Try(getBigInt(index)).getOrElse(default)

    /** Gets BigDecimal from array. */
    def getBigDecimal(index: Int): BigDecimal =
      json.getJsonNumber(index).bigDecimalValue

    /** Gets BigDecimal from array or returns default. */
    def getBigDecimal(index: Int, default: BigDecimal): BigDecimal =
      Try(getBigDecimal(index)).getOrElse(default)

    /** Tests whether value in array is integral. */
    def isIntegral(index: Int): Boolean =
      Try(json.getJsonNumber(index).isIntegral).getOrElse(false)

    /** Creates new JsonArray with additional value. */
    def %%(value: JsonValue): JsonArray =
      new CombinedJsonArray(json, Json.arr(value))

    /** Creates new JsonArray by concatenating other array to this array. */
    def ++(other: JsonArray): JsonArray =
      new CombinedJsonArray(json, other)
  }

  /**
   * Provides extension methods to `javax.json.JsonObject`.
   *
   * @see [[JsonValueType]], [[JsonArrayType]]
   */
  implicit class JsonObjectType(val json: JsonObject) extends AnyVal {
    /**
     * Gets value from object and converts it to requested type or returns
     * evaluated default.
     */
    def getOrElse[T](name: String, default: => T)(implicit convert: FromJson[T]): T =
      getTry[T](name).getOrElse(default)

    /** Optionally gets value from object and converts it to requested type. */
    def getOption[T](name: String)(implicit convert: FromJson[T]): Option[T] =
      getTry[T](name).toOption

    /** Tries to get value from object and convert it to requested type. */
    def getTry[T](name: String)(implicit convert: FromJson[T]): Try[T] =
      Try(json.get(name).as[T])

    /** Gets Long from object. */
    def getLong(name: String): Long =
      json.getJsonNumber(name).longValue

    /** Gets Long from object or returns default. */
    def getLong(name: String, default: Long): Long =
      Try(getLong(name)).getOrElse(default)

    /** Gets Double from object. */
    def getDouble(name: String): Double =
      json.getJsonNumber(name).doubleValue

    /** Gets Double from object or returns default. */
    def getDouble(name: String, default: Double): Double =
      Try(getDouble(name)).getOrElse(default)

    /** Gets BigInt from object. */
    def getBigInt(name: String): BigInt =
      json.getJsonNumber(name).bigIntegerValue

    /** Gets BigInt from object or returns default. */
    def getBigInt(name: String, default: BigInt): BigInt =
      Try(getBigInt(name)).getOrElse(default)

    /** Gets BigDecimal from object. */
    def getBigDecimal(name: String): BigDecimal =
      json.getJsonNumber(name).bigDecimalValue

    /** Gets BigDecimal from object or returns default. */
    def getBigDecimal(name: String, default: BigDecimal): BigDecimal =
      Try(getBigDecimal(name)).getOrElse(default)

    /** Tests whether value in object is integral. */
    def isIntegral(name: String): Boolean =
      Try(json.getJsonNumber(name).isIntegral).getOrElse(false)

    /**
     * Creates new JsonObject with additional field.
     *
     * If the field already exists, its value is replaced.
     */
    def %%(field: (String, JsonValue)): JsonObject =
      new MergedJsonObject(json, Json.obj(field))

    /**
     * Creates new JsonObject by merging other object with this one.
     *
     * If a field exists in both objects, the value from `other` is used.
     */
    def ++(other: JsonObject): JsonObject =
      new MergedJsonObject(json, other)
  }

  /**
   * Provides extension methods to `javax.json.JsonArrayBuilder`.
   *
   * @see [[JsonObjectBuilderType]]
   */
  implicit class JsonArrayBuilderType(val builder: JsonArrayBuilder) extends AnyVal {
    /** Adds value to array builder if `Some`; otherwise, adds null if `None`. */
    def add(value: Option[JsonValue]): JsonArrayBuilder =
      value.fold(builder.addNull()) { builder.add(_) }

    /** Adds value to array builder if `Success`; otherwise, adds null if `Failure`. */
    def add(value: Try[JsonValue]): JsonArrayBuilder =
      value.fold(_ => builder.addNull(), builder.add(_))

    /** Adds value to array builder. */
    def add[T](value: T)(implicit companion: ArrayBuilderCompanion[T]): JsonArrayBuilder =
      companion.add(value)(builder)

    /** Adds value to array builder or adds null if value is null. */
    def addNullable[T](value: T)(implicit companion: ArrayBuilderCompanion[T]): JsonArrayBuilder =
      if (value == null) builder.addNull()
      else companion.add(value)(builder)
  }

  /**
   * Provides extension methods to `javax.json.JsonObjectBuilder`.
   *
   * @see [[JsonArrayBuilderType]]
   */
  implicit class JsonObjectBuilderType(val builder: JsonObjectBuilder) extends AnyVal {
    /** Adds value to object builder if `Some`; otherwise, adds null if `None`. */
    def add(name: String, value: Option[JsonValue]): JsonObjectBuilder =
      value.fold(builder.addNull(name)) { builder.add(name, _) }

    /** Adds value to object builder if `Success`; otherwise, adds null if `Failure`. */
    def add(name: String, value: Try[JsonValue]): JsonObjectBuilder =
      value.fold(_ => builder.addNull(name), builder.add(name, _))

    /** Adds value to object builder. */
    def add[T](name: String, value: T)(implicit companion: ObjectBuilderCompanion[T]): JsonObjectBuilder =
      companion.add(name, value)(builder)

    /** Adds value to object builder or adds null if value is null. */
    def addNullable[T](name: String, value: T)(implicit companion: ObjectBuilderCompanion[T]): JsonObjectBuilder =
      if (value == null) builder.addNull(name)
      else companion.add(name, value)(builder)
  }

  /**
   * Provides extension methods to `javax.json.stream.JsonGenerator`.
   *
   * @see [[JsonParserType]]
   */
  implicit class JsonGeneratorType(val generator: JsonGenerator) extends AnyVal {
    /** Writes value to array context if `Some`; otherwise, writes null if `None`. */
    def write(value: Option[JsonValue]): JsonGenerator =
      value.fold(generator.writeNull()) { generator.write(_) }

    /** Writes value to array context if `Success`; otherwise, writes null if `Failure`. */
    def write(value: Try[JsonValue]): JsonGenerator =
      value.fold(_ => generator.writeNull(), generator.write(_))

    /** Writes value in array context. */
    def write[T](value: T)(implicit writer: ArrayContextWriter[T]): JsonGenerator =
      writer.write(value)(generator)

    /** Writes value in array context or writes null if value is null. */
    def writeNullable[T](value: T)(implicit writer: ArrayContextWriter[T]): JsonGenerator =
      if (value == null) generator.writeNull()
      else writer.write(value)(generator)

    /** Writes value to object context if `Some`; otherwise, writes null if `None`. */
    def write(name: String, value: Option[JsonValue]): JsonGenerator =
      value.fold(generator.writeNull(name)) { generator.write(name, _) }

    /** Writes value to object context if `Success`; otherwise, writes null if `Failure`. */
    def write(name: String, value: Try[JsonValue]): JsonGenerator =
      value.fold(_ => generator.writeNull(name), generator.write(name, _))

    /** Writes value in object context. */
    def write[T](name: String, value: T)(implicit writer: ObjectContextWriter[T]): JsonGenerator =
      writer.write(name, value)(generator)

    /** Writes value in object context or writes null if value is null. */
    def writeNullable[T](name: String, value: T)(implicit writer: ObjectContextWriter[T]): JsonGenerator =
      if (value == null) generator.writeNull(name)
      else writer.write(name, value)(generator)
  }

  /**
   * Provides extension methods to `javax.json.stream.JsonParser`.
   *
   * @see [[JsonGeneratorType]]
   */
  implicit class JsonParserType(val parser: JsonParser) extends AnyVal {
    import JsonParser.Event._

    /**
     * Parses next JSON array.
     *
     * Throws `JsonException` if next parser state is not start of array.
     */
    def nextArray(): JsonArray =
      parser.next() match {
        case START_ARRAY => getArray()
        case event => throw new JsonException(s"START_ARRAY expected but found $event")
      }

    /**
     * Parses next JSON object.
     *
     * Throws `JsonException` if next parser state is not start of object.
     */
    def nextObject(): JsonObject =
      parser.next() match {
        case START_OBJECT => getObject()
        case event => throw new JsonException(s"START_OBJECT expected but found $event")
      }

    /**
     * Gets JSON array.
     *
     * Throws `JsonException` if parser enters unexpected state.
     */
    def getArray(): JsonArray = {
      val builder = Json.createArrayBuilder()

      nextUntil(END_ARRAY) {
        case VALUE_STRING => builder.add(parser.getString())
        case VALUE_NUMBER => builder.add(parser.getBigDecimal())
        case VALUE_TRUE   => builder.add(true)
        case VALUE_FALSE  => builder.add(false)
        case VALUE_NULL   => builder.addNull()
        case START_ARRAY  => builder.add(getArray())
        case START_OBJECT => builder.add(getObject())
        case event        => throw new JsonException(s"unexpected parser event: $event")
      }

      builder.build()
    }

    /**
     * Gets JSON object.
     *
     * Throws `JsonException` if parser enters unexpected state.
     */
    def getObject(): JsonObject = {
      val builder = Json.createObjectBuilder()

      nextUntil(END_OBJECT) {
        case KEY_NAME =>
          val key = parser.getString()

          parser.next() match {
            case VALUE_STRING => builder.add(key, parser.getString())
            case VALUE_NUMBER => builder.add(key, parser.getBigDecimal())
            case VALUE_TRUE   => builder.add(key, true)
            case VALUE_FALSE  => builder.add(key, false)
            case VALUE_NULL   => builder.addNull(key)
            case START_ARRAY  => builder.add(key, getArray())
            case START_OBJECT => builder.add(key, getObject())
            case event        => throw new JsonException(s"unexpected parser event: $event")
          }

        case event => throw new JsonException(s"KEY_NAME expected but found $event")
      }

      builder.build()
    }

    private def nextUntil(terminal: JsonParser.Event)(f: JsonParser.Event => Unit): Unit = {
      var event = parser.next()

      while (event != terminal) {
        f(event)
        event = parser.next()
      }
    }
  }
}
