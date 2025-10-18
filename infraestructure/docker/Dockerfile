FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

COPY shipping/pom.xml .

RUN mvn dependency:go-offline -B

COPY shipping/src ./src

RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring
RUN apk add --no-cache curl

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

RUN chown -R spring:spring /app
USER spring:spring

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]