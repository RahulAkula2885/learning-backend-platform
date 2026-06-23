FROM eclipse-temurin:26_35-jdk
WORKDIR /app
COPY target/learning-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
