FROM eclipse-temurin:21-jre-alpine
COPY target/user*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]