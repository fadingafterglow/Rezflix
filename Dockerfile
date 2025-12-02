FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache ffmpeg

COPY target/rezflix.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]