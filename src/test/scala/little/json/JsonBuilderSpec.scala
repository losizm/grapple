package little.json

import org.scalatest.FlatSpec

import Implicits._
import Test._

class JsonBuilderSpec extends FlatSpec {
  val user = User(0, "root", true)

  "JSON builder" should "build array" in {
    val builder = Json.createArrayBuilder()

    builder.add(user)
    builder.addNullable(user)
    builder.addOption(Some(user))
    builder.add(1)
    builder.add(1L)
    builder.add(1.1)
    builder.add(BigInt(1))
    builder.addNullable(BigInt(1))
    builder.addOption(Option(BigInt(1)))
    builder.add(BigDecimal(1.0))
    builder.addNullable(BigDecimal(1.0))
    builder.addOption(Some(BigDecimal(1.0)))
  }

  it should "build object" in {
    val builder = Json.createObjectBuilder()

    builder.add("a", user)
    builder.addNullable("b", user)
    builder.addOption("c", Some(user))
    builder.add("d", 1)
    builder.add("e", 1L)
    builder.add("f", 1.1)
    builder.add("g", BigInt(1))
    builder.addNullable("h", BigInt(1))
    builder.addOption("i", Option(BigInt(1)))
    builder.add("j", BigDecimal(1.0))
    builder.addNullable("k", BigDecimal(1.0))
    builder.addOption("l", Some(BigDecimal(1.0)))
  }
}
