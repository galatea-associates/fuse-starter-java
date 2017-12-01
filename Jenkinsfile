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
				sh 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent -Dmaven.test.failure.ignore=true install'
				// sh 'mvn -DskipTests clean package'
			}
			post {
				success {
					// junit 'target/surefire-reports/**/*.xml' 
					echo 'Compilation success'
				}
				failure {
					echo 'Compilation failure'
				}
			}
		}
		stage('Test') {
			steps {
				echo 'Testing....'
				// sh 'mvn test'
				// sh 'mvn verify' used to integration tests
			}
			// post {
            //    always {
            //      junit 'target/surefire-reports/*.xml'
            //    }
            // }
		}
		stage('SonarQube analysis') {
			steps {
                // have created 2 SQ instances on Jenkins. One is SonarCloud and one is SonarQube
                // using SonarCloud results in an error related to parsing html
                // using SonarQube results in the analysis being done, but always timing out as the web ook reply comes from SonarCloud
				withSonarQubeEnv('SonarQube FUSE') {
					// requires SonarQube Scanner for Maven 3.2+
					sh 'mvn sonar:sonar'
				}
			}
		}
		stage('Quality gate') {
			steps {
                // this will currently always timeout. believe it's because SQ never posts back to Jenkins that it's done (even though it has)
				timeout(time: 5, unit: 'MINUTES') { // Just in case something goes wrong, pipeline will be killed after a timeout
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
		stage('Deliver') {
			steps {
				echo 'Delivering....'
				// The Jenkins Maven tutorial (https://jenkins.io/doc/tutorials/building-a-java-app-with-maven/)
				// runs the script inside https://github.com/gilesgas/simple-java-maven-app/blob/master/jenkins/scripts/deliver.sh
				// sh './jenkins/scripts/deliver.sh'
			}
		}
	}
}
