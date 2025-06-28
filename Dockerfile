FROM maven:3.9.5 AS build-java
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build-java /app/target/NaumenLocalChat-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 5001
ENTRYPOINT ["java", "-jar", "app.jar"]
