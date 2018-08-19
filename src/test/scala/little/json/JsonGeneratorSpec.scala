package little.json

import java.io.StringWriter

import org.scalatest.FlatSpec

import Implicits._
import Test._

class JsonGeneratorSpec extends FlatSpec {
  val user = User(0, "root", true)

  "JSON generator" should "generate array" in {
    val out = new StringWriter()
    val generator = Json.createGenerator(out)

    generator.writeStartArray()
    generator.write(user)
    generator.writeNullable(user)
    generator.writeOption(Some(user))
    generator.write(1)
    generator.write(1L)
    generator.write(1.1)
    generator.write(BigInt(1))
    generator.writeNullable(BigInt(1))
    generator.writeOption(Option(BigInt(1)))
    generator.write(BigDecimal(1.0))
    generator.writeNullable(BigDecimal(1.0))
    generator.writeOption(Some(BigDecimal(1.0)))
    generator.writeEnd()
    generator.close()
  }

  it should "generate object" in {
    val out = new StringWriter()
    val generator = Json.createGenerator(out)

    generator.writeStartObject()
    generator.write("a", user)
    generator.writeNullable("b", user)
    generator.writeOption("c", Some(user))
    generator.write("d", 1)
    generator.write("e", 1L)
    generator.write("f", 1.1)
    generator.write("g", BigInt(1))
    generator.writeNullable("h", BigInt(1))
    generator.writeOption("i", Option(BigInt(1)))
    generator.write("j", BigDecimal(1.0))
    generator.writeNullable("k", BigDecimal(1.0))
    generator.writeOption("l", Some(BigDecimal(1.0)))
    generator.writeEnd()
    generator.close()
  }
}
