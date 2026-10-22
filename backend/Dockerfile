FROM maven:3.9.15-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY src src
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests package

FROM docker:28-cli AS docker

FROM eclipse-temurin:21-jre
COPY --from=docker /usr/local/bin/docker /usr/local/bin/docker
WORKDIR /app
COPY --from=build /build/target/coding-agent-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
