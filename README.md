# Link to Project Promotion Plan
https://docs.google.com/document/d/1I-NU_J8tkkjC_YEnKh7cH7VKzgorLiq8W_BJwD99AIE/edit?usp=sharing

# API Documentation

## Swagger UI

This project includes Swagger UI for easy visualization and exploration of the API. Swagger UI allows you to interact with the API documentation in a user-friendly way.

To access Swagger UI, open your web browser and navigate to the following path:
[/webjars/swagger-ui/index.html](/webjars/swagger-ui/index.html)



This will launch Swagger UI, where you can explore the available API endpoints, view request and response examples, and test API calls directly from the documentation.

## OpenAPI Specification

The OpenAPI Specification (OAS) is provided to offer a comprehensive view of the API's structure and functionality. The raw OpenAPI JSON can be accessed at:
[/v3/api-docs](/v3/api-docs)

You can use this OpenAPI Specification to generate client libraries, server stubs, and more. This can be particularly useful for automating the integration process.


# How to Start the Application Locally

## Prerequisites

1. Install [Docker Desktop](https://www.docker.com/products/docker-desktop)
2. Install [Minikube](https://minikube.sigs.k8s.io/docs/start/)

## Steps to Follow

1. Start Minikube:

    ```
    minikube start
    ```

2. Install Kafka. Follow the quickstart guide here: [https://strimzi.io/quickstarts/](https://strimzi.io/quickstarts/)
   ```
   kubectl create namespace kafka
   kubectl create -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka
   kubectl apply -f https://strimzi.io/examples/latest/kafka/kafka-persistent-single.yaml -n kafka 
   
   kubectl -n kafka delete $(kubectl get strimzi -o name -n kafka)
   kubectl -n kafka delete -f 'https://strimzi.io/install/latest?namespace=kafka'
   ```


3. Create env variables with credentials for docker hub. 
  ``` 
   DOCKER_USERNAME
   DOCKER_PASSWORD
   DOCKER_EMAIL
  ```

4. Create the necessary Kubernetes resources:
    ```
    .\create-resources-in-kubernetes.ps1
    ```

4. Run the following command to see which port is exposed for minikube
    ```
    docker port minikube
    ```



