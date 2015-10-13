Feature: Kafka integration
  Scenario: Topic "topic-test" is empty
    Given a producer sends a message to "topic-test"
    Then a consumer receives a message from "topic-test"
