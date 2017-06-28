package streams.serde

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer

class MutableListSerde: Serializer<MutableList<String>>, Deserializer<MutableList<String>> {
    internal var mapper: ObjectMapper? = ObjectMapper()

    override fun configure(configs: MutableMap<String, *>, isKey: Boolean) {
        if (mapper == null) {
            mapper = ObjectMapper()
        }
    }

    override fun serialize(topic: String, data: MutableList<String>): ByteArray {
        return mapper!!.writeValueAsBytes(data)
    }

    override fun deserialize(topic: String, data: ByteArray?): MutableList<String> {
        if (data == null) {
            return mutableListOf<String>()
        }
        return mapper!!.readValue(data, mutableListOf<String>().javaClass)
    }

    override fun close() {
        mapper = null
    }
}

