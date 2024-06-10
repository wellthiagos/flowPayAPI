FROM openjdk:17-alpine

WORKDIR /app

COPY target/*.jar flowPayAPI.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "flowPayAPI.jar"]