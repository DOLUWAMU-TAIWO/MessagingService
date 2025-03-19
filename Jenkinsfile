pipeline {
    agent any
    stages {
        stage('Check Out') {
            steps {
                git(url: 'https://github.com/DOLUWAMU-TAIWO/MessagingService.git', branch: 'main')
            }
        }
        stage('Docker Login') {
            steps {
                // Use the withCredentials block to inject your credentials securely
                withCredentials([usernamePassword(credentialsId: 'DOCKERHUB_CREDENTIALS', 
                                                  usernameVariable: 'DOCKERHUB_USER', 
                                                  passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    // The echo command pipes the password into docker login without exposing it in logs.
                    sh 'echo $DOCKERHUB_PASSWORD | docker login -u $DOCKERHUB_USER --password-stdin'
                }
            }
        }
    }
}
