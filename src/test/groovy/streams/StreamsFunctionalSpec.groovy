package streams

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.streams.StreamsConfig
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.lang.Subject

import java.util.concurrent.TimeUnit


/**
 * Use docker-compose.yml in root dir to launch local Kafka before running these tests
 */
@Subject(StreamAggregatorService)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StreamsFunctionalSpec extends Specification {

  @AutoCleanup
  TestMessageProducer producer = new TestMessageProducer(
         )

  @AutoCleanup
  TestMessageConsumer consumer = new TestMessageConsumer()

  def setup() {
    System.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, TestUtils.randomString())
    System.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    System.setProperty(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181")
    System.setProperty(StreamsConfig.POLL_MS_CONFIG, "10000")

    consumer.start(['aggregate-counts'])
  }

  void 'agg counts'() {
    setup:
    Random random = new Random()
    Integer key = (Math.abs(random.nextInt() % 999999) + 1)

    when:
    producer.sendMessage('test-counts', key, "word").get(1000, TimeUnit.MILLISECONDS)

    and: 'retrieve pdp output messages again'
    List<ConsumerRecord> nextMessages = consumer.waitForMessages('aggregate-counts')

    then:
    nextMessages.size() != 0
  }

}
