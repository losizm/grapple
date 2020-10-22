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

class JsonRpcErrorSpec extends org.scalatest.flatspec.AnyFlatSpec {
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
    assert(!err1.isParseError)
    assert(!err1.isInvalidRequest)
    assert(!err1.isMethodNotFound)
    assert(!err1.isInvalidParams)
    assert(!err1.isInternalError)

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

    val err4 = JsonRpcError(-32700, "Error")
    assert(err4.code == -32700)
    assert(err4.message == "Error")
    assert(err4.data.isEmpty)
    assert(err4.isParseError)
    assert(!err4.isInvalidRequest)
    assert(!err4.isMethodNotFound)
    assert(!err4.isInvalidParams)
    assert(!err4.isInternalError)
  }

  it should "create ParseError" in {
    val err1 = ParseError()
    assert(err1.code == -32700)
    assert(err1.message == "Parse error")
    assert(err1.data.isEmpty)
    assert(err1.isParseError)
    assert(!err1.isInvalidRequest)
    assert(!err1.isMethodNotFound)
    assert(!err1.isInvalidParams)
    assert(!err1.isInternalError)

    err1 match {
      case ParseError(code, message, data) =>
        assert(code == -32700)
        assert(message == "Parse error")
        assert(data.isEmpty)
    }

    val err2 = ParseError("More information")
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
    val err1 = InvalidRequest()
    assert(err1.code == -32600)
    assert(err1.message == "Invalid request")
    assert(err1.data.isEmpty)
    assert(!err1.isParseError)
    assert(err1.isInvalidRequest)
    assert(!err1.isMethodNotFound)
    assert(!err1.isInvalidParams)
    assert(!err1.isInternalError)

    err1 match {
      case InvalidRequest(code, message, data) =>
        assert(code == -32600)
        assert(message == "Invalid request")
        assert(data.isEmpty)
    }

    val err2 = InvalidRequest("More information")
    assert(err2.code == -32600)
    assert(err2.message == "Invalid request")
    assert(err2.data.exists(_.as[String] == "More information"))

    err2 match {
      case InvalidRequest(code, message, Some(data)) =>
        assert(code == -32600)
        assert(message == "Invalid request")
        assert(data.as[String] == "More information")
    }

    val err3 = InvalidRequest(Data("Severe", "Unknown"))
    assert(err3.code == -32600)
    assert(err3.message == "Invalid request")
    assert(err3.data.exists(_.as[Data] == Data("Severe", "Unknown")))

    err3 match {
      case InvalidRequest(code, message, Some(data)) =>
        assert(code == -32600)
        assert(message == "Invalid request")
        assert(data.as[Data] == Data("Severe", "Unknown"))
    }
  }

  it should "create MethodNotFound" in {
    val err1 = MethodNotFound()
    assert(err1.code == -32601)
    assert(err1.message == "Method not found")
    assert(err1.data.isEmpty)
    assert(!err1.isParseError)
    assert(!err1.isInvalidRequest)
    assert(err1.isMethodNotFound)
    assert(!err1.isInvalidParams)
    assert(!err1.isInternalError)

    err1 match {
      case MethodNotFound(code, message, data) =>
        assert(code == -32601)
        assert(message == "Method not found")
        assert(data.isEmpty)
    }

    val err2 = MethodNotFound("More information")
    assert(err2.code == -32601)
    assert(err2.message == "Method not found")
    assert(err2.data.exists(_.as[String] == "More information"))

    err2 match {
      case MethodNotFound(code, message, Some(data)) =>
        assert(code == -32601)
        assert(message == "Method not found")
        assert(data.as[String] == "More information")
    }

    val err3 = MethodNotFound(Data("Severe", "Unknown"))
    assert(err3.code == -32601)
    assert(err3.message == "Method not found")
    assert(err3.data.exists(_.as[Data] == Data("Severe", "Unknown")))

    err3 match {
      case MethodNotFound(code, message, Some(data)) =>
        assert(code == -32601)
        assert(message == "Method not found")
        assert(data.as[Data] == Data("Severe", "Unknown"))
    }
  }

  it should "create InvalidParams" in {
    val err1 = InvalidParams()
    assert(err1.code == -32602)
    assert(err1.message == "Invalid params")
    assert(err1.data.isEmpty)
    assert(!err1.isParseError)
    assert(!err1.isInvalidRequest)
    assert(!err1.isMethodNotFound)
    assert(err1.isInvalidParams)
    assert(!err1.isInternalError)

    err1 match {
      case InvalidParams(code, message, data) =>
        assert(code == -32602)
        assert(message == "Invalid params")
        assert(data.isEmpty)
    }

    val err2 = InvalidParams("More information")
    assert(err2.code == -32602)
    assert(err2.message == "Invalid params")
    assert(err2.data.exists(_.as[String] == "More information"))

    err2 match {
      case InvalidParams(code, message, Some(data)) =>
        assert(code == -32602)
        assert(message == "Invalid params")
        assert(data.as[String] == "More information")
    }

    val err3 = InvalidParams(Data("Severe", "Unknown"))
    assert(err3.code == -32602)
    assert(err3.message == "Invalid params")
    assert(err3.data.exists(_.as[Data] == Data("Severe", "Unknown")))

    err3 match {
      case InvalidParams(code, message, Some(data)) =>
        assert(code == -32602)
        assert(message == "Invalid params")
        assert(data.as[Data] == Data("Severe", "Unknown"))
    }
  }

  it should "create InternalError" in {
    val err1 = InternalError()
    assert(err1.code == -32603)
    assert(err1.message == "Internal error")
    assert(err1.data.isEmpty)
    assert(!err1.isParseError)
    assert(!err1.isInvalidRequest)
    assert(!err1.isMethodNotFound)
    assert(!err1.isInvalidParams)
    assert(err1.isInternalError)

    err1 match {
      case InternalError(code, message, data) =>
        assert(code == -32603)
        assert(message == "Internal error")
        assert(data.isEmpty)
    }

    val err2 = InternalError("More information")
    assert(err2.code == -32603)
    assert(err2.message == "Internal error")
    assert(err2.data.exists(_.as[String] == "More information"))

    err2 match {
      case InternalError(code, message, Some(data)) =>
        assert(code == -32603)
        assert(message == "Internal error")
        assert(data.as[String] == "More information")
    }

    val err3 = InternalError(Data("Severe", "Unknown"))
    assert(err3.code == -32603)
    assert(err3.message == "Internal error")
    assert(err3.data.exists(_.as[Data] == Data("Severe", "Unknown")))

    err3 match {
      case InternalError(code, message, Some(data)) =>
        assert(code == -32603)
        assert(message == "Internal error")
        assert(data.as[Data] == Data("Severe", "Unknown"))
    }
  }
}
