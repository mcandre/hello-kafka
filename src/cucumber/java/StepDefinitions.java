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
  private String kafkaNodeList;
  private String zookeeperNodeList;

  @Given("^kafka cluster has kafka nodes \"([^\"]*)\" and zookeeper nodes \"([^\"]*)\"$")
  public void kafka_cluster_has_node(String kafkaNodeList, String zookeeperNodeList) {
    this.kafkaNodeList = kafkaNodeList;
    this.zookeeperNodeList = zookeeperNodeList;
  }

  @When("^a producer sends a message to \"([^\"]*)\"$")
  public void a_producer_sends_a_message_to(String topic) {
    Properties properties = new Properties();
    properties.setProperty("metadata.broker.list", kafkaNodeList);
    properties.setProperty("serializer.class", "kafka.serializer.DefaultEncoder");
    properties.setProperty("producer.type", "sync");
    properties.setProperty("request.required.acks", "0");
    // ... ?

    ProducerConfig producerConfig = new ProducerConfig(properties);
    Producer<byte[], byte[]> producer = new Producer<>(producerConfig);

    String message = "FizzBuzz";
    byte[] messageBytes = message.getBytes();

    KeyedMessage<byte[], byte[]> keyedMessage = new KeyedMessage<>(topic, messageBytes);

    producer.send(keyedMessage);
  }

  @Then("^a consumer receives a message from \"([^\"]*)\" in group \"([^\"]*)\"$")
  public void a_consumer_receives_a_message_from_in_group(String topic, String group) {
    Properties properties = new Properties();
//    properties.setProperty("metadata.broker.list", kafkaNodeList);
    properties.setProperty("zookeeper.connect", zookeeperNodeList);
    properties.setProperty("group.id", group);
    properties.setProperty("consumer.timeout.ms", "3"); // ms
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
