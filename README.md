# fuse-starter-java
This project serves two functions:
- Provides reference implementations for various features using our best-practices
- Provides a starting point for new galatea java projects

This readme will contain an index to features and their location in code.

## Getting Started
### Eclipse
- Import as a maven project however you like. https://www.youtube.com/watch?v=BlkgrXb3L0c is one place to start if you're at a complete loss on this step.
- Install lombok: https://projectlombok.org/setup/eclipse.  Note if you're doing this step last because you raced ahead and nothing compiles you'll have to do some cleans and re-compiles to get lombok involved in generating all the class files.
- Run stuff:
  - src/main/java/org/galatea/starter/Application.java -> r-click -> run as Java Application.  This will start a jms listener and REST services.  Note, there is an eclipse .launch file provided which configures some VM and Program args.  
    - Note, logs will be written to C:/Users/your-user-name/logs and will not be written to stdout as is generally appropriate in a deployed setting.
  - src/test/java/org/galatea/starter/UnitTestRunner.java -> r-click -> run as Junit test to run just the unit tests.
  - src/test/java/org/galatea/starter/AllTestRunner.java -> r-click -> run as Junit test to run the unit and integration tests.

### IntelliJ
- Import as a maven project.  A simple way to do this is to Open File and select the pom.xml.
- Install lombok: https://projectlombok.org/setup/intellij.
- Download the [IntelliJ Google Java Style](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml) file
  - In IntelliJ Navigate to File -> Settings -> Editor -> Code Style -> Java
  - Next to Scheme click the settings icon
  - Import Scheme
  - IntelliJ Idea Code Style as XML
  - Choose the downloaded file and click OK
- Run stuff:
  - src/main/java/org/galatea/starter/Application.java -> r-click -> Run Application.main().  This will start a jms listener and REST services.  Note, there is a supplied Run Configurations for this class in .idea/runConfigurations which sets up some VM and Program args for this class.  If IntelliJ didn't automatically find them you may have to manually set them.
    - Note, logs will be written to C:/Users/your-user-name/logs and will not be written to stdout as is generally appropriate in a deployed setting.
  - src/test/java/org/galatea/starter/UnitTestRunner.java -> r-click -> run UnitTestRunner to run just the unit tests.
  - src/test/java/org/galatea/starter/AllTestRunner.java -> r-click -> run AllTestRunner to run the unit and integration tests.

### A note on spring profiles
- The project comes with support for 3 spring profiles:
  - test: this profile is intended for running unit and integration tests.  This is the default active profile in application.yml
  - dev: this profile is for running the Application main via the IDE or cmd line.
  - uat: this profile is intended for a deployed environment.
- Your ultimate use of profiles will be dictated by the physical environment availables to your project.

### Maven
- mvn test will run the unit tests
- mvn verify will run the unit and integration tests

### Capsule
  - Once you've run mvn install, you should have a runnable jar in your target directory.  Run this command to start the jvm:  `java -Dcapsule.log=verbose -Dcapsule.mode=[mode] -jar target/fuse-starter-java-[version]-capsule.jar`  (e.g. `java -Dcapsule.log=verbose -Dcapsule.mode=test -jar target/fuse-starter-java-0.0.1-SNAPSHOT-capsule.jar`)

### Postman
 - You can import our Postman collection (src/postman/Fuse-Starter-Java.postman_collection.json) for sample REST calls that can be made to the application once it has been started.
 
## Branching model
We use this branching model in fuse-starter-java:  http://nvie.com/posts/a-successful-git-branching-model/

- Feature branches should be created under feature/
- Release candidate branches should be created under release/
- Develop is the main development branch
- Master should mirror what is running in "production"

