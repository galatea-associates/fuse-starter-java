pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'chmod u+x ./gradlew' // set file permissions on the wrapper that we just checked out

                // I'm going to call the gradle wrapper rather than use the jenkins gradle plugin
                // https://stackoverflow.com/questions/27064631/jenkins-gradle-integration-invoke-gradle-vs-use-gradle-wrapper-options
                // bigguy (a principal engineer at Gradle, Inc) says to, so clearly we should.
                sh './gradlew build -x test -i'
            }
        }
        stage('')
    }
}
