FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring
RUN apk add --no-cache curl

WORKDIR /app

# Copia o JAR que foi buildado localmente
COPY shipping/target/*.jar app.jar

RUN chown -R spring:spring /app
USER spring:spring

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]