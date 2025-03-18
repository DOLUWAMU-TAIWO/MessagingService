pipeline {
  agent any
  stages {
    stage('Check Out') {
      steps {
        git(url: 'https://github.com/DOLUWAMU-TAIWO/MessagingService.git', branch: 'main')
      }
    }

    stage('Build docker ') {
      steps {
        sh 'docker build -t modothegreat/messaging-service:latest .'
      }
    }

    stage('Push to Docker hub') {
      steps {
        sh '''withCredentials([usernamePassword(credentialsId: \'DOCKERHUB_CREDENTIALS\', usernameVariable: \'DOCKERHUB_USER\', passwordVariable: \'DOCKERHUB_PASSWORD\')]) {
    docker login -u $DOCKERHUB_USER -p $DOCKERHUB_PASSWORD
    docker push modothegreat/messaging-service:latest
}'''
        }
      }

    }
  }