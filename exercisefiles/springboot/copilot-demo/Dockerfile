# 11. Containerize the application
  #Use the Dockerfile provided to create a docker image of the application. There are some comments in the Dockerfile that will help you to complete the exercise.
  #
  #In order to build, run and test the docker image, you can use Copilot as well to generate the commands.
  #
  #For instance, create a DOCKER.md file where you can store the commands to build, run and test the docker image. You will notice that Copilot will also help you to document your project and commands.
  #
  #Examples of steps to document: Build the container image, Run the container, Test the container.
# Build a java application image based on openjdk 17 and run it on port 8080

# Use an OpenJDK 17 base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the jar file to the container
# Replace 'copilot-demo.jar' with the actual name of your jar file
COPY target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
