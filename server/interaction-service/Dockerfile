FROM eclipse-temurin:21-jre-alpine
COPY target/interaction*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]