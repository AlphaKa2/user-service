pipeline {

    agent any

    tools {
        jdk 'jdk17'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }


        stage('Build & Test') {
            steps {
                script {
                        sh './gradlew clean build -Dspring.profiles.active=develop --no-daemon'
                }
            }
        }



        stage('Build & Tag Docker Image') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'dockerhub-credential-alphaka') {
                        sh 'docker build -t alphaka/user-service:latest .'

                    }
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    sh 'docker push alphaka/user-service:latest'
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    withCredentials([
                            sshUserPrivateKey(credentialsId: 'jenkins-ssh', keyFileVariable: 'SSH_KEY'),
                            string(credentialsId: 'vm-app1-address', variable: 'VM_ADDRESS')
                    ]) {

                        // 미리 작성해둔 deploy.sh 실행
                        sh '''
                            /var/jenkins_home/scripts/deploy.sh \\
                            "$VM_ADDRESS" \\
                            "$SSH_KEY" \\
                            "alphaka/user-service" \\
                            "user-service" \\
                            "8001"
                        '''
                    }
                }
            }
        }

    }
}
