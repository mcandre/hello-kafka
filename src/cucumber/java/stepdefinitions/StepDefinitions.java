package stepdefinitions;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import org.junit.Assert;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import kafka.producer.KeyedMessage;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.consumer.KafkaStream;
import kafka.consumer.ConsumerIterator;
import kafka.message.MessageAndMetadata;

public class StepDefinitions {
  private String kafkaNodeList;
  private String zookeeperNodeList;

  @Given("^kafka cluster has kafka nodes \"([^\"]*)\" and zookeeper nodes \"([^\"]*)\"$")
  public void kafkaClusterHasNodesAndZookeeperNodes(String kafkaNodeList, String zookeeperNodeList) {
    this.kafkaNodeList = kafkaNodeList;
    this.zookeeperNodeList = zookeeperNodeList;
  }

  @When("^a producer sends a message to \"([^\"]*)\"$")
  public void aProducerSendsAMessageTo(String topic) {
    Properties properties = new Properties();
    properties.setProperty("metadata.broker.list", kafkaNodeList);
    properties.setProperty("producer.type", "sync");
    properties.setProperty("request.required.acks", "1");

    ProducerConfig producerConfig = new ProducerConfig(properties);
    Producer<byte[], byte[]> producer = new Producer<>(producerConfig);

    String message = "FizzBuzz";
    byte[] messageBytes = message.getBytes();

    KeyedMessage<byte[], byte[]> keyedMessage = new KeyedMessage<>(topic, messageBytes);

    producer.send(keyedMessage);
  }

  @Then("^a consumer receives a message from \"([^\"]*)\" in group \"([^\"]*)\"$")
  public void aConsumerReceivesAMessageFromInGroup(String topic, String group) {
    Properties properties = new Properties();
    properties.setProperty("zookeeper.connect", zookeeperNodeList);
    properties.setProperty("group.id", group);
    properties.setProperty("auto.offset.reset", "smallest");
    properties.setProperty("consumer.timeout.ms", "3000"); // ms

    ConsumerConfig consumerConfig = new ConsumerConfig(properties);
    Map<String, Integer> topicCountMap = new HashMap<>();
    topicCountMap.put(topic, 1);
    ConsumerConnector consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);
    Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreamsMap = consumerConnector.createMessageStreams(topicCountMap);
    List<KafkaStream<byte[], byte[]>> consumerStreams = consumerStreamsMap.get(topic);
    KafkaStream<byte[], byte[]> consumerStream = consumerStreams.get(0);
    ConsumerIterator<byte[], byte[]> messageIterator = consumerStream.iterator();

    Assert.assertTrue(messageIterator.hasNext());

    MessageAndMetadata<byte[], byte[]> messageAndMetadata = messageIterator.next();
    byte[] messageBytes = messageAndMetadata.message();

    Assert.assertEquals("FizzBuzz", new String(messageBytes));
  }
}
