# hello-kafka - Hello World Kafka example

# REQUIREMENTS

* [docker-kafka](https://github.com/wurstmeister/kafka-docker)
* [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) 1.8+
* [Gradle](http://gradle.org/) 2.7+

# EXAMPLE

```
$ docker-machine ip default
192.168.99.100
$ cd kafka-docker/
$ vi docker-compose.xml docker-compose-single-broker.yml
(Update KAFKA_ADVERTISED_HOST_NAME to match output of docker-machine ip default)
$ docker-compose up
...

$ cd hello-kafka/
$ gradle cucumber
...
```
