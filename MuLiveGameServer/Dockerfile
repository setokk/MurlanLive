# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build
LABEL authors="setokk"

COPY src /mulive-game-server/src
COPY pom.xml /mulive-game-server

RUN mvn -f /mulive-game-server/pom.xml package -DskipTests

# Package stage
FROM eclipse-temurin:21
COPY --from=build /mulive-game-server/target/live-1.0.jar mulive-game-server.jar

EXPOSE 45600

COPY ../wait-for-it.sh .
RUN chmod +x ./wait-for-it.sh
ENTRYPOINT ["./wait-for-it.sh", "umserver:8080", "--", "java", "-jar", "mulive-game-server.jar"]