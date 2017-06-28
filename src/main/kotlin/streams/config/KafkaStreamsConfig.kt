package streams.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component


@ConfigurationProperties(prefix = "stream")
@Component
open class StreamProperties {
    var config: MutableMap<String, Any> = HashMap()
}

@ConfigurationProperties(prefix = "producer")
@Component
open class ProducerProperties {
    var config: MutableMap<String, Any> = HashMap()
}

@ConfigurationProperties(prefix = "consumer")
@Component
open class ConsumerProperties {
    var config: MutableMap<String, Any> = HashMap()
}