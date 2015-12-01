Feature: Kafka integration
  Scenario: Hello World kafka
    Given kafka cluster has kafka nodes "192.168.99.100:9092" and zookeeper nodes "192.168.99.100:2181"
    When a producer sends a message to "topic-test"
    Then a consumer receives a message from "topic-test" in group "group-test"
