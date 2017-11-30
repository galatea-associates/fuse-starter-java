# fuse-starter-java
This project serves two functions:
- Provides reference implementations for various features using our best-practices
- Provides a starting point for new galatea java projects

This readme will contain an index to features and their location in code.

## Getting Started
### Eclipse
- import as a maven project however you like. https://www.youtube.com/watch?v=BlkgrXb3L0c is one place to start if you're at a complete loss on this step.
- you're going to need to install lombok for the lombok stuff to work.  Lombok is used extensively in this project.  https://projectlombok.org/ is a good place to get started on that.  Particularly this page which takes you through the pretty simple steps to install lombok in eclipse.  Note if you're doing this step last because you raced ahead and nothing compiles you'll have to do some cleans and re-compiles to get lombok involved in generating all the class files.
- fuse-starter-java-app.launch will run that main method that starts the spring application context.  This will start a jms listener and REST services.  You can interact with those REST services using Postman. 
- fuse-starter-java-tests-unit.launch will run unit tests
- fuse-starter-java-tests-all.launch will run unit + integration tests

### IntelliJ
- 

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
 - r-click on fuse-starter-java -> SonarLint -> Change binding...
 - Replace the current binding with 'https://sonarqube.com' in the URL
 - Search 'fuse-starter-java' and accept

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
- For configuring logging to the console and selectively enabling debug logging for local testing see: src/main/resources/log4j2.debug.yml 
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

Mocking is a good way to unit test (keeping in mind that your mock needs to be used in conjuction with a integration test).  FUSE has plenty of examples of how to use @MockBean.  See `org.galatea.starter.entrypoint.SettlementJmsListenerTest` and `org.galatea.starter.entrypoint.SettlementRestControllerTest` for some examples of how to mock.  Both of those tests mock out the settlement service using `given(...)` or `verify(...)` 

For testing rest requests/responses see:
- SettlementRestControllerTest
- For running a request: MockMvc.perform
- For assertions based on the response: MockMvc.andExpect along with MockMvcResultMatchers static methods
- For easy indexing of json responses: MockMvcResultMatchers.jsonPath and https://github.com/jayway/JsonPath
- For assertions on response headers: SettlementRestControllerTest.verifyAuditHeaders()
- For convenient tests/matchers: org.hamcrest.Matchers and https://code.google.com/archive/p/hamcrest/wikis/Tutorial.wiki

