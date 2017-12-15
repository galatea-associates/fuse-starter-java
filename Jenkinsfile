pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'chmod u+x ./gradlew'
                sh './gradlew build -i'
            }
        }
    }
}
