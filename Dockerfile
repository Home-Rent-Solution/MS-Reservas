FROM maven:3.9.11-eclipse-temurin-25 AS build

WORKDIR /workspace
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests clean package

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
