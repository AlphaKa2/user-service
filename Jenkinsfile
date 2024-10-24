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

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar') {
                    sh '''
                        $SCANNER_HOME/bin/sonar-scanner \
                        -Dsonar.projectName=OnGil-Blog \
                        -Dsonar.projectKey=OnGil-Blog \
                        -Dsonar.java.binaries=.
                    '''
                }
            }
        }
        stage('Quality Gate') {
            steps {
                script {
                    waitForQualityGate abortPipeline: false, credentialsId: 'sonar-token'
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
                    withDockerRegistry(credentialsId: 'dockerhub-credential-alphaka') {
                        sh 'docker push alphaka/user-service:latest'

                    }
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
