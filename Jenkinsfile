import groovy.json.JsonOutput

pipeline {
    agent any
    tools {
        maven '3.5.2'
    }
    stages {
        stage('Build') {
            steps {
                populateGlobalVariables()
                notifySlack("Starting", 'fuse-java-builds', "#2fc2e0")
                sh 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent -Dmaven.test.failure.ignore=true compile'
            }
        }
        stage('Unit tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('SonarQube analysis') {
            steps {
                withSonarQubeEnv('SonarCloud FUSE') {
                    sh 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent compile test-compile test sonar:sonar'
                }
            }
        }
        stage('Quality gate') {
            steps {
                // Just in case something goes wrong, pipeline will be killed after a timeout
                timeout(time: 2, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "Pipeline aborted due to quality gate failure: ${qg.status}"
                        }
                    }
                }
            }
        }
        stage('Deploy') {
            when {
                expression { isDeployBranch() }
            }
            steps {
                // for the moment just re-do all the maven phases, I tried doing just jar:jar, but it wasn't working with cloud foundry
                sh 'mvn package'

                pushToCloudFoundry(
                    target: 'https://api.run.pivotal.io/',
                    organization: 'FUSE',
                    cloudSpace: 'development',
                    credentialsId: 'cf-credentials',
                    manifestChoice: [manifestFile: 'manifest-dev.yml']
                    // pluginTimeout: 240 // default value is 120
                )
            }
        }
        stage('Integration tests') {
            when {
            	anyOf {
	                expression { BRANCH_NAME ==~ /^PR-\d+$/ }
            	 	branch 'develop'   
            	}
            }
            steps {
            	sleep time:90, unit: 'SECONDS'
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
                echo 'Running performance tests...'
                echo 'No performance tests defined yet.'
            }
        }
        stage('Shutdown') {
            when {
                expression { isDeployBranch() }
            }
            steps {
                echo 'Shutting down app'
                timeout(time: 2, unit: 'MINUTES') {
                    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'cf-credentials', usernameVariable: 'CF_USERNAME', passwordVariable: 'CF_PASSWORD']]) {
                        // make sure the password does not contain single quotes otherwise the escaping fails
                        sh "cf login -u ${CF_USERNAME} -p '${CF_PASSWORD}' -o FUSE -s development -a https://api.run.pivotal.io"
                        sh 'cf stop fuse-rest-dev'
                        sh 'cf logout'
                    }
                }
            }
        }
    }
    post {
        success {
            notifySlack("Successful!", 'fuse-java-builds', "good")
        }
        failure {
            notifySlack("Failed", 'fuse-java-builds', "danger")
        }
        unstable {
            notifySlack("Unstable", 'fuse-java-builds', "warning")
        }
        aborted {
            notifySlack("Aborted", 'fuse-java-builds', "#d3d3d3")
        }
    }
}

def isDeployBranch() {
   if ( BRANCH_NAME.startsWith('feature/') || BRANCH_NAME.startsWith('hotfix/') || BRANCH_NAME.startsWith('bugfix/') ) {
       return false
   } else {
       return true
   }
}

def notifySlack(titlePrefix, channel, color) {
    def jenkinsIcon = 'https://wiki.jenkins-ci.org/download/attachments/2916393/logo.png'

    def payload = JsonOutput.toJson([text: "",
        channel: channel,
        username: "Jenkins",
        icon_url: jenkinsIcon,
        attachments: [
            [
                title: "${titlePrefix} ${env.BRANCH_NAME}, build #${env.BUILD_NUMBER}",
                title_link: "${env.BUILD_URL}",
                color: "${color}",
                text: "Triggered by ${author}",
                "mrkdwn_in": ["fields"],
                fields: [
                    [
                        title: "Branch",
                        value: "${env.GIT_BRANCH}",
                        short: true
                    ],
                    [
                        title: "Last Commit",
                        value: "${message}",
                        short: true
                    ]
                ]
            ]
        ]
    ])

    withCredentials([string(credentialsId: 'gala-slack-url', variable: 'slackURL')]) {
        sh "curl -X POST --data-urlencode \'payload=${payload}\' ${slackURL}"
    }
}

def author = ""
def getGitAuthor() {
    def commit = sh(returnStdout: true, script: 'git rev-parse HEAD')
    author = sh(returnStdout: true, script: "git --no-pager show -s --format='%an' ${commit}").trim()
}

def message = ""
def getLastCommitMessage() {
    message = sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim()
}

def populateGlobalVariables() {
    getLastCommitMessage()
    getGitAuthor()
}
