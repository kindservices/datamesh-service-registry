FROM virtuslab/scala-cli:latest AS build
RUN mkdir /app
COPY ["Client.scala", "/app/"]

WORKDIR /app
RUN scala-cli --power package Client.scala -o client.jar -f --assembly --main-class heartbeat

FROM openjdk:18-oraclelinux8
RUN mkdir /app
COPY --from=build /app/* /app
WORKDIR /app

# use -cp rather than -jar as there are multiple main entry points
# see Client.scala comment for how to run
CMD java -jar client.jar 