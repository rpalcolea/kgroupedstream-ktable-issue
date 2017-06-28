package streams

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata

import java.util.concurrent.Future

class TestMessageProducer implements Closeable {
  Map defaultMessageBodyOverrides = [:]

  Properties createProducerProps() {
    Properties props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("acks", "all")
    props.put("retries", 0)
    props.put("batch.size", 16384)
    props.put("linger.ms", 1)
    props.put("buffer.memory", 33554432)
    props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    props
  }

  Producer<String, String> producer = createProducer()

  Producer<String, String> createProducer() {
    def producerProps = createProducerProps()
    new KafkaProducer<>(producerProps)
  }

  Future<RecordMetadata> sendNullMessage(String baseTopic) {
    sendMessage(baseTopic, null, null)
  }

  Future<RecordMetadata> sendMessage(String baseTopic, messageKey, messageData) {
    producer.send(new ProducerRecord<String, String>(
        baseTopic,
        messageKey,
        messageData))
  }

  @Override
  void close() throws IOException {
    producer.close()
  }
}
