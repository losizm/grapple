package little.json

import javax.json._
import javax.json.stream.{ JsonGenerator, JsonParser }

import scala.util.Try

/** Provides implicit values and types. */
object Implicits {
  /** Converts json to String. */
  implicit val jsonToString: FromJson[String] = {
    case json: JsonString => json.getString
    case json => throw new JsonException(s"required STRING but found ${json.getValueType}")
  }

  /** Converts json to Int (exact). */
  implicit val jsonToInt: FromJson[Int] = {
    case json: JsonNumber => json.intValueExact
    case json => throw new JsonException(s"required NUMBER but found ${json.getValueType}")
  }

  /** Converts json to Long (exact). */
  implicit val jsonToLong: FromJson[Long] = {
    case json: JsonNumber => json.longValueExact
    case json => throw new JsonException(s"required NUMBER but found ${json.getValueType}")
  }

  /** Converts json to Double. */
  implicit val jsonToDouble: FromJson[Double] = {
    case json: JsonNumber => json.doubleValue
    case json => throw new JsonException(s"required NUMBER but found ${json.getValueType}")
  }

  /** Converts json to BigInt (exact). */
  implicit val jsonToBigInt: FromJson[BigInt] = {
    case json: JsonNumber => json.bigIntegerValueExact
    case json => throw new JsonException(s"required NUMBER but found ${json.getValueType}")
  }

  /** Converts json to BigDecimal. */
  implicit val jsonToBigDecimal: FromJson[BigDecimal] = {
    case json: JsonNumber => json.bigDecimalValue
    case json => throw new JsonException(s"required NUMBER but found ${json.getValueType}")
  }

  /** Converts json to Boolean. */
  implicit val jsonToBoolean: FromJson[Boolean] = {
    case JsonValue.TRUE => true
    case JsonValue.FALSE => false
    case json => throw new JsonException(s"required TRUE or FALSE but found ${json.getValueType}")
  }

  /**
   * Type class of {@code javax.json.JsonValue}
   *
   * @see [[JsonArrayType]], [[JsonObjectType]]
   */
  implicit class JsonValueType(val json: JsonValue) extends AnyVal {
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
   * Type class of {@code javax.json.JsonArray}
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
  }

  /**
   * Type class of {@code javax.json.JsonObject}
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
  }

  /** Type class of {@code javax.json.stream.JsonGenerator} */
  implicit class JsonGeneratorType(val generator: JsonGenerator) extends AnyVal {
    /** Writes value in array context. */
    def write[T](value: T)(implicit writer: ArrayContextWriter[T]): JsonGenerator =
      writer.write(value)(generator)

    /** Writes value in array context or writes null if value is null. */
    def writeNullable[T](value: T)(implicit writer: ArrayContextWriter[T]): JsonGenerator =
      if (value == null) generator.writeNull()
      else writer.write(value)(generator)

    /**
     * Writes value in array context if {@code Some}; otherwise, writes null if
     * {@code None}.
     */
    def writeOption[T](value: Option[T])(implicit writer: ArrayContextWriter[T]): JsonGenerator =
      value.fold(generator.writeNull()) { x => writer.write(x)(generator) }

    /** Writes value in object context. */
    def write[T](name: String, value: T)(implicit writer: ObjectContextWriter[T]): JsonGenerator =
      writer.write(name, value)(generator)

    /** Writes value in object context or writes null if value is null. */
    def writeNullable[T](name: String, value: T)(implicit writer: ObjectContextWriter[T]): JsonGenerator =
      if (value == null) generator.writeNull(name)
      else writer.write(name, value)(generator)

    /**
     * Writes value in object context if {@code Some}; otherwise, writes null if
     * {@code None}.
     */
    def writeOption[T](name: String, value: Option[T])(implicit writer: ObjectContextWriter[T]): JsonGenerator =
      value.fold(generator.writeNull(name)) { x => writer.write(name, x)(generator) }
  }

