# fuse-starter-java
This project serves two functions:
- Provides reference implementations for various features using our best-practices
- Provides a starting point for new galatea java projects

Fuse is designed primarily to mock a securities settlement service, along with some other smaller service components. 

Settlements involve processing a trade request, and spawning relevant 'missions' that interact with various agents and counterparties to handle the delivery and receipt of securities and cash.

This readme will contain an index to features and their location in code.

## Getting Started
### Java
- Download OpenJDK 11. https://jdk.java.net/java-se-ri/11
- Unzip the archive. We recommend putting it in C:\Program Files\Java. It should create a folder called "jdk-11"
- Add a system or user variable for JAVA_HOME
  - Navigate to Control Panel -> System -> Edit environment variables for your account
  - Under User Variables, click New and add the path for the jdk-11 folder
- Add JAVA_HOME to your path
  - Edit Path under User Variables and add a new entry for %JAVA_HOME%\bin

### Maven
- For help installing Apache Maven visit: https://maven.apache.org/
- Download Maven. https://maven.apache.org/download.cgi
- Unzip the archive.  We recommend putting it in C:\Program Files\Maven.  It should create a folder called "apache-maven-3.8.1" (Or whichever version you downloaded)
- Add a system or user variable for Maven
  - Navigate to Control Panel -> System -> Edit environment variables for your account
  - Under User Variables, click New. For variable name, type MAVEN_HOME.  Click Browse Directory and add the path for the apache-maven-3.8.1 folder
- Add MAVEN_HOME to your path
  - Select the Path variable under User variables and click Edit...
  - Click new and type %MAVEN_HOME%\bin

