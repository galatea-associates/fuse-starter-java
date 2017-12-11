pipeline {
	agent any
	tools {
		maven '3.5.2'	// 3.5.2 relates to the label applied to a given version of Maven
	}
	stages {
		stage('Initialization') {
			steps {
				echo 'Environment info'
				sh 'mvn -version'
				echo 'Branch name:'
				echo BRANCH_NAME
			}
		}
		stage('Build') {
			steps {
				echo 'Building...'
				// we'll want to separate the jacoco step once get around to handling PR builds
				sh 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent -Dmaven.test.failure.ignore=true compile'
			}
		}
		stage('Unit tests') {
            steps {
                sh 'mvn test'
            }
            // post {
            //    always {
            //      junit 'target/surefire-reports/*.xml'
            //    }
            // }
        }
		stage('SonarQube analysis') {
			steps {
				withSonarQubeEnv('SonarCloud FUSE') {
					// requires SonarQube Scanner for Maven 3.2+
					sh 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install sonar:sonar'
				}
			}
		}
		stage('Quality gate') {
			steps {
                // this currently always timeout. believe it's because SQ never posts back to Jenkins that it's done (even though it has)
				timeout(time: 1, unit: 'MINUTES') { // Just in case something goes wrong, pipeline will be killed after a timeout
					script {
						def qg = waitForQualityGate()
						if (qg.status != 'OK') {
							error "Pipeline aborted due to quality gate failure: ${qg.status}"	// note that "" are needed for string interpolation
						}
					}
				}
			}
		}
		stage('Checkstyle') {
			steps {
                sh 'mvn checkstyle:check'
			}
            post {
                success {
                    echo 'Checkstyle success'
                }
                failure {
                    echo 'Checkstyle failure'
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
				echo 'Deploying to Cloud Foundary....'
//				pushToCloudFoundry(
//					target: 'https://api.run.pivotal.io/',
//					organization: 'FUSE',
//					cloudSpace: 'development',
//					credentialsId: 'pcfdev_user',
//					manifestChoice: [manifestFile: 'manifest-dev.yml']
//				)
			}
		}
        stage('Integration tests') {
			// according to https://gist.github.com/jonico/e205b16cf07451b2f475543cf1541e70 we can check for a PR build using the following
			when {
				expression { BRANCH_NAME ==~ /^PR-\d+$/ }
			}
            steps {
                sh 'mvn verify'
            }
            // post {
            //    always {
            //      junit 'target/surefire-reports/*.xml'
            //    }
            // }
        }
        stage('Performance tests') {
            when {
                branch 'develop'
            }
            steps {
                echo 'Running performance tests...'
            }
        }
	}
}
