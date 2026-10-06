# ==========================================
# ETAPA 1: COMPILACIÓN
# ==========================================
FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests


# ==========================================
# ETAPA 2: EJECUCIÓN
# ==========================================
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/discovery-server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]