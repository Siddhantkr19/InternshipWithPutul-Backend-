# Use Maven with JDK 21 as base image
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

## Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Use OpenJDK 21 for runtime
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/sql-admin-auth-0.0.1-SNAPSHOT.jar .



# Expose port 8080
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java", "-jar", "sql-admin-auth-0.0.1-SNAPSHOT.jar"]