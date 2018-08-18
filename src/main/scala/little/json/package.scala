package little

import java.io.{ File, FileReader, InputStream, OutputStream, Reader, StringReader, Writer }

import javax.json._
import javax.json.{ Json => JavaxJson }
import javax.json.stream.{ JsonGenerator, JsonParser }

import scala.collection.JavaConverters.iterableAsScalaIterable
import scala.util.Try

/** Provides JSON related type classes and utilities. */
package object json {
  /** Writes value of type T in array context. */
  trait ArrayContextWriter[T] extends Any {
    /** Writes value in array context. */
    def write(value: T)(implicit generator: JsonGenerator): JsonGenerator
  }

  /** Writes value of type T in object context. */
  trait ObjectContextWriter[T] extends Any {
    /** Writes value in object context. */
    def write(name: String, value: T)(implicit generator: JsonGenerator): JsonGenerator
  }

  /** Writes value of type T in requested context. */
  trait ContextWriter[T] extends ArrayContextWriter[T] with ObjectContextWriter[T]

  /** Converts value of type T to JsonValue. */
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

  /** Converts JsonValue to value of type T. */
  trait FromJson[T] {
    /** Converts json to T. */
    def apply(json: JsonValue): T
  }

  /** Provides JSON utilities. */
  object Json {
    /** Converts value to JSON value. */
    def toJson[T](value: T)(implicit convert: ToJson[T]): JsonValue =
      convert(value)

    /** Parses given text to JsonStructure. */
    def parse[T <: JsonStructure](text: String): T = {
      val in = new StringReader(text)
      try parse(in)
      finally Try(in.close())
    }

    /** Parses text from given input stream to JsonStructure. */
    def parse[T <: JsonStructure](in: InputStream): T = {
      val json = createReader(in)
      try json.read().asInstanceOf[T]
      finally Try(json.close())
    }

    /** Parses text from given reader to JsonStructure. */
    def parse[T <: JsonStructure](reader: Reader): T = {
      val json = createReader(reader)
      try json.read().asInstanceOf[T]
      finally Try(json.close())
    }

    /** Parses text from given file to JsonStructure. */
    def parse[T <: JsonStructure](file: File): T = {
      val in = new FileReader(file)
      try parse(in)
      finally Try(in.close())
    }

    /** Creates JsonArrayBuilder. */
    def createArrayBuilder(): JsonArrayBuilder =
      JavaxJson.createArrayBuilder()

    /** Creates JsonObjectBuilder. */
    def createObjectBuilder(): JsonObjectBuilder =
      JavaxJson.createObjectBuilder()

    /** Creates JsonReader with given input stream. */
    def createReader(in: InputStream): JsonReader =
      JavaxJson.createReader(in)

    /** Creates JsonReader with given reader. */
    def createReader(reader: Reader): JsonReader =
      JavaxJson.createReader(reader)

    /** Creates JsonWriter with given output stream. */
    def createWriter(out: OutputStream): JsonWriter =
      JavaxJson.createWriter(out)

    /** Creates JsonWriter with given writer. */
    def createWriter(writer: Writer): JsonWriter =
      JavaxJson.createWriter(writer)

    /** Creates JsonParser with given input stream. */
    def createParser(in: InputStream): JsonParser =
      JavaxJson.createParser(in)

    /** Creates JsonParser with given reader. */
    def createParser(reader: Reader): JsonParser =
      JavaxJson.createParser(reader)

    /** Creates JsonGenerator with given output stream. */
    def createGenerator(out: OutputStream): JsonGenerator =
      JavaxJson.createGenerator(out)

    /** Creates JsonGenerator with given writer. */
    def createGenerator(writer: Writer): JsonGenerator =
      JavaxJson.createGenerator(writer)
  }

  /** Type class of {@code javax.json.JsonValue} */
  implicit class LittleJsonValue(val json: JsonValue) extends AnyVal {
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

  /** Type class of {@code javax.json.JsonArray} */
  implicit class LittleJsonArray(val json: JsonArray) extends AnyVal {
    /** Gets value from array and converts it to requested type. */
    def get[T](index: Int)(implicit convert: FromJson[T]): T =
      convert(json.get(index))

    /** Optionally gets value from array and converts it to requested type. */
    def getOption[T](index: Int)(implicit convert: FromJson[T]): Option[T] =
      getTry[T](index).toOption

    /** Tries to get value from array and convert it to requested type. */
    def getTry[T](index: Int)(implicit convert: FromJson[T]): Try[T] =
      Try(get[T](index))

    /** Gets exact Int from array. */
    def getIntExact(index: Int): Int =
      json.getJsonNumber(index).intValueExact

    /** Gets exact Int from array or returns default. */
    def getIntExact(index: Int, default: Int): Int =
      Try(getIntExact(index)).getOrElse(default)

    /** Gets Long from array. */
    def getLong(index: Int): Long =
      json.getJsonNumber(index).longValue

    /** Gets Long from array or returns default. */
    def getLong(index: Int, default: Long): Long =
      Try(getLong(index)).getOrElse(default)

    /** Gets exact Long from array. */
    def getLongExact(index: Int): Long =
      json.getJsonNumber(index).longValueExact

    /** Gets exact Long from array or returns default. */
    def getLongExact(index: Int, default: Long): Long =
      Try(getLongExact(index)).getOrElse(default)

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

    /** Gets exact BigInt from array. */
    def getBigIntExact(index: Int): BigInt =
      json.getJsonNumber(index).bigIntegerValueExact

    /** Gets exact BigInt from array or returns default. */
    def getBigIntExact(index: Int, default: BigInt): BigInt =
      Try(getBigIntExact(index)).getOrElse(default)

    /** Gets BigDecimal from array. */
    def getBigDecimal(index: Int): BigDecimal =
      json.getJsonNumber(index).bigDecimalValue

    /** Gets BigDecimal from array or returns default. */
    def getBigDecimal(index: Int, default: BigDecimal): BigDecimal =
      Try(getBigDecimal(index)).getOrElse(default)

    /** Tests whether JsonNumber in array is integral. */
    def isIntegral(index: Int): Boolean =
      json.getJsonNumber(index).isIntegral
  }

  /** Type class of {@code javax.json.JsonObject} */
  implicit class LittleJsonObject(val json: JsonObject) extends AnyVal {
    /** Gets value from object and converts it to requested type. */
    def get[T](name: String)(implicit convert: FromJson[T]): T =
      convert(json.get(name))

    /** Optionally gets value from object and converts it to requested type. */
    def getOption[T](name: String)(implicit convert: FromJson[T]): Option[T] =
      getTry[T](name).toOption

    /** Tries to get value from object and convert it to requested type. */
    def getTry[T](name: String)(implicit convert: FromJson[T]): Try[T] =
      Try(get[T](name))

    /** Gets exact Int from object. */
    def getIntExact(name: String): Int =
      json.getJsonNumber(name).intValueExact

    /** Gets exact Int from object or returns default. */
    def getIntExact(name: String, default: Int): Int =
      Try(getIntExact(name)).getOrElse(default)

    /** Gets Long from object. */
    def getLong(name: String): Long =
      json.getJsonNumber(name).longValue

    /** Gets Long from object or returns default. */
    def getLong(name: String, default: Long): Long =
      Try(getLong(name)).getOrElse(default)

    /** Gets exact Long from object. */
    def getLongExact(name: String): Long =
      json.getJsonNumber(name).longValueExact

    /** Gets exact Long from object or returns default. */
    def getLongExact(name: String, default: Long): Long =
      Try(getLongExact(name)).getOrElse(default)

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

    /** Gets exact BigInt from object. */
    def getBigIntExact(name: String): BigInt =
      json.getJsonNumber(name).bigIntegerValueExact

    /** Gets exact BigInt from object or returns default. */
    def getBigIntExact(name: String, default: BigInt): BigInt =
      Try(getBigIntExact(name)).getOrElse(default)

    /** Gets BigDecimal from object. */
    def getBigDecimal(name: String): BigDecimal =
      json.getJsonNumber(name).bigDecimalValue

    /** Gets BigDecimal from object or returns default. */
    def getBigDecimal(name: String, default: BigDecimal): BigDecimal =
      Try(getBigDecimal(name)).getOrElse(default)

    /** Tests whether JsonNumber in object is integral. */
    def isIntegral(name: String): Boolean =
      json.getJsonNumber(name).isIntegral
  }

  /** Type class of {@code javax.json.stream.JsonParser} */
  implicit class LittleJsonParser(val parser: JsonParser) extends AnyVal {
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
      val builder = JavaxJson.createArrayBuilder()
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
      val builder = JavaxJson.createObjectBuilder()
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

  /** Provides standard set of ContextWriters. */
  object ContextWriter {
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

  /** Type class of {@code javax.json.stream.JsonGenerator} */
  implicit class LittleJsonGenerator(val generator: JsonGenerator) extends AnyVal {
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
}
