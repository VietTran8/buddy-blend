FROM eclipse-temurin:21-jre-alpine
COPY target/moderation*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]