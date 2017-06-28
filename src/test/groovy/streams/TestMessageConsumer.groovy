package streams

import com.google.common.base.Preconditions
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer

class TestMessageConsumer implements Closeable {

  static Properties createConsumerProps() {
    Properties props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("group.id", TestUtils.randomString(16))
    props.put("enable.auto.commit", "false")
    props.put("session.timeout.ms", "30000")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

    props
  }

  Consumer<String, String> consumer

  Consumer<String, String> createConsumer() {
    def consumerProps = createConsumerProps()
    new KafkaConsumer<>(consumerProps)
  }

  Consumer<String, String> start(List<String> baseTopics) {
    consumer = createConsumer()
    consumer.subscribe(Preconditions.checkNotNull(baseTopics).collect {
      generateFinalTopicName(it)
    })
  }

  private String generateFinalTopicName(baseTopic) {
    baseTopic
  }

  List<ConsumerRecord> nextMessages(baseTopic) {
    Preconditions.checkNotNull(consumer).poll(0).records(generateFinalTopicName(baseTopic)).toList().findAll {
      it.value() != null
    }
  }

  @Override
  void close() throws IOException {
    consumer?.close()
  }

  List<ConsumerRecord> waitForMessages(String baseTopic) {
    Retryers.untilNotEmpty.call({
      nextMessages(baseTopic)
    })
  }
}
