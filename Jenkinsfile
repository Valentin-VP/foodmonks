pipeline {
  agent {
    docker { image 'node:latest' }
  }
  stages {
    stage('Clone repo') {
        checkout scm
    }

    stage('Build') {
      steps { sh 'docker compose build --no-cache' }
    }

    stage('Deploy') {
      steps {
        script {
            docker.withRegistry(
                'https://711621010839.dkr.ecr.us-east-2.amazonaws.com',
                'ecr:us-east-2:foodmonks-aws-credentials') {
                    def backend = docker.build('foodmonks-dev-backend')
                    backend.push('latest-dev')
                },
            docker.withRegistry(
                'https://711621010839.dkr.ecr.us-east-2.amazonaws.com',
                'ecr:us-east-2:foodmonks-aws-credentials') {
                    def backend = docker.build('foodmonks-dev-frontend')
                    backend.push('latest-dev')
                },
        }
      }
    }
  }
}