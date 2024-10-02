# Use Maven to build the project with JDK 17
FROM maven:3.8.1-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Use an official OpenJDK runtime as a parent image with JDK 17
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copy the JAR file from the target directory
COPY --from=build /app/target/chal-0.0.1-SNAPSHOT.jar app.jar

# Copy the sample.txt file into the /tmp directory in the container
COPY src/main/resources/static/ /app/static

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]