#!/usr/bin/env bash
export TAG=${TAG:-local}
export IMG=${IMG:-"kindservices/service-registry-client:$TAG"}

# scala-cli --power package --docker App.scala --docker-from openjdk:11 --docker-image-repository service-registry

build() {
    docker build --tag $IMG .
}

buildLocally() {
    scala-cli --power package Client.scala -o client.jar --assembly --main-class heartbeat
}

testRegisterLocal() {
    scala-cli Client.scala --main-class registerArgs -- testOne '{"webComponent":{"jsUrl":"path/to/component.js","cssUrl":"path/to/component.css","componentId":"some-component"},"label":"some friendly label","tags":{"env":"prod","createdBy":"somebody"}}' http://localhost:8080
    # scala-cli Client.scala --main-class register -- testOne '{"webComponent":{"jsUrl":"path/to/component.js","cssUrl":"path/to/component.css","componentId":"some-component"},"label":"some friendly label","tags":{"env":"prod","createdBy":"somebody"}}' http://localhost:8080
}

testHeartbeatLocal() {
    scala-cli Client.scala --main-class heartbeatArgs -- testHeartbeat '{"webComponent":{"jsUrl":"path/to/component.js","cssUrl":"path/to/component.css","componentId":"some-component"},"label":"some friendly label","tags":{"env":"prod","createdBy":"somebody"}}' http://localhost:8080 2
}

run() {
    docker run --rm -env HOST --env ID --env BODY -d $IMG
}