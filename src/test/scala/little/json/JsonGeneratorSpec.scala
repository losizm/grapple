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
    generator.write(Seq(user, user, user))
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
    generator.write("d", Seq(user, user, user))
    generator.write("e", 1)
    generator.write("f", 1L)
    generator.write("g", 1.1)
    generator.write("h", BigInt(1))
    generator.writeNullable("i", BigInt(1))
    generator.writeOption("j", Option(BigInt(1)))
    generator.write("k", BigDecimal(1.0))
    generator.writeNullable("l", BigDecimal(1.0))
    generator.writeOption("m", Some(BigDecimal(1.0)))
    generator.writeEnd()
    generator.close()
  }
}
