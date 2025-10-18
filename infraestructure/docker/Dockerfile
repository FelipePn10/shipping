# ============================================
# 🏗️ STAGE 1: Build
# ============================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copiar pom.xml primeiro (cache layer)
COPY pom.xml .

# Baixar dependências (permite falha parcial)
RUN mvn dependency:go-offline -B || true

# Copiar código fonte
COPY src ./src

# Build da aplicação
RUN mvn clean package -DskipTests -B

# ============================================
# 🚀 STAGE 2: Runtime (Production-ready)
# ============================================
FROM eclipse-temurin:21-jre-alpine

# Metadados
LABEL maintainer="Redirex Team"
LABEL version="1.0"
LABEL description="Redirex Shipping Backend"

# Criar usuário não-root
RUN addgroup -S spring && adduser -S spring -G spring

# Instalar dependências
RUN apk add --no-cache curl tzdata && rm -rf /var/cache/apk/*

# Configurar timezone Brasil
ENV TZ=America/Sao_Paulo
RUN cp /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app

# Copiar JAR do stage anterior
COPY --from=builder /build/target/*.jar app.jar

# Permissões
RUN chown -R spring:spring /app
USER spring:spring

# Configurações JVM
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ENV SPRING_PROFILES_ACTIVE=dev

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]