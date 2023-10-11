FROM virtuslab/scala-cli:latest AS build
RUN mkdir /app
COPY ["Server.scala", "/app/"]
WORKDIR /app
RUN scala-cli --power package Server.scala -o server.jar --assembly

FROM openjdk:18-oraclelinux8
RUN mkdir /app
COPY --from=build /app/* /app
WORKDIR /app
EXPOSE 8080
ENV PORT=8080
CMD java -jar server.jar