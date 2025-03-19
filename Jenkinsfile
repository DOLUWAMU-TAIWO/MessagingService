pipeline {
  agent any
  stages {
    stage('Check Out') {
      steps {
        git(url: 'https://github.com/DOLUWAMU-TAIWO/MessagingService.git', branch: 'main')
      }
    }

    stage('Docker login') {
      environment {
        DOCKERHUB_CREDENTIALS = 'credentials(\'DOCKERHUB_CREDENTIALS\')'
      }
      steps {
        sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
      }
    }

  }
}