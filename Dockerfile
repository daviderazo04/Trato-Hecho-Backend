    FROM eclipse-temurin:17-jdk-jammy
    WORKDIR /app
    COPY . .
    RUN chmod +x gradlew
    # Construir saltando tests
    RUN ./gradlew clean bootJar -x test

    # Exponer el puerto
    EXPOSE 8080

    # Ajusta el nombre del jar si cambia la versión en build.gradle
    CMD ["java", "-jar", "build/libs/backend-trato-hecho-0.0.1-SNAPSHOT.jar"]
