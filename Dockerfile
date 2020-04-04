FROM maven:latest AS builder

WORKDIR /app

COPY ./src/ /app/src
COPY pom.xml /app/pom.xml 

RUN mvn package


FROM openjdk:11.0-jre

WORKDIR /app
COPY --from=builder /app/target/AnnouncerBot-*-jar-with-dependencies.jar /app/announcerbot.jar

CMD ["java", "-jar", "/app/announcerbot.jar"]
