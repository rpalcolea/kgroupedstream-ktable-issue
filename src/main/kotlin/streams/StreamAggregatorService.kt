package streams

import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.util.*
import org.apache.kafka.streams.kstream.Aggregator
import streams.serde.MutableListSerdeWrapper
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
open class StreamAggregatorService {

    lateinit var kafkaStreams: KafkaStreams

    @Autowired
    @Qualifier("streamAggregatorProperties")
    lateinit var props: Properties

    @PostConstruct
    fun init() {
        kafkaStreams = createStream()
        kafkaStreams.start()
    }

    @PreDestroy
    fun shutdown() {
        kafkaStreams.cleanUp()
        kafkaStreams.close()
    }

    fun createStream(): KafkaStreams {
        val builder = KStreamBuilder()
        val mutableListSerde: Serde<MutableList<String>> = MutableListSerdeWrapper()

        val countStream: KStream<Int, String> = builder.stream(
                Serdes.Integer(),
                Serdes.String(),
                "test-counts"
        ).map { key, value ->
            KeyValue(key, value)
        }


        val countsTable = countStream.groupByKey(Serdes.Integer(), Serdes.String()).aggregate(
                object : Initializer<MutableList<String>> {
                    override fun apply(): MutableList<String>? {
                        return mutableListOf<String>()
                    }
                },
                Aggregator<Int, String, MutableList<String>> { aggKey, newValue, aggValue ->
                    aggValue!!.add(newValue)
                    aggValue
                },
                mutableListSerde,
                "aggregated-words-store")

        countsTable.to(Serdes.Integer(), mutableListSerde, "aggregate-counts")


        return KafkaStreams(builder, props)
    }

}