### Git
- Git is a [Version Control System](https://git-scm.com/book/en/v2/Getting-Started-About-Version-Control) and it used with this project.
- [Download Git](https://git-scm.com/downloads) and install it with the default options.
- When the installation is finished open the newly installed "Git Bash" application and paste the following commands (with your name and work email):
   > git config --global user.name "Firstname Lastname"
   
   > git config --global user.email "lastname@galatea-associates.com"
##### Git Clone to Intellij
1. Open Intellij IDEA
2. You will be greeted with a welcome screen. Click "Check out from Version Control" and then "Git".
    - If you don't see welcome screen, choose "File" -> "New" -> "Project from Version Control" -> "Git" from menu
3. In your browser of choice go this [project's page](https://github.com/galatea-associates/fuse-starter-java)
4. Towards the top of the page click the drop down with the text "Code".
5. Copy the URL in the text box under "HTTPS"
3. Back in Intellij paste the URL you copied into the "Git Repository URL" field.
4. Click the "clone" button.

This will create a new project in Intellij and download a copy of this project into it.

##### Git Clone to Eclipse
1. Open Eclipse.
2. "File" -> "Import" -> "Git" -> "Projects from Git"
3. Click "Next", select "Clone URI" and then click "Next" again.
4. In your browser of choice go this [project's page](https://github.com/galatea-associates/fuse-starter-java)
5. Towards the top of the page click the drop down with the text "Code"
6. Copy the URL in the text box under "HTTPS"
7. Back in Eclipse, past the URL you copied into the "URI" field.
8. At the bottom of the window enter your gitlab username and password.
9. Click "Next" three more times until you have the option to select "Import as General Project".
10. Click "Finish"

This will create a new project in Eclipse and download a copy of this project into it.

### Eclipse
- Import as a maven project however you like. https://www.youtube.com/watch?v=BlkgrXb3L0c is one place to start if you're at a complete loss on this step.
- Make sure Eclipse is set up to compile to Java 11
  - This page should have the necessary details on how to set this up: https://www.baeldung.com/eclipse-change-java-version
- Install lombok: https://projectlombok.org/setup/eclipse.  Note if you're doing this step last because you raced ahead and nothing compiles you'll have to do some cleans and re-compiles to get lombok involved in generating all the class files.
- Set code style settings, which will allow auto-formatting of code to match the Google style guide
  - Navigate to Window -> Preferences -> Java -> Code Style -> Formatter
  - Click Import and select <project_directory>/style/eclipse-java-google-style.xml
  - Navigate to Window -> Preferences -> Java -> Code Style -> Organize Imports
  - Click Import and select <project_directory>/style/eclipse-java-google-style.importorder
  - Navigate to Window -> Preferences -> Java -> Editor -> Save Actions
  - Select the "Perform the selected actions on save", "Format source code", "Format edited lines", and "Organize imports" options
- Run stuff:
  - The Protobuf files for this project are generated by a Maven plugin.  Before running the application as described below, build the project with maven using mvn compile or a similar command to generate the Protobuf and avoid compilation errors.
  - src/main/java/org/galatea/starter/Application.java -> r-click -> run as Java Application.  This will start a jms listener and REST services.  Note, there is an eclipse .launch file provided which configures some VM and Program args.  
    - Note, logs will be written to C:/Users/your-user-name/logs as well as being written to stdout.
    - Note, the server port on which the application runs is set via program argument in the run configuration, --server.port=8080.  If you need to change which port to run on, change the argument in the run configuration.
  - src/test/java/org/galatea/starter/UnitTestRunner.java -> r-click -> run as Junit test to run just the unit tests.
  - src/test/java/org/galatea/starter/AllTestRunner.java -> r-click -> run as Junit test to run the unit and integration tests.

### IntelliJ
- Import as a maven project.  A simple way to do this is to Open File and select the pom.xml.
- Make sure IntelliJ is set up to compile to Java 11
  - In IntelliJ navigate to File -> Settings -> Build, Execution, Deployment -> Compiler -> Java Compiler and set the target bytecode version to 11
  - Navigate to File -> Project Structure
    - Under "Project", make sure both Project SDK and project language level are both set to Java 11
    - Under "Module", make sure the language level is 11
- Install lombok: https://projectlombok.org/setup/intellij.
- Set code style settings, which will allow auto-formatting of code to match the Google style guide
  - In IntelliJ navigate to File -> Settings -> Editor -> Code Style -> Java
  - Next to Scheme click the settings icon -> Import Scheme -> IntelliJ Idea code style XML
  - Choose <project_directory>/style/intellij-java-google-style.xml, and hit OK a few times
- Run stuff:
  - FUSE has some IntelliJ run configurations checked into the repository under .idea/runConfigurations. These run configs should be automatically imported by IntelliJ and listed in a drop-down in the top-right of the screen. Next to the drop-down are buttons to run the selected run configuration, to run in debug mode, or to run with coverage measurement. If the run configs are automatically found, they may need to be manually imported.
  - The Protobuf files for this project are generated by a Maven plugin.  Before using the below run configurations, build the project with maven using mvn compile or a similar command to generate the Protobuf and avoid compilation errors.
  - To run FUSE, use the "Application" run config.  This will start a jms listener and REST services.
    - Note, logs will be written to <project_directory>/logs as well as being written to stdout.
    - Note, the server port on which the application runs is set via program argument in the run configuration, --server.port=8080.  If you need to change which port to run on, change the argument in the run configuration.
  - To run unit tests, use the "Unit Tests" run config. See Testing section below for more info.
  - To validate code style, use the "Checkstyle" run config.

### A note on spring profiles
- The project comes with support for 3 spring profiles:
  - test: this profile is intended for running unit and integration tests.  This is the default active profile in application.yml
  - dev: this profile is for running the Application main via the IDE or cmd line.
  - uat: this profile is intended for a deployed environment.
- Your ultimate use of profiles will be dictated by the physical environment availables to your project.

### Maven
- mvn test will run the unit tests
- mvn verify will run the unit and integration tests

### Postman
 - Postman is a popular API client that makes it easy for developers to create, share, test and document APIs. This is done by allowing users to create and save simple and complex HTTP/s requests, as well as read their responses.
 - You can import our Postman collection (src/postman/Fuse-Starter-Java.postman_collection.json) for sample REST calls that can be made to the application once it has been started.
#### Installing
 - Navigate to https://www.postman.com/downloads/ and click "Download the App"
   - Note that you can also try the Web Version of postman, but you will have to make an account
 - Run the Postman-Setup executable
#### Create a new Environment
 - On the tab on the left side, select Environments
 - Click the "+" to create a new environment
 - Set the environment name to "Local"
 - Add a new variable "host" with a value of "localhost:8080"
 - Save the changes and select "Local" in the drop down menu on the top right
#### Running Sample REST Calls 
 - Select Import in the top left corner
 - Select Upload Files and upload fuse-starter-java/src/postman/Fuse-Starter-Java.postman_collection.json 
 - You should now see a collection called Fuse-Starter-Java with 7 different Post, Get, and Put requests
 - Run Fuse and send a request through Postman to test a response
   - Note that if you're getting a NOT FOUND error, make sure that the {{host}} in the request is pointing to the Local environment you created.  Host should be written in all lowercase.

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
 - Search for the Organization 'Galatea'
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

`org.galatea.starter.entrypoint.SettlementJmsListener` - shows how you listen for messages. Supports both JSON and Protobuf message formats.
`org.galatea.starter.utils.jms.FuseJmsListenerContainerFactory` - provides a custom "listener container" factory (which is a spring jms concept).  We use our own factory, so we can create our own "listener container".
`org.galatea.starter.utils.jms.FuseMessageListenerContainer` - is a custom listener container.  This is the code that will actually call the JMS listener that you have registered.  You'll notice that we populate our trace repository here.  This allows us to capture every message we process and the resulting outcome.  
`org.galatea.starter.JmsConfig` - is the spring java config related to jms
`org.galatea.starter.entrypoint.SettlementJmsListenerTest` - shows you how to test a jms listener.  SpringBoot fires up an embedded ActiveMQ broker for the test.  It's important to look at the mentiod annotated with @After in ASpringTest.  You'll see that we tear down the jms connection after each test to ensure isolation between tests.  This is important.

## JPA

- **JPA (Java Persistence API)** is a Java specification for ORM (Object Relational Mapping) which is the process of converting objects to records in a relational database and vice versa. This abstracts developers from low level SQL code as well as the need to hand-map SQL result objects to Java POJOs.

- **Hibernate** is an very popular implementation of the JPA specification that FUSE uses. It also offers other features not in the JPA specification. However, usage of these features will make it more challenging to switch to another JPA provider if needed.

- **Spring Data** is a layer on top of a JPA provider that acts as an abstraction for JPA repositories to reduce boilerplate code. For example, Spring Data contains the `CrudRepository` interface which provides CRUD functionality, in very few lines of code, for an entity class being managed.

## Logging
- FUSE uses Log4j2 for logging. See the "Automatic Configuration" section of <https://logging.apache.org/log4j/2.x/manual/configuration.html> for how Log4j2 decides which log config file to use.
- For the main configuration see: src/main/resources/log4j2.yml
- For configuring logging to the console and selectively enabling debug logging for local testing see: src/test/resources/log4j2-test.yml
- For required dependencies see: pom.xml
- For creation of internal request id see: Tracer.java
- For creation of external request id for Rest requests see: SettlementRestController.java
- For creation of external request id for JMS requests see: FuseMessageListenerContainer.java
- For inclusion of internal/external request ids in log statements see: log4j2.yml's log-pattern definition
- To log to console instead of logging to file, set -Dlog4j.configurationFile=log4j2-stdout.yml as a VM option

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
- **IntelliJ**: Run "Unit Tests" run configuration 

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

## Application and Thread Monitoring
In order to view the status of a running FUSE application you can use [VisualVM](https://visualvm.github.io/index.html). Downloads and installation instructions are available at <https://visualvm.github.io/download.html>.
Basic Instructions
* Download zip file from the location mentioned above and extract the contents.
* In the unzipped files find and run `visualvm_206/bin/visualvm.exe`
    - If the following error appears "Cannot find Java 1.8 or higher" you will need to specify the location of the jdk to use.
    - example: `./visualvm.exe --jdkhome "C:/Program Files/Java/jdk-11"`
* Once VisualVM is running, if FUSE is also running locally on the same machine, there should be an application with a name similar to the following "org.galatea.starter.Application (pid XXXXX)"
    - If the fuse application does not automatically appear you can connect to it manually by going to File -> Add JMX Connection and entering the host and port of the Fuse instance's JMX server
    - To find the JMX server port for your FUSE instance you can search through the log to find a line similar to the following `INFO  o.a.a.b.j.ManagementContext - JMX consoles can connect to service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi`

Using VisualVM
Once the application is opened in VisualVM there are several tabs filled with different information about the application
* Overview - provides basic details about the application's process and the JVM running it.
* Monitor - shows real time information about the application including CPU usage, memory usage, and number of running threads.
* Threads - provides a list of all the currently running threads and has the ability to make a thread dump for any selected threads.
    - If you want to be able to get realtime stack traces from any thread you can install the VisualVM Threads Inspector plugin
    - To install go to Tools -> Plugins, then select the Available Plugins tab. Find "Threads Inspector" in the list of plugins and click install.
* Sampler - Gives the option to see a more detailed view of CPU and memory usage by the app including a by thread breakdown
* Profiler - Allows enabling CPU or memory profiling and viewing the results.
