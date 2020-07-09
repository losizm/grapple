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
package little.json.rpc

import javax.json.JsonObject

import little.json.{ Json, JsonInput, JsonOutput }
import little.json.Implicits._

import org.scalatest.FlatSpec

class JsonRpcErrorSpec extends FlatSpec {
  case class Data(name: String, value: String)

  implicit val dataInput: JsonInput[Data] = {
    case value: JsonObject => Data(
      value.getString("name"),
      value.getString("value")
    )
  }

  implicit val dataOutput: JsonOutput[Data] =
    data => Json.obj(
      "name"  -> data.name,
      "value" -> data.value
    )

  it should "create JsonRpcError" in {
    val err1 = JsonRpcError(100, "Error")
    assert(err1.code == 100)
    assert(err1.message == "Error")
    assert(err1.data.isEmpty)

    err1 match {
      case JsonRpcError(code, message, data) =>
        assert(code == 100)
        assert(message == "Error")
        assert(data.isEmpty)
    }

    val err2 = JsonRpcError(200, "Error", "More information")
    assert(err2.code == 200)
    assert(err2.message == "Error")
    assert(err2.data.exists(_.as[String] == "More information"))

    err2 match {
      case JsonRpcError(code, message, Some(data)) =>
        assert(code == 200)
        assert(message == "Error")
        assert(data.as[String] == "More information")
    }

    val err3 = JsonRpcError(300, "Error", Data("Severe", "Unknown"))
    assert(err3.code == 300)
    assert(err3.message == "Error")
    assert(err3.data.exists(_.as[Data] == Data("Severe", "Unknown")))

    err3 match {
      case JsonRpcError(code, message, Some(data)) =>
        assert(code == 300)
        assert(message == "Error")
        assert(data.as[Data] == Data("Severe", "Unknown"))
    }
  }

  it should "create ParseError" in {
    val err1 = ParseError("Parse error")
    assert(err1.code == -32700)
    assert(err1.message == "Parse error")
    assert(err1.data.isEmpty)

    err1 match {
      case ParseError(code, message, data) =>
        assert(code == -32700)
        assert(message == "Parse error")
        assert(data.isEmpty)
    }

    val err2 = ParseError("Parse error", "More information")
    assert(err2.code == -32700)
    assert(err2.message == "Parse error")
    assert(err2.data.exists(_.as[String] == "More information"))

    err2 match {
      case ParseError(code, message, Some(data)) =>
        assert(code == -32700)
        assert(message == "Parse error")
        assert(data.as[String] == "More information")
    }
  }

  it should "create InvalidRequest" in {
    val err1 = InvalidRequest("InvalidRequest")
    assert(err1.code == -32600)
    assert(err1.message == "InvalidRequest")
    assert(err1.data.isEmpty)

    err1 match {
      case InvalidRequest(code, message, data) =>
        assert(code == -32600)
        assert(message == "InvalidRequest")
        assert(data.isEmpty)
    }

    val err2 = InvalidRequest("InvalidRequest", "More information")
    assert(err2.code == -32600)
    assert(err2.message == "InvalidRequest")
    assert(err2.data.exists(_.as[String] == "More information"))

    err2 match {
      case InvalidRequest(code, message, Some(data)) =>
        assert(code == -32600)
        assert(message == "InvalidRequest")
        assert(data.as[String] == "More information")
    }

    val err3 = InvalidRequest("Error", Data("Severe", "Unknown"))
    assert(err3.code == -32600)
    assert(err3.message == "Error")
    assert(err3.data.exists(_.as[Data] == Data("Severe", "Unknown")))

    err3 match {
      case InvalidRequest(code, message, Some(data)) =>
        assert(code == -32600)
        assert(message == "Error")
        assert(data.as[Data] == Data("Severe", "Unknown"))
    }
  }

  it should "create MethodNotFound" in {
    val err1 = MethodNotFound("MethodNotFound")
    assert(err1.code == -32601)
    assert(err1.message == "MethodNotFound")
    assert(err1.data.isEmpty)

    err1 match {
      case MethodNotFound(code, message, data) =>
        assert(code == -32601)
        assert(message == "MethodNotFound")
        assert(data.isEmpty)
    }

    val err2 = MethodNotFound("MethodNotFound", "More information")
    assert(err2.code == -32601)
    assert(err2.message == "MethodNotFound")
    assert(err2.data.exists(_.as[String] == "More information"))

    err2 match {
      case MethodNotFound(code, message, Some(data)) =>
        assert(code == -32601)
        assert(message == "MethodNotFound")
        assert(data.as[String] == "More information")
    }

    val err3 = MethodNotFound("Error", Data("Severe", "Unknown"))
    assert(err3.code == -32601)
    assert(err3.message == "Error")
    assert(err3.data.exists(_.as[Data] == Data("Severe", "Unknown")))

    err3 match {
      case MethodNotFound(code, message, Some(data)) =>
        assert(code == -32601)
        assert(message == "Error")
        assert(data.as[Data] == Data("Severe", "Unknown"))
    }
  }

  it should "create InvalidParams" in {
    val err1 = InvalidParams("InvalidParams")
    assert(err1.code == -32602)
    assert(err1.message == "InvalidParams")
    assert(err1.data.isEmpty)

    err1 match {
      case InvalidParams(code, message, data) =>
        assert(code == -32602)
        assert(message == "InvalidParams")
        assert(data.isEmpty)
    }

    val err2 = InvalidParams("InvalidParams", "More information")
    assert(err2.code == -32602)
    assert(err2.message == "InvalidParams")
    assert(err2.data.exists(_.as[String] == "More information"))

    err2 match {
      case InvalidParams(code, message, Some(data)) =>
        assert(code == -32602)
        assert(message == "InvalidParams")
        assert(data.as[String] == "More information")
    }

    val err3 = InvalidParams("Error", Data("Severe", "Unknown"))
    assert(err3.code == -32602)
    assert(err3.message == "Error")
    assert(err3.data.exists(_.as[Data] == Data("Severe", "Unknown")))

    err3 match {
      case InvalidParams(code, message, Some(data)) =>
        assert(code == -32602)
        assert(message == "Error")
        assert(data.as[Data] == Data("Severe", "Unknown"))
    }
  }

  it should "create InternalError" in {
    val err1 = InternalError("InternalError")
    assert(err1.code == -32603)
    assert(err1.message == "InternalError")
    assert(err1.data.isEmpty)

    err1 match {
      case InternalError(code, message, data) =>
        assert(code == -32603)
        assert(message == "InternalError")
        assert(data.isEmpty)
    }

    val err2 = InternalError("InternalError", "More information")
    assert(err2.code == -32603)
    assert(err2.message == "InternalError")
    assert(err2.data.exists(_.as[String] == "More information"))

    err2 match {
      case InternalError(code, message, Some(data)) =>
        assert(code == -32603)
        assert(message == "InternalError")
        assert(data.as[String] == "More information")
    }

    val err3 = InternalError("Error", Data("Severe", "Unknown"))
    assert(err3.code == -32603)
    assert(err3.message == "Error")
    assert(err3.data.exists(_.as[Data] == Data("Severe", "Unknown")))

    err3 match {
      case InternalError(code, message, Some(data)) =>
        assert(code == -32603)
        assert(message == "Error")
        assert(data.as[Data] == Data("Severe", "Unknown"))
    }
  }
}
