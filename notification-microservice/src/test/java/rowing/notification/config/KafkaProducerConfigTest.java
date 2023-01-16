package rowing.notification.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KafkaProducerConfigTest {

    KafkaProducerConfig kafkaProducerConfig;

    @BeforeEach
    void setup() {
        kafkaProducerConfig = new KafkaProducerConfig();
        ReflectionTestUtils.setField(kafkaProducerConfig, "bootstrapServers",
                "pkc-ewzgj.europe-west4.gcp.confluent.cloud:9092");
        ReflectionTestUtils.setField(kafkaProducerConfig, "kafkaJaasConfig",
                "org.apache.kafka.common.security.plain.PlainLoginModule "
                        + "required username='XB3BPWFWIIFKLH6A' "
                        + "password='CAoprjk2rgUqNEBAvdY2EAhdLAZ8rz3qx+qNE4uaLG4z7ru9k7BV5efdy2RdOWpB';");
        ReflectionTestUtils.setField(kafkaProducerConfig, "kafkaSaslMechanism",
                "PLAIN");
        ReflectionTestUtils.setField(kafkaProducerConfig, "kafkaSecurityProtocol",
                "SASL_SSL");
        ReflectionTestUtils.setField(kafkaProducerConfig, "useAuthentication",
                true);
    }

    @Test
    void gettersTest() {
        assertEquals("pkc-ewzgj.europe-west4.gcp.confluent.cloud:9092", kafkaProducerConfig.getBootstrapServers());
        assertEquals("org.apache.kafka.common.security.plain.PlainLoginModule "
                        + "required username='XB3BPWFWIIFKLH6A' "
                        + "password='CAoprjk2rgUqNEBAvdY2EAhdLAZ8rz3qx+qNE4uaLG4z7ru9k7BV5efdy2RdOWpB';",
                kafkaProducerConfig.getKafkaJaasConfig());
        assertEquals("PLAIN", kafkaProducerConfig.getKafkaSaslMechanism());
        assertEquals("SASL_SSL", kafkaProducerConfig.getKafkaSecurityProtocol());
        assertEquals(true, kafkaProducerConfig.isUseAuthentication());
    }

    @Test
    void producerConfigUseAuthentication() {
        Map<String, Object> props = kafkaProducerConfig.producerConfig();
        assertNotNull(props);

        assertEquals("pkc-ewzgj.europe-west4.gcp.confluent.cloud:9092",
                props.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(StringSerializer.class, props.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(StringSerializer.class, props.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));

        assertTrue(kafkaProducerConfig.isUseAuthentication());
        assertEquals("PLAIN", props.get("sasl.mechanism"));
        assertEquals("org.apache.kafka.common.security.plain.PlainLoginModule "
                        + "required username='XB3BPWFWIIFKLH6A' "
                        + "password='CAoprjk2rgUqNEBAvdY2EAhdLAZ8rz3qx+qNE4uaLG4z7ru9k7BV5efdy2RdOWpB';",
                props.get("sasl.jaas.config"));
        assertEquals("SASL_SSL", props.get("security.protocol"));
    }

    @Test
    void producerConfigNotUseAuthentication() {
        kafkaProducerConfig.setUseAuthentication(false);
        Map<String, Object> props = kafkaProducerConfig.producerConfig();
        assertNotNull(props);

        assertEquals("pkc-ewzgj.europe-west4.gcp.confluent.cloud:9092",
                props.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(StringSerializer.class, props.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(StringSerializer.class, props.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));

        assertFalse(kafkaProducerConfig.isUseAuthentication());
        assertFalse(props.containsKey("sasl.mechanism"));
        assertFalse(props.containsKey("sasl.jaas.config"));
        assertFalse(props.containsKey("security.protocol"));
    }

    @Test
    void producerFactory() {
        Map<String, Object> props = kafkaProducerConfig.producerConfig();
        ProducerFactory<String, String> producerFactoryRet = kafkaProducerConfig.producerFactory();

        assertNotNull(producerFactoryRet);
        assertEquals(props, producerFactoryRet.getConfigurationProperties());
    }

    @Test
    void kafkaTemplate() {
        ProducerFactory<String, String> producerFactoryRet = kafkaProducerConfig.producerFactory();
        KafkaTemplate template = kafkaProducerConfig.kafkaTemplate(producerFactoryRet);

        assertNotNull(template);
        assertEquals(producerFactoryRet, template.getProducerFactory());
    }
}