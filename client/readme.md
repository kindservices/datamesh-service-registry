# About

A client for our noddy service registry


## Running Locally
List components:
args are <hostport>
```
scala-cli Client.scala --main-class list -- http://localhost:8080
```

### Get a component:
args are <id> <hostport>
```
scala-cli Client.scala --main-class get -- foo http://localhost:8080
```

or by building the assembly:

```
which scala-cli || brew install Virtuslab/scala-cli/scala-cli
scala-cli package Client.scala -o client.jar -f --assembly
java -jar client.jar
```


See the [Makefile](./Makefile) for build targets.e.g:

```
make run

make test
```

You could run the image locally using:

```
docker run \
 -e ID=foo \
 -e HOSTPORT=http://localhost:8080 \
 -e BODY='{"webComponent":{"jsUrl":"path/to/component.js","cssUrl":"path/to/component.css","componentId":"some-component"},"label":"some friendly label","tags":{"env":"prod","createdBy":"somebody"}}' \
 -e FREQUENCY_IN_SECONDS=1 \
 --rm \
 --name client-test \
 --network host \
 kindservices/service-registry-client:0.0.2
 ```