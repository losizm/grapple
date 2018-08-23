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
    builder.add(Seq(user, user, user))
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
    builder.add("d", Seq(user, user, user))
    builder.add("e", 1)
    builder.add("f", 1L)
    builder.add("g", 1.1)
    builder.add("h", BigInt(1))
    builder.addNullable("i", BigInt(1))
    builder.addOption("j", Option(BigInt(1)))
    builder.add("k", BigDecimal(1.0))
    builder.addNullable("l", BigDecimal(1.0))
    builder.addOption("m", Some(BigDecimal(1.0)))
  }
}
