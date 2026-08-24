# ---- Etapa 1: build del artefacto (Maven + JDK 21) ----

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Las dependencias se descargan antes de copiar el código fuente
# para aprovechar la caché de capas de Docker

COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src

# Los tests se ejecutan en el entorno de desarrollo/CI (requieren Docker para
# Testcontainers), no dentro del build de imagen

RUN mvn -q package -DskipTests

# ---- Etapa 2: extracción de las capas del jar (optimiza caché y tamaño) ----

FROM eclipse-temurin:21-jre AS extract
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# ---- Etapa 3: imagen de runtime (solo JRE, sin Maven ni fuentes) ----

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=extract /app/dependencies/ ./
COPY --from=extract /app/spring-boot-loader/ ./
COPY --from=extract /app/snapshot-dependencies/ ./
COPY --from=extract /app/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
