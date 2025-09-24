FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY . /app
RUN ./gradlew clean fatJar -x test --no-daemon
EXPOSE 8080
CMD ["java", "-jar", "build/libs/romantic-app-all.jar"]
