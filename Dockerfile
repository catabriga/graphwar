FROM eclipse-temurin:8-jdk AS builder
WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle

COPY . .

RUN chmod +x ./gradlew \
    && ./gradlew clean globalServerShadow --no-daemon || true

FROM eclipse-temurin:8-jre
WORKDIR /app

COPY --from=builder /app/build/libs/globalServerAll.jar /app/globalServerAll.jar

ENTRYPOINT ["java","-jar","/app/globalServerAll.jar"]
