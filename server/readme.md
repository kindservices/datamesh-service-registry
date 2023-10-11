# About

A noddy service-registry - a means to register and look-up services.

See the [Makefile](./Makefile) for build targets.e.g:

```
make run

make test
```

or, just using scala-cli:

run with `scala-cli Server.scala`

or package and run locally with: 
```
which scala-cli || brew install Virtuslab/scala-cli/scala-cli
scala-cli package Server.scala -o app.jar -f --assembly
java -jar app.jar
```

# IDE setup
use `scala-cli setup-ide`