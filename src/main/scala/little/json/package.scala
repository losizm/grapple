package little

import java.io.{ File, FileReader, InputStream, OutputStream, Reader, StringReader, Writer }

import javax.json._
import javax.json.{ Json => JavaxJson }
import javax.json.stream.{ JsonGenerator, JsonParser }

import scala.collection.JavaConverters.iterableAsScalaIterable
import scala.util.Try

/** Provides JSON related type classes and utilities. */
package object json {
  /** Provides JSON utilities. */
  object Json {
    /** Converts value to JSON value. */
    def toJson[T](value: T)(implicit convert: T => JsonValue): JsonValue =
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
  implicit class JsonValueType(val json: JsonValue) extends AnyVal {
    /** Converts json to specified type. */
    def as[T](implicit convert: JsonValue => T): T =
      convert(json)

    /** Tries to convert json to specified type. */
    def asTry[T](implicit convert: JsonValue => T): Try[T] =
      Try(as[T])

     /** Optionally converts json to specified type. */
    def asOption[T](implicit convert: JsonValue => T): Option[T] =
      asTry[T].toOption

    /** Gets String from JsonArray. */
    def getString(index: Int): String =
      json.asInstanceOf[JsonArray].getString(index)

    /** Gets String from JsonArray or returns default. */
    def getString(index: Int, default: String): String =
      json.asInstanceOf[JsonArray].getString(index, default)

    /** Gets Int from JsonArray. */
    def getInt(index: Int): Int =
      json.asInstanceOf[JsonArray].getInt(index)

    /** Gets Int from JsonArray or returns default. */
    def getInt(index: Int, default: Int): Int =
      json.asInstanceOf[JsonArray].getInt(index, default)

    /** Gets exact Int from JsonArray. */
    def getIntExact(index: Int): Int =
      json.asInstanceOf[JsonArray].getJsonNumber(index).intValueExact

    /** Gets exact Int from JsonArray or returns default. */
    def getIntExact(index: Int, default: Int): Int = {
      val arr = json.asInstanceOf[JsonArray]
      Try(arr.getJsonNumber(index).intValueExact).getOrElse(default)
    }

    /** Gets Long from JsonArray. */
    def getLong(index: Int): Long =
      json.asInstanceOf[JsonArray].getJsonNumber(index).longValue

    /** Gets Long from JsonArray or returns default. */
    def getLong(index: Int, default: Long): Long = {
      val arr = json.asInstanceOf[JsonArray]
      Try(arr.getJsonNumber(index).longValue).getOrElse(default)
    }

    /** Gets exact Long from JsonArray. */
    def getLongExact(index: Int): Long =
      json.asInstanceOf[JsonArray].getJsonNumber(index).longValueExact

    /** Gets exact Long from JsonArray or returns default. */
    def getLongExact(index: Int, default: Long): Long = {
      val arr = json.asInstanceOf[JsonArray]
      Try(arr.getJsonNumber(index).longValueExact).getOrElse(default)
    }

    /** Gets Double from JsonArray. */
    def getDouble(index: Int): Double =
      json.asInstanceOf[JsonArray].getJsonNumber(index).doubleValue

    /** Gets Double from JsonArray or returns default. */
    def getDouble(index: Int, default: Double): Double = {
      val arr = json.asInstanceOf[JsonArray]
      Try(arr.getJsonNumber(index).doubleValue).getOrElse(default)
    }

    /** Gets BigInt from JsonArray. */
    def getBigInt(index: Int): BigInt =
      json.asInstanceOf[JsonArray].getJsonNumber(index).bigIntegerValue

    /** Gets BigInt from JsonArray or returns default. */
    def getBigInt(index: Int, default: BigInt): BigInt = {
      val arr = json.asInstanceOf[JsonArray]
      Try[BigInt](arr.getJsonNumber(index).bigIntegerValue).getOrElse(default)
    }

    /** Gets exact BigInt from JsonArray. */
    def getBigIntExact(index: Int): BigInt =
      json.asInstanceOf[JsonArray].getJsonNumber(index).bigIntegerValueExact

    /** Gets exact BigInt from JsonArray or returns default. */
    def getBigIntExact(index: Int, default: BigInt): BigInt = {
      val arr = json.asInstanceOf[JsonArray]
      Try[BigInt](arr.getJsonNumber(index).bigIntegerValueExact).getOrElse(default)
    }

    /** Gets BigDecimal from JsonArray. */
    def getBigDecimal(index: Int): BigDecimal =
      json.asInstanceOf[JsonArray].getJsonNumber(index).bigDecimalValue

    /** Gets BigDecimal from JsonArray or returns default. */
    def getBigDecimal(index: Int, default: BigDecimal): BigDecimal = {
      val arr = json.asInstanceOf[JsonArray]
      Try[BigDecimal](arr.getJsonNumber(index).bigDecimalValue).getOrElse(default)
    }

    /** Gets Boolean from JsonArray. */
    def getBoolean(index: Int): Boolean =
      json.asInstanceOf[JsonArray].getBoolean(index)

    /** Gets Boolean from JsonArray or returns default. */
    def getBoolean(index: Int, default: Boolean): Boolean =
      json.asInstanceOf[JsonArray].getBoolean(index, default)

    /** Tests whether value in JsonArray is null. */
    def isNull(index: Int): Boolean =
      json.asInstanceOf[JsonArray].isNull(index)

    /** Tests whether JsonNumber in JsonArray is integral. */
    def isIntegral(index: Int): Boolean =
      json.asInstanceOf[JsonArray].getJsonNumber(index).isIntegral

    /** Gets JsonArray from JsonArray. */
    def getJsonArray(index: Int): JsonArray =
      json.asInstanceOf[JsonArray].getJsonArray(index)

    /** Gets JsonObject from JsonArray. */
    def getJsonObject(index: Int): JsonObject =
      json.asInstanceOf[JsonArray].getJsonObject(index)

    /** Gets String from JsonObject. */
    def getString(name: String): String =
      json.asInstanceOf[JsonObject].getString(name)

    /** Gets String from JsonObject or returns default. */
    def getString(name: String, default: String): String =
      json.asInstanceOf[JsonObject].getString(name, default)

    /** Gets Int from JsonObject. */
    def getInt(name: String): Int =
      json.asInstanceOf[JsonObject].getInt(name)

    /** Gets Int from JsonObject or returns default. */
    def getInt(name: String, default: Int): Int =
      json.asInstanceOf[JsonObject].getInt(name, default)

    /** Gets exact Int from JsonObject. */
    def getIntExact(name: String): Int =
      json.asInstanceOf[JsonObject].getJsonNumber(name).intValueExact

    /** Gets exact Int from JsonObject or returns default. */
    def getIntExact(name: String, default: Int): Int = {
      val obj = json.asInstanceOf[JsonObject]
      Try(obj.getJsonNumber(name).intValueExact).getOrElse(default)
    }

    /** Gets Long from JsonObject. */
    def getLong(name: String): Long =
      json.asInstanceOf[JsonObject].getJsonNumber(name).longValue

    /** Gets Long from JsonObject or returns default. */
    def getLong(name: String, default: Long): Long = {
      val obj = json.asInstanceOf[JsonObject]
      Try(obj.getJsonNumber(name).longValue).getOrElse(default)
    }

    /** Gets exact Long from JsonObject. */
    def getLongExact(name: String): Long =
      json.asInstanceOf[JsonObject].getJsonNumber(name).longValueExact

    /** Gets exact Long from JsonObject or returns default. */
    def getLongExact(name: String, default: Long): Long = {
      val obj = json.asInstanceOf[JsonObject]
      Try(obj.getJsonNumber(name).longValueExact).getOrElse(default)
    }

    /** Gets Double from JsonObject. */
    def getDouble(name: String): Double =
      json.asInstanceOf[JsonObject].getJsonNumber(name).doubleValue

    /** Gets Double from JsonObject or returns default. */
    def getDouble(name: String, default: Double): Double = {
      val obj = json.asInstanceOf[JsonObject]
      Try(obj.getJsonNumber(name).doubleValue).getOrElse(default)
    }

    /** Gets BigInt from JsonObject. */
    def getBigInt(name: String): BigInt =
      json.asInstanceOf[JsonObject].getJsonNumber(name).bigIntegerValue

    /** Gets BigInt from JsonObject or returns default. */
    def getBigInt(name: String, default: BigInt): BigInt = {
      val obj = json.asInstanceOf[JsonObject]
      Try[BigInt](obj.getJsonNumber(name).bigIntegerValue).getOrElse(default)
    }

    /** Gets exact BigInt from JsonObject. */
    def getBigIntExact(name: String): BigInt =
      json.asInstanceOf[JsonObject].getJsonNumber(name).bigIntegerValueExact

    /** Gets exact BigInt from JsonObject or returns default. */
    def getBigIntExact(name: String, default: BigInt): BigInt = {
      val obj = json.asInstanceOf[JsonObject]
      Try[BigInt](obj.getJsonNumber(name).bigIntegerValueExact).getOrElse(default)
    }

    /** Gets BigDecimal from JsonObject. */
    def getBigDecimal(name: String): BigDecimal =
      json.asInstanceOf[JsonObject].getJsonNumber(name).bigDecimalValue

    /** Gets BigDecimal from JsonObject or returns default. */
    def getBigDecimal(name: String, default: BigDecimal): BigDecimal = {
      val obj = json.asInstanceOf[JsonObject]
      Try[BigDecimal](obj.getJsonNumber(name).bigDecimalValue).getOrElse(default)
    }

    /** Gets Boolean from JsonObject. */
    def getBoolean(name: String): Boolean =
      json.asInstanceOf[JsonObject].getBoolean(name)

    /** Gets Boolean from JsonObject or returns default. */
    def getBoolean(name: String, default: Boolean): Boolean =
      json.asInstanceOf[JsonObject].getBoolean(name)

    /** Tests whether value in JsonObject is null. */
    def isNull(name: String): Boolean =
      json.asInstanceOf[JsonObject].isNull(name)

    /** Tests whether JsonNumber in JsonObject is integral. */
    def isIntegral(name: String): Boolean =
      json.asInstanceOf[JsonObject].getJsonNumber(name).isIntegral

    /** Gets JsonArray from JsonObject. */
    def getJsonArray(name: String): JsonArray =
      json.asInstanceOf[JsonObject].getJsonArray(name)

    /** Gets JsonObject from JsonObject. */
    def getJsonObject(name: String): JsonObject =
      json.asInstanceOf[JsonObject].getJsonObject(name)
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

  /** Writes value of type T in array context. */
  trait ArrayContextWriter[T] {
    /** Writes value in array context. */
    def write(value: T)(implicit generator: JsonGenerator): JsonGenerator
  }

  /** Writes value of type T in object context. */
  trait ObjectContextWriter[T] {
    /** Writes value in object context. */
    def write(name: String, value: T)(implicit generator: JsonGenerator): JsonGenerator
  }

  /** Type class of {@code javax.json.stream.JsonGenerator} */
  implicit class JsonGeneratorType(val generator: JsonGenerator) extends AnyVal {
    /** Writes value in array context. */
    def write[T](value: T)(implicit writer: ArrayContextWriter[T]): JsonGenerator =
      writer.write(value)(generator)

    /** Writes value in object context. */
    def write[T](name: String, value: T)(implicit writer: ObjectContextWriter[T]): JsonGenerator =
      writer.write(name, value)(generator)

    /** Writes value in array context or writes null if value is null. */
    def writeNullable[T](value: T)(implicit writer: ArrayContextWriter[T]): JsonGenerator =
      if (value == null) generator.writeNull()
      else writer.write(value)(generator)

    /** Writes value in object context or writes null if value is null. */
    def writeNullable[T](name: String, value: T)(implicit writer: ObjectContextWriter[T]): JsonGenerator =
      if (value == null) generator.writeNull(name)
      else writer.write(name, value)(generator)

    /**
     * Writes value in array context if {@code Some}; otherwise, writes null if
     * {@code None}.
     */
    def writeOption[T](value: Option[T])(implicit writer: ArrayContextWriter[T]): JsonGenerator =
      value.fold(generator.writeNull()) { x => writer.write(x)(generator) }

    /**
     * Writes value in object context if {@code Some}; otherwise, writes null if
     * {@code None}.
     */
    def writeOption[T](name: String, value: Option[T])(implicit writer: ObjectContextWriter[T]): JsonGenerator =
      value.fold(generator.writeNull(name)) { x => writer.write(name, x)(generator) }
  }
}
