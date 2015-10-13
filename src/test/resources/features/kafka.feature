Feature: Kafka integration
  #  docker-compose ip default
  Scenario: Hello World kafka
    Given kafka cluster has node "192.168.99.100:9092"
    When a producer sends a message to "topic-test"
    Then a consumer receives a message from "topic-test"
