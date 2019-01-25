# hello-kafka - Hello World Kafka example

# EXAMPLE

```console
$ docker-machine ip default
192.168.99.100
$ cd kafka-docker/
$ vi docker-compose.xml docker-compose-single-broker.yml
(Update KAFKA_ADVERTISED_HOST_NAME to match output of docker-machine ip default)
$ docker-compose up
...

$ cd hello-kafka/
$ gradle cucumber
:compileJava UP-TO-DATE
:processResources UP-TO-DATE
:classes UP-TO-DATE
:jar
:assemble
:compileTestJava UP-TO-DATE
:processTestResources UP-TO-DATE
:testClasses UP-TO-DATE
:compileCucumberJava
:processCucumberResources
:cucumberClasses
:cucumber
Feature: Kafka integration
  Scenario: Hello World kafka                                                                           # kafka.feature:2
    #  docker-compose ip default
    Given kafka cluster has kafka nodes "192.168.99.100:9092" and zookeeper nodes "192.168.99.100:2181" # StepDefinitions.kafkaClusterHasNodesAndZookeeperNodes(String,String)
    When a producer sends a message to "topic-test"                                                     # StepDefinitions.aProducerSendsAMessageTo(String)
    Then a consumer receives a message from "topic-test" in group "group-test"                          # StepDefinitions.aConsumerReceivesAMessageFromInGroup(String,String)

1 Scenarios (1 passed)
3 Steps (3 passed)
0m0.893s


BUILD SUCCESSFUL

Total time: 2.67 secs
```

# REQUIREMENTS

* [Kafka](http://kafka.apache.org/) 0.8+ (e.g. https://github.com/mcandre/docker-kafka)
* [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) 1.8+
* [gradle](http://gradle.org/) 2.7+

## Optional

* [Sonar](http://www.sonarqube.org/)
* [Infer](http://fbinfer.com/)
* [make](https://www.gnu.org/software/make/)
* [GNU findutils](https://www.gnu.org/software/findutils/)
* [stank](https://github.com/mcandre/stank) (e.g. `go get github.com/mcandre/stank/...`)
* [Python](https://www.python.org) 3+ (for yamllint)
* [Node.js](https://nodejs.org/en/) (for eclint)

# JAVADOCS

```console
$ gradle javadoc
$ open build/docs/javadoc/index.html
```

# TEST + CODE COVERAGE

```console
$ gradle test jacoco
$ open build/reports/jacoco/test/html/index.html
```

# LINTING

```console
$ gradle check

```

## Optional: FindBugs

```console
$ gradle check
$ open build/reports/findbugs/main.html
```

## Optional: Sonar

```console
$ sonar start
$ gradle check sonar
$ open http://localhost:9000/
```

## Optional: Infer

```console
$ infer -- gradle clean build
```

# Advanced Topics

For Kafka Connect examples, see https://github.com/mcandre/hello-kafka-connect
