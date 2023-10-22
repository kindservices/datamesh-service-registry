//> using scala "3.3.1"
//> using lib "com.lihaoyi::cask:0.9.1"
//> using lib "com.lihaoyi::upickle:3.0.0"

import java.time.format.*
import scala.util.*
import Properties.*
import java.time.*

import upickle.*
import upickle.default.*
import upickle.default.{ReadWriter => RW, macroRW}

// format: off
/**
 * Moving to contract-first would probably be the first tech debt to pay down,
 * but for now, the response for 
 * 
 * GET /api/v1/registry is:
 * 
 * {{{
 * [
    {
        "id": "testOne",
        "service": {
            "webComponent": {
                "jsUrl": "path/to/component.js",
                "cssUrl": "path/to/component.css",
                "componentId": "some-component"
            },
            "label": "some friendly label",
            "tags": {
                "env": "prod",
                "createdBy": "somebody"
            }
        },
        "lastUpdated": "2023-10-21T20:53:53.893639Z"
    },
    {
        "id": "testHeartbeat",
        "service": {
            "webComponent": {
                "jsUrl": "path/to/component.js",
                "cssUrl": "path/to/component.css",
                "componentId": "some-component"
            },
            "label": "some friendly label",
            "tags": {
                "env": "prod",
                "createdBy": "somebody"
            }
        },
        "lastUpdated": "2023-10-21T20:53:58.565700Z"
    }
]
 * }}}
 * 
 * 
 */
// format: on
object model {

  /** @param jsUrl
    *   The URL where to load the web-component's javascript
    * @param cssUrl
    *   The URL where to load the css web-component's css
    * @param componentId
    *   the web component id
    */
  case class WebComponent(jsUrl: String, cssUrl: String, componentId: String)
  object WebComponent {
    given rw: RW[WebComponent] = macroRW
    def example = WebComponent("path/to/component.js", "path/to/component.css", "comp-onent")

  }

  given ReadWriter[ZonedDateTime] = readwriter[String].bimap[ZonedDateTime](
    zonedDateTime => DateTimeFormatter.ISO_INSTANT.format(zonedDateTime),
    str => ZonedDateTime.parse(str, DateTimeFormatter.ISO_INSTANT)
  )

  /** The register request body */
  case class Register(
      webComponent: WebComponent,
      label: String,
      tags: Map[String, String] = Map.empty
  )
  object Register {
    given rw: RW[Register] = macroRW

    def example = Register(
      WebComponent.example,
      "some friendly label",
      Map("env" -> "prod", "createdBy" -> "somebody")
    )
  }

  case class Service(service: Register, lastUpdated: ZonedDateTime = ZonedDateTime.now())
  object Service {
    given rw: RW[Service] = macroRW
  }
}

object App extends cask.MainRoutes {

  import model.{given, *}
  private var serviceById = Map[String, Service]()

  def msg(text: String) = writeJs(Map("message" -> text))

  def reply(body: ujson.Value = ujson.Null, statusCode: Int = 200) = cask.Response(
    data = body,
    statusCode = statusCode,
    headers = Seq("Access-Control-Allow-Origin" -> "*", "Content-Type" -> "application/json")
  )

  @cask.get("/")
  def getRoot() = s"""GET /api/v1/registry/:id
                     |POST /api/v1/registry/:id""".stripMargin

  @cask.post("/api/v1/registry/:id")
  def register(id: String, request: cask.Request) = {
    val body = read[Register](request.text())
    serviceById = serviceById.updated(id, Service(body, ZonedDateTime.now()))
    write(body)
  }

  @cask.getJson("/api/v1/registry")
  def list() = {
    val asList = serviceById.map { case (id, service) =>
      ujson.Obj(
        "id"          -> writeJs(id),
        "service"     -> writeJs(service.service),
        "lastUpdated" -> writeJs(service.lastUpdated)
      )
    }
    reply(ujson.Arr.from(asList))
  }

  @cask.get("/api/v1/registry/:id")
  def get(id: String) = serviceById
    .get(id)
    .map(x => reply(writeJs(x)))
    .getOrElse(reply(msg("Not Found"), statusCode = 404))

  @cask.get("/health")
  def getHealthCheck() = s"${ZonedDateTime.now(ZoneId.of("UTC"))}"

  override def host: String = "0.0.0.0"
  override def port         = envOrElse("PORT", propOrElse("PORT", 8080.toString)).toInt

  initialize()

  println(s""" 🚀 running on $host:$port {verbose : $verbose, debugMode : $debugMode }  🚀""")
}
