FROM eclipse-temurin:22-jdk
WORKDIR /app
COPY target/Wallets_ITK-1.0-SNAPSHOT.jar app.jar
COPY .env.docker .env.docker
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["java", "-jar", "app.jar"]