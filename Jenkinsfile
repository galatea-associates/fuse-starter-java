pipeline {
	agent any
	tools {
		maven '3.5.2'	// 3.5.2 relates to the label applied to a given version of Maven
		//jdk 'jdk8'	// doesn't currently work - Tool type "jdk" does not have an install of "jdk8" configured - did you mean "null"? @ line 5, column 7. Same with oraclejdk8
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
		stage('SonarQube analysis') {
			steps {
				withSonarQubeEnv('SonarCloud FUSE') {
					// requires SonarQube Scanner for Maven 3.2+
					sh 'mvn sonar:sonar'
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
                // for some reason, when configure the checkstyle plugin to use google_checks.xml this step never fails even though there are violations
                // removing the use of google_checks.xml causes violations to be reported
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
				echo 'Deploying....'
//				pushToCloudFoundry(
//					target: 'fuse-rest-dev.cfapps.io',
//					organization: 'FUSE',
//					cloudSpace: 'development',
//					credentialsId: 'pcfdev_user',
//					manifestChoice: [manifestFile: 'path/to/manifest.yml']
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
