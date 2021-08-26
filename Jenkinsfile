pipeline {
    agent { 
        kubernetes{
            label 'jenkins-slave'
        }
        
    }
    environment{
        DOCKER_USERNAME = 'brainupgrade'
        DOCKER_PASSWORD = credentials('docker-brainupgrade')
        GIT_USERNAME = 'brainupgrade-in'
        GIT_PASSWORD = credentials('bugithub')
        GIT_TOKEN = credentials('github-bu-token')
    }
    stages {
        stage('docker login') {
            steps{
                sh(script: """
                    docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
                """, returnStdout: true) 
            }
        }
        stage('code checkout') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/features-one']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/brainupgrade-in/weather.git']]])
                // sh 'mvn clean install'
            }
        }
        stage('code build') {
            steps{
                sh script: '''
                #!/bin/bash
                cd $WORKSPACE
                mvn clean install
                '''
            }
        }
        stage('docker build') {
            steps{
                sh script: '''
                #!/bin/bash
                cd $WORKSPACE
                docker build -t brainupgrade/weather:features-one-${BUILD_NUMBER} -f Dockerfile .
                '''
            }
        }

        stage('docker repo push') {
            steps{
                sh(script: """
                    docker push brainupgrade/weather:features-one-${BUILD_NUMBER}
                """)
            }
        }

        stage('k8s deploy') {
            steps{
                sh script: '''
                #!/bin/bash
                cd $HOME
                curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl
                chmod +x ./kubectl
                sed -i "s/weather:latest/weather:features-one-${BUILD_NUMBER}/g" $WORKSPACE/k8s/deploy.yaml
                ./kubectl apply -f $WORKSPACE/k8s/deploy.yaml
                '''
            }
        }
        stage('tag & git push') {
            steps {
                sh script: '''
                #!/bin/bash
                cd $WORKSPACE/
                git config --global user.email "jenkins@brainupgrade.in"
                git config --global user.name "jenkins @ brainupgrade.in"
                git config --global push.default current
                git checkout .
                git tag -a ${BUILD_NUMBER} -m "deployed ${BUILD_NUMBER} to kubernetes cluster"
                git push https://$GIT_USERNAME:$GIT_TOKEN@github.com/brainupgrade-in/weather.git  ${BUILD_NUMBER}
                '''  
            }
        }
    }  
}
