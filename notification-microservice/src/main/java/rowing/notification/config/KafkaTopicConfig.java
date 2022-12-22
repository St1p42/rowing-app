package rowing.notification.config;

import lombok.Data;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Data
@Configuration
public class KafkaTopicConfig {

    @Value("${topicName}")
    String topicName;

    @Bean
    public NewTopic  exampleTopic() {
        return TopicBuilder.name(topicName)
                .build();
    }
}
