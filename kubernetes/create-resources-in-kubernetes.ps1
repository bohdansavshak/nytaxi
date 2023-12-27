# PowerShell
$ErrorActionPreference = "Stop" # This will make the script stop on the first error
try {
    # create custom namespace
    Write-Output "create custom namespace"
    kubectl create namespace bohdansavshak

    # set it as default
    Write-Output "set namepsace as defualt"
    kubectl config set-context --current --namespace=bohdansavshak

    # create secret with docker hub credentials
    Write-Output "Creating secrets for pods to be able to access dockerhub"
    kubectl create secret docker-registry promotion --docker-server=https://index.docker.io/v1/ --docker-username=$env:DOCKER_USERNAME --docker-password=$env:DOCKER_PASSWORD --docker-email=$env:DOCKER_EMAIL --namespace=bohdansavshak

    #create configmap with
    Write-Output "Creating ConfigMap..."
    kubectl create configmap postgres-config --from-file=create-schema-init.sql --namespace bohdansavshak

    # create secret with password for postgres
    Write-Output "Creating Secret..."
    kubectl create secret generic postgres-secret --from-literal=postgresql-password=mysecretpassword --namespace bohdansavshak

    # create postgres
    Write-Output "Applying StatefulSet..."
    kubectl apply -f .\postgres-statefulset.yml

    # create redis
    Write-Output "Applying backend deployment..."
    kubectl apply -f .\redis-statefulset.yml

    # Apply the backend deployment
    Write-Output "Applying backend deployment..."
    kubectl apply -f .\backend-deployment.yml

    # Apply the frontend deployment
    Write-Output "Applying frontend deployment..."
    kubectl apply -f .\frontend-deployment.yml

    # Apply the total-calculator cronjob
    Write-Output "Applying total-calculator cronjob..."
    kubectl apply -f .\total-calculator-cronjob.yml

    Write-Output "Done!"
} catch {
    Write-Output $_.Exception.Message
    exit 1
}