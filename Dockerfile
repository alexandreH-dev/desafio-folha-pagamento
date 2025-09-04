# ----------------------------------------------------------------------------------
# Etapa 1: Build (Maven + JDK)
# ----------------------------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ----------------------------------------------------------------------------------
# Etapa 2: Runtime (JRE)
# ----------------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/folha-pagamento-batch-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
