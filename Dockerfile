#
# Build stage
#
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only pom first (better caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests

#
# Package stage
#
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy jar
COPY --from=build /app/target/*.jar app.jar

# Railway uses dynamic port
ENV PORT=8080

EXPOSE 8080

# IMPORTANT: bind to 0.0.0.0 and dynamic port
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]