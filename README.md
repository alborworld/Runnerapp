This application is a primitive RESTful service with one Spring/MVC controller. 
It keeps track of how many kilometers some runners have run, and in which country. A runner is supposed to always run in the same country.

It provides the following services:

- *sendRunnerStatusUpdate*: adds/updates the status of a runner with the last number of km run
- *getRunnerStatus*: returns the status of a runner
- *getRunnerList*: returns a list of runners, with info on the total number of km run 
- *getCountryList:* returns a list of countries, with the option to sort it by total distance run or name (or unsorted), in ascending or descending order

There are three types of tests:

- unit tests
- integration tests, which run jetty and test the deployed war
- concurrency tests, to verify that the state of each runner remains consistent if multiple clients send updates concurrently

Jacoco code coverage reports are generated under *target/sites/jacoco/* during each build.

To issue a build, from the command line:

~~~~
$ mvn clean install
~~~~

To run the application in jetty execute:

~~~~
$ mvn jetty:run
~~~~

It is reachable at *localhost:9090*.

Under the directory *scripts* there are some simple scripts addressing sample queries to the service.

**IMPORTANT**: you need to use Maven 3 and Java 1.7.
