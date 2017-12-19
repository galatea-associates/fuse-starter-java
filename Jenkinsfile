pipeline {
    agent any
    tools {
        maven '3.5.2'    // 3.5.2 relates to the label applied to a given version of Maven
    }
    stages {
        stage('Initialization') {
            steps {
                echo 'Environment info'
                sh 'mvn -version'
                echo "Branch name: ${BRANCH_NAME}"
                // send build started notifications
                slackSend (color: '#FFFF00', message: "STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent -Dmaven.test.failure.ignore=true compile'
                slackSend (color: '#FFFF00', message: "BUILDING: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
            }
        }
        stage('Unit tests') {
            steps {
                sh 'mvn test'
                slackSend (color: '#FFFF00', message: "RUNNING UNIT TESTS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('SonarQube analysis') {
            steps {
                slackSend (color: '#FFFF00', message: "SONARQUBE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
                withSonarQubeEnv('SonarCloud FUSE') {
                    // requires SonarQube Scanner for Maven 3.2+
                    sh 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent compile test-compile test sonar:sonar'
                }
            }
        }
        stage('Quality gate') {
            steps {
                slackSend (color: '#FFFF00', message: "QUALITY GATE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
                // Just in case something goes wrong, pipeline will be killed after a timeout
                timeout(time: 1, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "Pipeline aborted due to quality gate failure: ${qg.status}"
                        }
                    }
                }
            }
        }
        stage('Checkstyle') {
            steps {
                slackSend (color: '#FFFF00', message: "CHECKSTYLE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
                sh 'mvn checkstyle:check'
            }
            // using the following results in an error in the pipeline - ERROR: None of the test reports contained any result
            //post {
            //    always {
            //        junit 'target/checkstyle-result.xml'
            //    }
            //}
            // this will simply show a blank report if the checkstyle check is successful
            post {
                failure {
                    publishHTML (target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: false,
                        keepAll: true,
                        reportDir: 'target',
                        reportFiles: 'checkstyle-result.xml',
                        reportName: 'Checkstyle report'
                    ])
                }
            }
        }
        stage('Deploy') {
            when {
                not {
                    anyOf {
                        // sure there's a nicer way of doing this with a regex...
                        expression { BRANCH_NAME.startsWith('feature/') }
                        expression { BRANCH_NAME.startsWith('hotfix/') }
                        expression { BRANCH_NAME.startsWith('bugfix/') }
                    }
                }
            }
            steps {
                slackSend (color: '#FFFF00', message: "DEPLOYING: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
                // for the moment just re-do all the maven phases, I tried doing just jar:jar, but it wasn't working with cloud foundry
                sh 'mvn package'
                pushToCloudFoundry(
                    target: 'https://api.run.pivotal.io/',
                    organization: 'FUSE',
                    cloudSpace: 'development',
                    credentialsId: 'danny-cloud-foundry',
                    manifestChoice: [manifestFile: 'manifest-dev.yml']
                )
            }
        }
        stage('Integration tests') {
            // according to https://gist.github.com/jonico/e205b16cf07451b2f475543cf1541e70 we can check for a PR build using the following
            when {
                expression { BRANCH_NAME ==~ /^PR-\d+$/ }
            }
            steps {
                slackSend (color: '#FFFF00', message: "INTEGRATION TESTS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
                sh 'mvn verify'
            }
            post {
               always {
                 junit 'target/failsafe-reports/*.xml'
               }
            }
        }
        stage('Performance tests') {
            when {
                branch 'develop'
            }
            steps {
                slackSend (color: '#FFFF00', message: "PERFORMANCE TESTS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
                echo 'Running performance tests...'
            }
        }
    }
}