## SonarQube integration
- Sonar: https://sonarcloud.io/dashboard?id=org.galatea%3Afuse-starter-java (can login using GitHub account)
- To integrate into eclipse
 - Install SonarLint
 - r-click on fuse-starter-java -> SonarLint -> Bind to a SonarQube project...
 - Select 'Connect to a SonarQube server...'
 - Select SonarCloud and generate a token to continue.
 - Search for the Organization 'Open Source' (there are a few, pick the one with the Description "This organization gathers..."
 - Bind to 'starter-java' and accept

##  Components
FUSE suggests that you break up your application into the following components.  Many of these correspond to spring stereotypes:
- **Entry points**: Components that receive stimuli from the outside world and react to them.  This can include REST requests, JMS messages, files.  You'll find examples of these in the org.galatea.starter.entrypoint package.
- **Services**: We embrace the micro-service architecture and suggest putting business logic inside small services that can be composed together to perform a business function.  Services may perform business processing themselves or make out-of-process calls to other services (e.g. another team's web service).   You should strive to inject a single service into each entry point class.   This service can then be composed of multiple other services.  Examples of these can be found in org.galatea.starter.service
- **Domain objects**: These are your "model" entities that represent your business objects and data model.  These should be anemic objects  i.e. primarily data containers with little business logic.  Examples of these can be found in org.galatea.starter.domain
- **Repositories**:  These components handle interactions with your "persistence" layer.  This could include a database, a distributed cache, a file, etc. Repositories should be injected into Services that need to store data.  Examples can be found in org.galatea.starter.domain.rpsy.

## Dev best practices
- Use constructor based DI outside of your unit tests.  With lombok and spring 4.3, this should be very little work.  You no longer need to add an Autowired annotation for single-constructor classes.  Spring will just figure it out.  See `SettlementRestController` as an example.
- Use Spring to automatically bind arguments for @Bean methods in your configuration classes. See `MvcConfig.webRequestLoggingFilter` as an example.

## JMS
FUSE currently shows how to read from a queue (not a topic).  

`org.galatea.starter.entrypoint.SettlementJmsListener` - shows how you listen for messages.
`org.galatea.starter.utils.jms.FuseJmsListenerContainerFactory` - provides a custom "listener container" factory (which is a spring jms concept).  We use our own factory, so we can create our own "listener container".
`org.galatea.starter.utils.jms.FuseMessageListenerContainer` - is a custom listener container.  This is the code that will actually call the JMS listener that you have registered.  You'll notice that we populate our trace repository here.  This allows us to capture every message we process and the resulting outcome.  
`org.galatea.starter.JmsConfig` - is the spring java config related to jms
`org.galatea.starter.entrypoint.SettlementJmsListenerTest` - shows you how to test a jms listener.  SpringBoot fires up an embedded ActiveMQ broker for the test.  It's important to look at the mentiod annotated with @After in ASpringTest.  You'll see that we tear down the jms connection after each test to ensure isolation between tests.  This is important.

## Logging
- For the main configuration see: src/main/resources/log4j2.yml 
- For configuring logging to the console and selectively enabling debug logging for local testing see: src/test/resources/log4j2-debug.yml 
- For required dependencies see: pom.xml
- For creation of internal request id see: Tracer.java
- For creation of external request id for Rest requests see: SettlementRestController.java
- For creation of external request id for JMS requests see: FuseMessageListenerContainer.java
- For inclusion of internal/external request ids in log statements see: log4j2.yml's log-pattern definition

## Request Audit
For inclusion of audit details in the response headers see: FuseWebRequestTraceFilter.addAuditHeaders()

## Testing
This section will cover some high level principles that we want to follow.  Specifics about testing a feature (e.g. JMS) will be covered in the section relevant to that topic.

Automated testing is good.  You should do it.  You should also design your application to be easily tested.  It's important to think about testing "seams" up front.  Designing for testability is just as important as designing for functional or performance requirements.

We often struggle with the terms unit vs integration test.  For the purposes of FUSE, let's define as follows:
- A unit test should test specific functionality without requiring resources outside of your jvm (e.g. no external databases, no external queues).  It really should only test a single class with mocked out dependencies, but we can see cases where you might want to relax the "single class" restriction.  Unit tests should be executed during the mvn test goal.
- An integration test should connect to resources outside of your jvm and test that the end-to-end flow works as expected.  These should be executed during the mvn verify goal.

To run the FUSE unit tests:
- **Eclipse**: r-click 'Run As -> JUnit Test' on src/test/java/org/galatea/starter/UnitTestRunner
- **command line**: Run '$>mvn test'
- **IntelliJ**: TBD 

Mocking is a good way to unit test (keeping in mind that your mock needs to be used in conjuction with a integration test).  FUSE has plenty of examples of how to use @MockBean.  See `org.galatea.starter.entrypoint.SettlementJmsListenerTest` and `org.galatea.starter.entrypoint.SettlementRestControllerTest` for some examples of how to mock.  Both of those tests mock out the settlement service using `given(...)` or `verify(...)` 

For testing rest requests/responses see:
- SettlementRestControllerTest
- For running a request: MockMvc.perform
- For assertions based on the response: MockMvc.andExpect along with MockMvcResultMatchers static methods
- For easy indexing of json responses: MockMvcResultMatchers.jsonPath and https://github.com/jayway/JsonPath
- For assertions on response headers: SettlementRestControllerTest.verifyAuditHeaders()
- For convenient tests/matchers: org.hamcrest.Matchers and https://code.google.com/archive/p/hamcrest/wikis/Tutorial.wiki

## Builds
We have a Jenkins server hosted on AWS that handles the FUSE continuous integration process - https://jenkins.fuse.galatea-associates.com

The build process is handled via a [Jenkins pipeline build](https://jenkins.io/doc/book/pipeline/), with the pipeline managed via the *Jenkins* file in the root of the project.  We recommend that all of your build steps are captured in the *Jeninks* file.  You should not define any build steps directly within the Jenkins server.

We currently don't have a recommendation for how to handle this in TeamCity and would love someone who is familiar with TeamCity to recommend an equivalent model.
