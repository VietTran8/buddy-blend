FROM eclipse-temurin:21-jre-alpine
COPY target/group*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]