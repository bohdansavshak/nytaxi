# PowerShell script

# Navigate to your project directory if needed
# Set-Location -Path "Path\to\your\project\directory"

# Execute Gradle assemble task
Invoke-Expression "./gradlew clean assemble"

# Build the Docker image
Invoke-Expression "docker build -t lari245/first-promotion-image:latest ."

# Push the Docker image to the Docker repository
Invoke-Expression "docker image push lari245/first-promotion-image:latest"