pipeline {

    agent any

    environment {
        CONFIG_ADDRESS = credentials('config-service-address')
    }

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
                        echo "CONFIG_ADDRESS: ${CONFIG_ADDRESS}"
                        sh './gradlew clean build -Dspring.profiles.active=develop -Dspring.config.import=configserver:${CONFIG_ADDRESS} --no-daemon'
                }
            }
        }



        stage('Build & Tag Docker Image') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'dockerhub-credential-alphaka') {
                        sh 'docker build --build-arg CONFIG_ADDRESS_ARG=\$CONFIG_ADDRESS -t alphaka/user-service:latest .'

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
