# Use JDK environment for our Docker image
FROM amazoncorretto:21-alpine

# Copy each jar file from respective module into the image
COPY backend-service/build/libs/backend-service-0.0.1-SNAPSHOT.jar       /app/backend.jar
COPY client/build/libs/client-0.0.1-SNAPSHOT.jar                         /app/client.jar
COPY frontend-service/build/libs/frontend-service-0.0.1-SNAPSHOT.jar     /app/frontend.jar
COPY total-calculator/build/libs/total-calculator-0.0.1-SNAPSHOT.jar     /app/total-calculator.jar

# Specify the working directory
WORKDIR /app

# Use an environment variable for the jar filename
ENV APP=frontend

# Add an entrypoint script
ENTRYPOINT java -jar $APP.jar