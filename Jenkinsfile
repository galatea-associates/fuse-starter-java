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
					echo 'Success'
				}
				failure {
					echo 'Failure'
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
			// withSonarQubeEnv('My SonarQube Server') {	// this will come from the Manage Jenkins -> Configure System -> SQ Servers section, which doesn't current exist...
				// requires SonarQube Scanner for Maven 3.2+
				// sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar'
			// }
		}
		stage('Quality Gate'){
			//timeout(time: 1, unit: 'HOURS') { // Just in case something goes wrong, pipeline will be killed after a timeout
			//	def qg = waitForQualityGate()
			//	if (qg.status != 'OK') {
			//		error "Pipeline aborted due to quality gate failure: ${qg.status}"	// note that "" are needed for string interpolation
			//	}
			//}
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