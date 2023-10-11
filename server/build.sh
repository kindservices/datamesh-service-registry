#!/usr/bin/env bash
export TAG=${TAG:-0.0.1}
export IMG=${IMG:-kindservices/service-registry:$TAG}
export PORT=${PORT:-8080}

# scala-cli --power package --docker App.scala --docker-from openjdk:11 --docker-image-repository service-registry

buildDocker() {
    docker build --tag $IMG .
}

buildLocally() {
    scala-cli --power package Server.scala -o server.jar --force --assembly
}

test() {
  echo "registering foo"
  curl -X POST -d '{"webComponent":{"jsUrl":"path/to/component.js","cssUrl":"path/to/component.css","componentId":"some-component"},"label":"some friendly label","tags":{"env":"prod","createdBy":"somebody"}}' http://localhost:$PORT/api/v1/registry/foo
  curl -X POST -d '{"webComponent":{"jsUrl":"another/server/bundle.js","cssUrl":"bundle.css","componentId":"another-component"},"label":"another label","tags":{"env":"prod"}}' http://localhost:$PORT/api/v1/registry/bar
  echo ""
  echo "getting foo:"
  curl -X GET http://localhost:$PORT/api/v1/registry/foo
  echo ""
  echo "listing result:"
  curl -X GET http://localhost:$PORT/api/v1/registry
}

push() {
    docker push $IMG
}


run() {
    scala-cli Server.scala
}


runDocker() {
    echo "docker run -it --rm -p $PORT:$PORT -d $IMG"
    id=`docker run -it --rm -p $PORT:$PORT -d $IMG`
    cat > kill.sh <<EOL
docker kill $id
# clean up after ourselves
rm kill.sh
EOL
    chmod +x kill.sh

    echo "Running on port $PORT --- stop server using ./kill.sh"
}


# assumes argocd (which argocd || brew install argocd) installed and logged in (argocd login localhost:$ARGO_PORT --username admin --password $MY_ARGO_PWD  --insecure --skip-test-tls )
#
# see 
# https://github.com/easy-being-green/argo-drone/blob/main/argo/argo.sh
#
installArgo() {
    APP=${APP:-service-registry}
    BRANCH=${BRANCH:-`git rev-parse --abbrev-ref HEAD`}

    echo "creating $APP in $BRANCH"

    kubectl create namespace data-mesh || echo "couldn't create data-mesh namespace"
    
    # beast mode :-)
    argocd app create $APP \
    --repo https://github.com/kindservices/idealab-service-registry.git \
    --path server/k8s \
    --dest-server https://kubernetes.default.svc \
    --dest-namespace data-mesh \
    --sync-policy automated \
    --auto-prune \
    --self-heal \
    --revision $BRANCH
}