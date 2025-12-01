# ---- Build ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY pom.xml . 
COPY mvnw . 
COPY .mvn .mvn

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw clean package -DskipTests

# ---- Run ----
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Usar el puerto asignado por Render
ENV PORT=8080
EXPOSE ${PORT}

ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]
