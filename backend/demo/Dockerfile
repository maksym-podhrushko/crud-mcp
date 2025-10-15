# Build stage
FROM eclipse-temurin:21-jdk as builder
WORKDIR /app

# Copy Maven files and download dependencies first for better caching
COPY pom.xml ./
COPY mvnw* ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build the application (skip tests for speed)
RUN ./mvnw clean install -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy built jar
COPY --from=builder /app/target/*.jar app.jar

# Create non-root user
RUN addgroup --system spring && adduser --system --group spring
USER spring:spring

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]