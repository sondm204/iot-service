FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

RUN addgroup --system spring && adduser --system --ingroup spring spring

COPY --from=build /workspace/build/libs/*.jar app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
