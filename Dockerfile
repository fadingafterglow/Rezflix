FROM maven:3.9.9-eclipse-temurin-21
COPY target/rezflix.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]