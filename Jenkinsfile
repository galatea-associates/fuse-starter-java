pipeline {
    agent any
    stages {
        stage('Initialization') {
            steps {
                echo 'Environment info'
                sh 'gradle -version'
                echo "Branch name: ${BRANCH_NAME}"
            }
        }
        stage('Build') {
            steps {
                sh './gradlew build -i'
            }
        }
    }
}
