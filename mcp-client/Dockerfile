FROM eclipse-temurin:21-jdk as mcp-server-build
WORKDIR /mcp-server
COPY mcp-server/pom.xml .
COPY mcp-server/mvnw* ./
COPY mcp-server/.mvn .mvn
RUN ./mvnw dependency:go-offline -DskipTests
COPY mcp-server/src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jdk as builder
WORKDIR /app
COPY mcp-client/pom.xml .
COPY mcp-client/mvnw* ./
COPY mcp-client/.mvn .mvn
RUN ./mvnw dependency:go-offline -DskipTests
COPY mcp-client/src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY --from=mcp-server-build /mcp-server/target/mcp-server-0.0.1-SNAPSHOT.jar /workspace/mcp-server/target/mcp-server-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "app.jar"]