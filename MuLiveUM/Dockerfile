# Build stage
FROM maven:3.9.9 AS build
LABEL authors="setokk"

COPY src /mulive-um-server/src
COPY pom.xml /mulive-um-server

RUN mvn -f /mulive-um-server/pom.xml clean package -DskipTests

# Package stage
FROM openjdk:21-jdk-bullseye
COPY --from=build /mulive-um-server/target/*.jar mulive-um-server.jar

EXPOSE 8080

COPY ../wait-for-it.sh .
RUN chmod +x ./wait-for-it.sh
ENTRYPOINT ["./wait-for-it.sh", "db:5432", "--", "java", "-jar", "mulive-um-server.jar"]