package streams

import io.prometheus.client.spring.boot.EnablePrometheusEndpoint
import io.prometheus.client.spring.boot.EnableSpringBootMetricsCollector
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.processor.WallclockTimestampExtractor
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import streams.config.ConsumerProperties
import streams.config.ProducerProperties
import streams.config.StreamProperties
import java.util.*

@Configuration()
@EnableAutoConfiguration
@SpringBootApplication
@EnablePrometheusEndpoint
@EnableSpringBootMetricsCollector
class StreamAggregatorApplication

fun main(args: Array<String>) {
    SpringApplication.run(StreamAggregatorApplication::class.java, *args)
}

@Configuration
@ComponentScan("streams")
@EnableConfigurationProperties(
    StreamProperties::class,
    ConsumerProperties::class,
    ProducerProperties::class)
open class StreamAggregatorConfig {

    @Primary
    @Bean(name = arrayOf<String>("streamAggregatorProperties"))
    fun getProperties(streamProperties: StreamProperties, consumerProperties: ConsumerProperties, producerProperties: ProducerProperties): Properties {
        val randomId = "stream-aggregator-${UUID.randomUUID().toString()}"
        val props = Properties()
        props.put(StreamsConfig.CLIENT_ID_CONFIG, streamProperties.config.get("clientId") ?: randomId)

        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, streamProperties.config.get("streamThreads") ?: 1)
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, randomId)
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, streamProperties.config.get("bootstrapServers") ?: "")
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, streamProperties.config.get("replicationFactor") ?: 1)
        props.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, streamProperties.config.get("zookeeperConnect") ?: "")
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, streamProperties.config.get("commitIntervalMs") ?: 500)
        props.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor::class.java)
        props.put(StreamsConfig.POLL_MS_CONFIG, streamProperties.config.get("pollMs") ?: 500)
        props.put(StreamsConfig.BUFFERED_RECORDS_PER_PARTITION_CONFIG, streamProperties.config.get("bufferedRecordsPerPartition") ?: 1000)
        props.put(StreamsConfig.STATE_DIR_CONFIG, streamProperties.config.get("stateDir").toString() + System.currentTimeMillis())
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.config.get("groupId") ?: "redskyStreamAggregator")
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerProperties.config.get("autoOffsetReset") ?: "latest")
        return props
    }
}
