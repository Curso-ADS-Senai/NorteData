# O Render não tem Java pronto: este arquivo ensina a compilar e ligar a API.
# Root Directory no Render = Backend (com B maiúsculo: o build roda em Linux,
# onde "backend" e "Backend" são pastas diferentes).

# Etapa 1: compila com Maven e Java 21 (mesma versão do <java.version> do pom).
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

# Etapa 2: imagem leve, só o Java para rodar o .jar.
FROM eclipse-temurin:21-jre
WORKDIR /app
# Glob em vez do nome fixo: o artefato é aula08-0.0.1-SNAPSHOT.jar (artifactId +
# version do pom). O handout usa "cti-provedor-api.jar", que não existe aqui.
COPY --from=build /app/target/*.jar app.jar
# Informativo: quem manda de verdade é a variável PORT do Render, lida pelo
# server.port=${PORT:8080} do application.properties.
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