  /** Type class of {@code javax.json.stream.JsonParser} */
  implicit class JsonParserType(val parser: JsonParser) extends AnyVal {
    import JsonParser.Event._

    /**
     * Parses next array.
     *
     * Throws {@code JsonException} if parser position is not at start of array.
     */
    def nextArray(): JsonArray = {
      if (parser.next() != START_ARRAY)
        throw new JsonException("Not start of array")

      finishArray()
    }

    /**
     * Parses next object.
     *
     * Throws {@code JsonException} if parser position is not at start of object.
     */
    def nextObject(): JsonObject = {
      if (parser.next() != START_OBJECT)
        throw new JsonException("Not start of object")

      finishObject()
    }

    /**
     * Parses remainder of array.
     *
     * Throws {@code JsonException} if unexpected parser event is encountered.
     */
    def finishArray(): JsonArray = {
      val builder = Json.createArrayBuilder()
      var evt = parser.next()

      while (evt != END_ARRAY) {
        evt match {
          case VALUE_STRING => builder.add(parser.getString())
          case VALUE_NUMBER => builder.add(parser.getBigDecimal())
          case VALUE_TRUE   => builder.add(true)
          case VALUE_FALSE  => builder.add(false)
          case VALUE_NULL   => builder.addNull()
          case START_ARRAY  => builder.add(finishArray())
          case START_OBJECT => builder.add(finishObject())
          case event        => throw new JsonException(s"Unexpected event: $event")
        }

        evt = parser.next()
      }

      builder.build()
    }

    /**
     * Parses remainder of object.
     *
     * Throws {@code JsonException} if unexpected parser event is encountered.
     */
    def finishObject(): JsonObject = {
      val builder = Json.createObjectBuilder()
      var evt = parser.next()

      while (evt != END_OBJECT) {
        if (evt != KEY_NAME)
          throw new JsonException(s"Unexpected event: $evt")

        val key = parser.getString()

        parser.next() match {
          case VALUE_STRING => builder.add(key, parser.getString())
          case VALUE_NUMBER => builder.add(key, parser.getBigDecimal())
          case VALUE_TRUE   => builder.add(key, true)
          case VALUE_FALSE  => builder.add(key, false)
          case VALUE_NULL   => builder.addNull(key)
          case START_ARRAY  => builder.add(key, finishArray())
          case START_OBJECT => builder.add(key, finishObject())
          case event        => throw new JsonException(s"Unexpected event: $event")
        }

        evt = parser.next()
      }

      builder.build()
    }
  }

  /** Writes String in requested context. */
  implicit object StringContextWriter extends ContextWriter[String] {
    /** Writes String in array context. */
    def write(value: String)(implicit generator: JsonGenerator): JsonGenerator =
      generator.write(value)

    /** Writes String in object context. */
    def write(name: String, value: String)(implicit generator: JsonGenerator): JsonGenerator =
      generator.write(name, value)
  }

  /** Writes Int in requested context. */
  implicit object IntContextWriter extends ContextWriter[Int] {
    /** Writes Int in array context. */
    def write(value: Int)(implicit generator: JsonGenerator): JsonGenerator =
      generator.write(value)

    /** Writes Int in object context. */
    def write(name: String, value: Int)(implicit generator: JsonGenerator): JsonGenerator =
      generator.write(name, value)
  }

  /** Writes Long in requested context. */
  implicit object LongContextWriter extends ContextWriter[Long] {
    /** Writes Long in array context. */
    def write(value: Long)(implicit generator: JsonGenerator): JsonGenerator =
      generator.write(value)

    /** Writes Long in object context. */
    def write(name: String, value: Long)(implicit generator: JsonGenerator): JsonGenerator =
      generator.write(name, value)
  }

  /** Writes Double in requested context. */
  implicit object DoubleContextWriter extends ContextWriter[Double] {
    /** Writes Double in array context. */
    def write(value: Double)(implicit generator: JsonGenerator): JsonGenerator =
      generator.write(value)

    /** Writes Double in object context. */
    def write(name: String, value: Double)(implicit generator: JsonGenerator): JsonGenerator =
      generator.write(name, value)
  }

  /** Writes BigInt in requested context. */
  implicit object BigIntContextWriter extends ContextWriter[BigInt] {
    /** Writes BigInt in array context. */
    def write(value: BigInt)(implicit generator: JsonGenerator): JsonGenerator =
      generator.write(value.bigInteger)

    /** Writes BigInt in object context. */
    def write(name: String, value: BigInt)(implicit generator: JsonGenerator): JsonGenerator =
      generator.write(name, value.bigInteger)
  }

  /** Writes BigDecimal in requested context. */
  implicit object BigDecimalContextWriter extends ContextWriter[BigDecimal] {
    /** Writes BigDecimal in array context. */
    def write(value: BigDecimal)(implicit generator: JsonGenerator): JsonGenerator =
      generator.write(value.bigDecimal)

    /** Writes BigDecimal in object context. */
    def write(name: String, value: BigDecimal)(implicit generator: JsonGenerator): JsonGenerator =
      generator.write(name, value.bigDecimal)
  }

  /** Writes Boolean in requested context. */
  implicit object BooleanContextWriter extends ContextWriter[Boolean] {
    /** Writes Boolean in array context. */
    def write(value: Boolean)(implicit generator: JsonGenerator): JsonGenerator =
      generator.write(value)

    /** Writes Boolean in object context. */
    def write(name: String, value: Boolean)(implicit generator: JsonGenerator): JsonGenerator =
      generator.write(name, value)
  }
}
