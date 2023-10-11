//> using scala "3.3.1"
//> using lib "com.lihaoyi::requests:0.8.0"

def env(key: String) = sys.env.getOrElse(
  key,
  sys.error(
    s"$key env variable not set: ${sys.env.mkString("\n", "\n", "\n")}\n properties:\n ${sys.props.mkString("\n")}"
  )
)
@main def register(
    id: String = env("ID"),
    body: String = env("BODY"),
    hostPort: String = env("HOSTPORT")
) = {
  val url      = s"$hostPort/api/v1/registry/$id"
  val response = requests.post(url, data = body)
  println(response)
  response.ensuring(_.statusCode == 200, s"$url returned ${response.statusCode}: $response")
}

/** register at a fixed rate
  */
@main def heartbeat = {
  val id: String              = env("ID")
  val body: String            = env("BODY")
  val hostPort: String        = env("HOSTPORT")
  val frequencyInSeconds: Int = env("FREQUENCY_IN_SECONDS").toInt
  while (true) {
    register(id, body, hostPort)
    Thread.sleep(frequencyInSeconds * 1000)
  }
}

@main def get(id: String = env("ID"), hostPort: String = env("HOSTPORT")) = {
  val url      = s"$hostPort/api/v1/registry/$id"
  val response = requests.get(url)
  println(response.text())
  response.ensuring(_.statusCode == 200, s"$url returned ${response.statusCode}: $response")
}

@main def list(hostPort: String = env("HOSTPORT")) = {
  val url      = s"$hostPort/api/v1/registry"
  val response = requests.get(url)
  println(response.text())
  response.ensuring(_.statusCode == 200, s"$url returned ${response.statusCode}: $response")
}
