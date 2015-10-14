import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import junit.framework.Assert;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;

import kafka.consumer.ConsumerIterator;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.consumer.KafkaStream;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.message.MessageAndMetadata;

public class StepDefinitions {
  private String node;

  @Given("^kafka cluster has node \"([^\"]*)\"$")
  public void kafka_cluster_has_node(String address) {
    node = address;
  }

  @When("^a producer sends a message to \"([^\"]*)\"$")
  public void a_producer_sends_a_message_to(final String topic) {
    Properties properties = new Properties();
    properties.setProperty("metadata.broker.list", node);
    // ... ?

    ProducerConfig producerConfig = new ProducerConfig(properties);
    Producer<Integer, byte[]> producer = new Producer<>(producerConfig);
    String message = "FizzBuzz";
    byte[] messageBytes = message.getBytes();
    KeyedMessage<Integer, byte[]> keyedMessage = new KeyedMessage<>(topic, messageBytes);

    producer.send(keyedMessage);
  }

  @Then("^a consumer receives a message from \"([^\"]*)\"$")
  public void a_consumer_receives_a_message_from(final String topic) {
    Properties properties = new Properties();
    properties.setProperty("metadata.broker.list", node);
    // ... ?

    ConsumerConfig consumerConfig = new ConsumerConfig(properties);
    Map<String, Integer> topicCountMap = new HashMap<>();
    topicCountMap.put(topic, 1);
    ConsumerConnector consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);
    Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreamsMap = consumerConnector.createMessageStreams(topicCountMap);
    ConsumerIterator<byte[], byte[]> messageIterator = null;

    for (final Map.Entry<String, List<KafkaStream<byte[], byte[]>>> consumerStreamEntry: consumerStreamsMap.entrySet()) {
      String t = consumerStreamEntry.getKey();

      if (t.equals(topic)) {
        List<KafkaStream<byte[], byte[]>> consumerStreams = consumerStreamEntry.getValue();
        KafkaStream<byte[], byte[]> consumerStream = consumerStreams.get(0);

        messageIterator = consumerStream.iterator();
      }
    }

    MessageAndMetadata<byte[], byte[]> messageAndMetadata = messageIterator.next();
    byte[] messageBytes = messageAndMetadata.message();

    Assert.assertEquals("FizzBuzz", new String(messageBytes));
  }
}
