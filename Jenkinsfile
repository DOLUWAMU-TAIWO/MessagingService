pipeline {
    agent any
    stages {
        stage('Check Out') {
            steps {
                git url: 'https://github.com/DOLUWAMU-TAIWO/MessagingService.git', branch: 'main'
            }
        }
        stage('Docker Login') {
            steps {
                // Securely inject credentials stored with ID 'DOCKERHUB_CREDENTIALS'
                withCredentials([usernamePassword(credentialsId: 'DOCKERHUB_CREDENTIALS', 
                                                  usernameVariable: 'DOCKERHUB_USER', 
                                                  passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    // Use a multi-line shell step with double quotes for proper environment variable expansion.
                    sh """
                        echo "$DOCKERHUB_PASSWORD" | docker login -u "$DOCKERHUB_USER" --password-stdin
                    """
                }
            }
        }
    }
}
