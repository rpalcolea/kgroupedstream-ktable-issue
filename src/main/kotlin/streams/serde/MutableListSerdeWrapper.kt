package streams.serde

import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

class MutableListSerdeWrapper: Serde<MutableList<String>> {

    override fun configure(configs: MutableMap<String, *> , isKey: Boolean) {
    }

    override fun close() {
    }


    override fun serializer(): Serializer<MutableList<String>> {
        return MutableListSerde()
    }


    override fun deserializer(): Deserializer<MutableList<String>> {
        return MutableListSerde()
    }
}