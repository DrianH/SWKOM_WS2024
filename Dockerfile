# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file to the container
COPY target/SWKOM2024-0.0.1-SNAPSHOT.jar app.jar

RUN mkdir -p /uploads

# Expose the port the app runs on (default Spring Boot port)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]